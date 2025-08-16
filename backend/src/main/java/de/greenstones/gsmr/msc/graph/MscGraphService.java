package de.greenstones.gsmr.msc.graph;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.neo4j.driver.Query;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.greenstones.gsmr.msc.core.Command.Params;
import de.greenstones.gsmr.msc.core.MscInstance;
import de.greenstones.gsmr.msc.core.MscInstance.Job;
import de.greenstones.gsmr.msc.core.MscInstance.MscRepository;
import de.greenstones.gsmr.msc.core.MscResolver;
import de.greenstones.gsmr.msc.graph.MscGraphService.Graph.Node;
import de.greenstones.gsmr.msc.graph.MscGraphService.Graph.Relation;
import de.greenstones.gsmr.msc.model.Obj;
import de.greenstones.gsmr.msc.types.ConfigType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Component
@Setter
@Slf4j
public class MscGraphService {

	@Autowired
	GraphService graphService;

	@Autowired
	MscResolver mscResolver;

	@Autowired
	ObjectMapper mapper;

	@org.springframework.beans.factory.annotation.Value("${msc-viewer.graph.updateOnStartup:false}")
	boolean updateGraphOnStartup = false;

	@org.springframework.beans.factory.annotation.Value("${msc-viewer.graph.forceReloadOnStartup:false}")
	boolean forceReloadOnStartup = false;

	public List<Map<String, Object>> getNodes(String mscId, String sourceType) {
		MscInstance msc = mscResolver.find(mscId);
		ConfigType stype = msc.getTypes().get(sourceType);
		String q = "MATCH (p:" + fullLabel(mscId, stype.getNode().getLabel()) + " ) return p";
		return graphService.queryMap(q, "p");
	}

	public Map<String, Object> getNode(String mscId, String sourceType, String id) {
		MscInstance msc = mscResolver.find(mscId);
		ConfigType stype = msc.getTypes().get(sourceType);
		String q = "MATCH (p:" + fullLabel(mscId, stype.getNode().getLabel()) + " where p.id='" + id + "'  ) return p";
		List<Map<String, Object>> data = graphService.queryMap(q, "p");
		return data.isEmpty() ? null : data.get(0);
	}

	public List<RelatedNodes> getAllRelatedNodes(String mscId, String sourceType,
			String... targetTypes) {
		return getRelatedNodes(mscId, sourceType, null, targetTypes);
	}

	public List<RelatedNodes> getRelatedNodes(String mscId, String sourceType, String sourceId,
			String... targetTypes) {
		MscInstance msc = mscResolver.find(mscId);
		ConfigType stype = msc.getTypes().get(sourceType);

		List<String> pathNodeTypes = Arrays.asList(targetTypes).stream().map(tt -> msc.getTypes().get(tt))
				.map(tt -> fullLabel(mscId, tt.getNode().getLabel())).collect(Collectors.toList());
		String targetType = pathNodeTypes.remove(pathNodeTypes.size() - 1);
		List<String> pathNodes = pathNodeTypes.stream().map(s -> "(:" + s + ")").collect(Collectors.toList());

		List<String> nodes = new ArrayList<String>();
		nodes.add("(source:" + fullLabel(mscId, stype.getNode().getLabel())
				+ (sourceId != null ? " where source.id='" + sourceId + "'" : "")
				+ ") ");
		nodes.addAll(pathNodes);
		nodes.add("(target:" + targetType + ")");

		String q = "match " + nodes.stream().collect(Collectors.joining("-[]-")) + " return source, target";
		System.err.println(q);
		List<Record> records = graphService.query(q);
		var pairs = records.stream().collect(Collectors.groupingBy(p -> p.get("source").asMap(),
				Collectors.mapping(p -> p.get("target").asMap(), Collectors.toSet())));

		return pairs.entrySet().stream().map(p -> new RelatedNodes(p.getKey(), p.getValue())).toList();

	}

	public static record RelatedNodes(
			Map<String, Object> source,
			Set<Map<String, Object>> targets) {

	};

	public List<Obj> getRelatedData(String mscId, String sourceType, String sourceId, String... targetTypes) {

		MscInstance msc = mscResolver.find(mscId);

		ConfigType stype = msc.getTypes().get(sourceType);

		List<String> pathNodeTypes = Arrays.asList(targetTypes).stream().map(tt -> msc.getTypes().get(tt))
				.map(tt -> fullLabel(mscId, tt.getNode().getLabel())).collect(Collectors.toList());
		String targetType = pathNodeTypes.remove(pathNodeTypes.size() - 1);
		List<String> pathNodes = pathNodeTypes.stream().map(s -> "(:" + s + ")").collect(Collectors.toList());

		List<String> nodes = new ArrayList<String>();
		nodes.add("(source:" + fullLabel(mscId, stype.getNode().getLabel()) + " where source.id='" + sourceId
				+ "') ");
		nodes.addAll(pathNodes);
		nodes.add("(target:" + targetType + ")");

		String q = "match " + nodes.stream().collect(Collectors.joining("-[]-")) + " return target";

		List<Record> query = graphService.query(q);
		List<Obj> list = query.stream().map(p -> p.get("target").get("json").asString()).map(p -> fromJson(p))
				.collect(Collectors.toList());

		return list;

	}

	@EventListener(ApplicationReadyEvent.class)
	public void updateGraphsOnStartup() {
		if (updateGraphOnStartup) {

			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {

			}

			Set<String> mscIds = mscResolver.getMscIds();
			mscIds.stream().forEach(mscId -> {
				try {
					updateGraph(mscId, forceReloadOnStartup);
				} catch (Exception e) {
					log.warn("can't update graph for " + mscId, e);
				}
			});
		}
	}

	public void updateGraph(String mscId, boolean force) {
		log.info(
				"updateGraph msc:{} force:{}", mscId, force);
		MscInstance msc = mscResolver.find(mscId);

		if (force) {
			msc.getMscService().clearCache();
		}

		graphService.runJob(tx -> {
			tx.run(createGraphInfoQuery(mscId, "loading"));
			return null;
		});

		LoadGraphJob job = new LoadGraphJob(force);
		Map<String, List<Obj>> data = msc.execute(job);

		long start = System.currentTimeMillis();

		graphService.runBatchJob(session -> {

			Transaction tx = session.beginTransaction();
			deleteAll(mscId, tx);
			// tx.run(createGraphInfoQuery(mscId, "loading"));
			tx.commit();

			tx = session.beginTransaction();
			createIndexes(mscId, msc.getTypes(), data, tx);
			tx.commit();

			createNodes(mscId, msc.getTypes(), data, session);

			tx = session.beginTransaction();
			Query q = createGraphInfoQuery(mscId, "loaded");
			tx.run(q);
			tx.commit();

			return null;
		});

		long end = System.currentTimeMillis();
		log.info("graph updated msc: {} in {} ms", mscId, end - start);

	}

	private Query createGraphInfoQuery(String mscId, String status) {
		String cmd = """
				MERGE (p:MscGraph {mscId: $mscId})
				ON CREATE SET p.date = $date, p.status = $status
				ON MATCH SET p.date = $date, p.status = $status
				""";

		var v = Values.parameters("mscId", mscId, "date",
				Instant.now().truncatedTo(ChronoUnit.MILLIS).toString(),
				"status", status);

		Query q = new Query(cmd, v);
		return q;
	}

	private void createIndexes(String mscId, Map<String, ConfigType> types, Map<String, List<Obj>> data,
			Transaction tx) {
		data.keySet().stream().forEach(k -> {
			ConfigType configType = types.get(k);
			List<Obj> list = data.get(k);
			if (configType.getNode() != null && list != null) {
				String label = configType.getNode().getLabel();
				String q = "CREATE INDEX " + label + "_id_index IF NOT EXISTS FOR (p:" + label + ") ON (p.id) ";
				log.info("createIndex node {} label: {}", k, q);
				tx.run(q);
			}

		});

	}

	@AllArgsConstructor
	@Slf4j
	public static class LoadGraphJob implements Job<Map<String, List<Obj>>> {

		boolean force;

		@Override
		public Map<String, List<Obj>> run(MscRepository repository) {
			return repository.getTypes().keySet().stream()
					.filter(k -> repository.getTypes().get(k).getNode() != null)
					.collect(Collectors.toMap(k -> k, k -> {
						log.debug("loading data {}", k);
						ConfigType type = repository.getTypes().get(k);
						List<Obj> data = type.getNode().loadData(repository, k, force);
						return data;
					}));
		}
	}

	private void createNodes(String mscId, Map<String, ConfigType> types, Map<String, List<Obj>> data,
			Session session) {
		long start = System.currentTimeMillis();
		Graph graph = createGraph(types, data);
		log.info("createGraph {} in {} msc, nodes: {}, rels:{} ", mscId, System.currentTimeMillis() - start,
				graph.getNodes().size(), graph.getRelations().size());

		start = System.currentTimeMillis();

		Stream<Query> nodeQueries = graph.getNodes().stream().map(n -> n.toCreateQuery(mscId));
		runQueries(session, nodeQueries);

		Stream<Query> relqueries = graph.getRelations().stream().map(n -> n.toCreateQuery(mscId));
		runQueries(session, relqueries);

		// graph.getNodes().stream().map(n -> n.toCreateQuery(mscId)).forEach(q -> {
		// tx.run(q);
		// });

		// graph.getRelations().stream().map(n -> n.toCreateQuery(mscId)).forEach(q -> {
		// tx.run(q);
		// });
		log.info("updateGraph {} in {} msc", mscId, System.currentTimeMillis() - start);

	}

	private void deleteAll(String mscId, Transaction tx) {
		log.debug("delete all");

		tx.run("MATCH (" + label(mscId, "") + ")-[r]-(" + label(mscId, "") + ") DELETE r");
		tx.run("MATCH (n" + label(mscId, "") + ") DELETE n");

		// tx.run("MATCH (p:MscGraph WHERE p.mscId=\"" + mscId + "\") DELETE p");

	}

	protected String label(String mscId, String entity) {
		return entity + ":`" + mscId + "`";
	}

	public Graph createGraph(Map<String, ConfigType> types, Map<String, List<Obj>> data) {

		List<Node> nodes = new ArrayList<Node>();
		List<Relation> relations = new ArrayList<Relation>();

		data.keySet().stream().forEach(k -> {
			ConfigType configType = types.get(k);
			List<Obj> list = data.get(k);
			if (configType.getNode() != null && list != null) {
				String label = configType.getNode().getLabel();
				log.debug("createGraph node {} label: {}", k, label);
				// nodes
				list.stream().forEach(o -> {
					Map<String, String> map = configType.getNode().getPropsMapping().map(o);
					Map<String, String> map1 = new HashMap<String, String>(map);
					String id = configType.getId(o);
					map1.put("id", id);
					map1.put("fulltext", label + "_" + map.values().stream().collect(Collectors.joining("_")));
					map1.put("json", toJson(o));

					nodes.add(new Node(label, map1));

					configType.getNode().getRelations().stream().forEach(r -> {

						Map<String, String> sourceParams = Map.of("id", id);

						ConfigType targetType = types.get(r.getTarget());

						if (r.getTableSection() == null) {
							Params targetIdParams = Params.from(r.getMapping().map(o));
							String targetId = targetType.getIdConverter().toRequestId(targetIdParams);
							Map<String, String> targetParams = Map.of("id", targetId);

							Relation rel = new Relation(configType.getNode().getLabel(),
									targetType.getNode().getLabel(), r.getName(), sourceParams, targetParams);
							relations.add(rel);
						} else {
							o.findTableSection(r.getTableSection()).getObjects().stream().forEach(row -> {
								Params targetParams = Params.from(r.getMapping().map(row));

								Relation rel = new Relation(configType.getNode().getLabel(),
										targetType.getNode().getLabel(), r.getName(), sourceParams, targetParams);
								relations.add(rel);
							});
						}

					});

				});

			}

		});

		return new Graph(nodes, relations);

	}

	@SneakyThrows
	private String toJson(Obj o) {
		return mapper.writeValueAsString(o);
	}

	@SneakyThrows
	private Obj fromJson(String p) {
		return mapper.readValue(p, Obj.class);
	}

	@AllArgsConstructor
	@Getter
	public static class Graph {

		List<Node> nodes;
		List<Relation> relations;

		@AllArgsConstructor
		public static class Node {
			String label;
			Map<String, String> props;

			public Query toCreateQuery(String mscId) {
				String fullText = label + "_" + props.values().stream().collect(Collectors.joining("_"));
				Map<String, String> map = new HashMap<String, String>(props);
				map.put("fulltext", fullText);

				String cmd = "CREATE (p:" + labelWithParamNames(mscId, label, mapKeys(map.keySet(), "")) + " )";
				Query q = new Query(cmd, toValues(map));
				return q;

			}

		}

		@AllArgsConstructor
		public static class Relation {
			String sourceLabel;
			String targetLabel;
			String relationName;
			Map<String, String> sourceProps;
			Map<String, String> targetProps;

			public Query toCreateQuery(String mscId) {

				Map<String, String> source = mapKeys(sourceProps.keySet(), "S_");
				Map<String, String> target = mapKeys(targetProps.keySet(), "T_");

				Map<String, String> all = new HashMap<String, String>();
				all.putAll(remap(sourceProps, "S_"));
				all.putAll(remap(targetProps, "T_"));

				String cmd = "match (source:" + labelWithParamNames(mscId, sourceLabel, source) + ")," //
						+ " (target:" + labelWithParamNames(mscId, targetLabel, target) + ") " //
						+ "CREATE (source)-[:" + relationName + "]->(target)";

				var v = Values.value(all);
				Query q = new Query(cmd, v);

				return q;

			}

		}

	}

	public static String fullLabel(String mscId, String entity) {
		return entity + ":`" + mscId + "`";
	}

	public static String labelWithParamNames(String mscId, String label, Map<String, String> paramMapping) {
		return fullLabel(mscId, label) + " { " + paramMapping.keySet().stream().map(p -> p + ":$" + paramMapping.get(p))
				.collect(Collectors.joining(",")) + " } ";
	}

	public static String labelWithParams(String mscId, String label, Map<String, String> params) {
		return fullLabel(mscId, label) + " { "
				+ params.keySet().stream().map(p -> p + ":'" + params.get(p) + "'").collect(Collectors.joining(","))
				+ " } ";
	}

	public static Value toValues(Map<String, String> v) {
		Map<String, Value> map = new HashMap<String, Value>();
		v.keySet().forEach(key -> {
			map.put(key, Values.value(v.get(key)));
		});

		return Values.value(map);
	}

	public static Map<String, String> mapKeys(Set<String> v, String prefix) {
		return v.stream().collect(Collectors.toMap(k -> k, k -> prefix + k));
	}

	public static Map<String, String> remap(Map<String, String> v, String prefix) {
		return v.keySet().stream().collect(Collectors.toMap(k -> prefix + k, k -> v.get(k)));
	}

	private void runQueries(Session session, Stream<Query> queries) {
		var qs = batchStream(queries, 1000).toList();
		int index = 0;
		for (List<Query> batch : qs) {
			long start = System.currentTimeMillis();
			try (Transaction tx = session.beginTransaction()) {
				for (Query q : batch) {
					tx.run(q);
				}
				tx.commit();
				log.info("Committed batch {}/{} of size {} in {} ms ", index, qs.size(), batch.size(),
						System.currentTimeMillis() - start);
			}
			index++;
		}

	}

	static <T> Stream<List<T>> batchStream(Stream<T> stream, int batchSize) {
		Iterator<T> iterator = stream.iterator();

		return StreamSupport.stream(new Spliterators.AbstractSpliterator<List<T>>(Long.MAX_VALUE, Spliterator.ORDERED) {
			@Override
			public boolean tryAdvance(Consumer<? super List<T>> action) {
				List<T> batch = new ArrayList<>(batchSize);
				int i = 0;
				while (i < batchSize && iterator.hasNext()) {
					batch.add(iterator.next());
					i++;
				}
				if (!batch.isEmpty()) {
					action.accept(batch);
					return true;
				}
				return false;
			}
		}, false);
	}

}
