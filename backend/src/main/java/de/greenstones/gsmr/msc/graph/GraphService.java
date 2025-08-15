package de.greenstones.gsmr.msc.graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.neo4j.driver.AccessMode;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.springframework.stereotype.Component;

import lombok.Setter;

@Component
@Setter
public class GraphService {

	@org.springframework.beans.factory.annotation.Value("${msc-viewer.neo4j.uri}")
	String uri = "neo4j://localhost";

	@org.springframework.beans.factory.annotation.Value("${msc-viewer.neo4j.user}")
	String user = "neo4j";

	@org.springframework.beans.factory.annotation.Value("${msc-viewer.neo4j.password}")
	String password = "admin123";

	@org.springframework.beans.factory.annotation.Value("${msc-viewer.neo4j.database}")
	String database = "neo4j";

	public List<Map<String, Object>> queryMap(String query, String varName) {
		return query(query).stream().map(q -> q.get(varName).asMap()).toList();
	}

	public List<org.neo4j.driver.Record> query(String query) {

		try (var driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
			driver.verifyConnectivity();

			var sessionConfig = SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).withDatabase(database)
					.build();

			try (var session = driver.session(sessionConfig)) {
				var rr = session.executeRead(tx -> tx.run(query).list());
				return rr;

			}

		}
	}

	public <Result> Result runJob(Function<Transaction, Result> job) {

		Result result = null;
		try (var driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
			driver.verifyConnectivity();

			var sessionConfig = SessionConfig.builder().withDefaultAccessMode(AccessMode.WRITE).withDatabase(database)
					.build();

			try (var session = driver.session(sessionConfig)) {

				Transaction tx = session.beginTransaction();
				result = job.apply(tx);
				tx.commit();
			}

		}
		return result;
	}

	public <Result> Result runBatchJob(Function<Session, Result> job) {
		Result result = null;
		try (var driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {
			driver.verifyConnectivity();

			var sessionConfig = SessionConfig.builder().withDefaultAccessMode(AccessMode.WRITE).withDatabase(database)
					.build();

			try (var session = driver.session(sessionConfig)) {
				result = job.apply(session);
			}

		}
		return result;
	}

	public Graph graph(String query) {
		return graph(Arrays.asList(query));
	}

	public Graph graph(List<String> querys) {
		return runJob(tx -> {
			List<Record> list = querys.stream().flatMap(q -> tx.run(q).list().stream()).collect(Collectors.toList());
			return Graph.create(list);
		});
	}

	public static class Graph {

		Set<Node> nodes = new HashSet<>();
		Set<Relationship> relationships = new HashSet<>();

		public void add(Node node) {
			nodes.add(node);
		}

		public void add(Relationship rel) {
			relationships.add(rel);
		}

		public Map<String, Object> toMap() {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("nodes", nodes.stream().map(n -> convertNode(n)).collect(Collectors.toList()));
			m.put("relationships",
					relationships.stream().map(n -> convertRelationship(n)).collect(Collectors.toList()));
			return m;
		}

		public String stats() {
			return "graph\nnodes:\n" + nodes.size() + "\nrelationships: " + relationships.size();
		}

		public String desc() {
			String n1 = nodes.stream().map(n -> "\t\t[" + n.labels().iterator().next() + ":"
					+ n.get(n.keys().iterator().next()).asString() + "]").collect(Collectors.joining(",\n"));
			return "graph:\n\tnodes:\n " + n1 + "\n\trelationships: " + relationships.size();
		}

		public static Graph create(List<Record> results) {
			Graph g = new Graph();
			results.stream().forEach(r -> {
				r.keys().forEach(k -> {
					Value v = r.get(k);
					if (v.type().name().equals("NODE")) {
						Node node = v.asNode();
						g.add(node);

					}
					if (v.type().name().equals("RELATION")) {
						Relationship rel = v.asRelationship();
						g.add(rel);

					}
					if (v.type().name().equals("PATH")) {
						Path path = v.asPath();

						path.nodes().forEach(n -> g.add(n));
						path.relationships().forEach(n -> g.add(n));

					}

				});

			});

			return g;
		}

		public static Map<String, Object> convertNode(Node node) {
			Map<String, Object> nodeMap = new HashMap<>();
			nodeMap.put("id", node.elementId());
			nodeMap.put("labels", node.labels());
			nodeMap.put("properties", node.asMap());
			return nodeMap;
		}

		public static Map<String, Object> convertRelationship(Relationship relationship) {
			Map<String, Object> relMap = new HashMap<>();
			relMap.put("id", relationship.elementId());
			relMap.put("startId", relationship.startNodeElementId());
			relMap.put("endId", relationship.endNodeElementId());
			relMap.put("type", relationship.type());
			relMap.put("properties", relationship.asMap());
			return relMap;
		}
	}

}
