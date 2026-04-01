package de.greenstones.gsmr.msc.graph;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.neo4j.driver.AccessMode;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Transaction;
import org.springframework.stereotype.Component;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Setter
@Slf4j
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

	public List<Map<String, Object>> list(List<String> querys) {
		return runJob(tx -> {
			List<Record> list = querys.stream().flatMap(q -> tx.run(q).list().stream()).collect(Collectors.toList());
			return ListResults.create(list);
		});
	}

}
