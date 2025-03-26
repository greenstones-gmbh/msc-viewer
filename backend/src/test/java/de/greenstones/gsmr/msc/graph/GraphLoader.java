package de.greenstones.gsmr.msc.graph;

import java.util.concurrent.TimeUnit;

import org.neo4j.driver.AccessMode;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.QueryConfig;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Values;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.greenstones.gsmr.msc.MscParserConfig;
import de.greenstones.gsmr.msc.model.Obj;
import de.greenstones.gsmr.msc.parser.MscParser;
import de.greenstones.gsmr.msc.ssh.output.Output;

public class GraphLoader {

	static ObjectMapper mapper = new ObjectMapper();

	static MscParserConfig parser = new MscParserConfig(new MscParser());

	public static void main(String[] args) {

		// TODO Auto-generated method stub
		final String dbUri = "neo4j://localhost";
		final String dbUser = "neo4j";
		final String dbPassword = "admin123";

		try (var driver = GraphDatabase.driver(dbUri, AuthTokens.basic(dbUser, dbPassword))) {
			driver.verifyConnectivity();
			System.out.println("Connection established.");

			// query(driver);

			var sessionConfig = SessionConfig.builder().withDefaultAccessMode(AccessMode.WRITE).withDatabase("neo4j")
					.build();

			try (var session = driver.session(sessionConfig)) {

				deleteAll(session);
				createLacs(session);
				createCells(session);
				createLteConfigs(session);
				createGcas(session);
				createGcrefs(session);

				// var rr = session.executeRead(tx->tx.run("MATCH
				// (p:Person)-[:ACTED_IN]->(m:Movie) return p.name as name, m as
				// movieName").list());
				// rr.stream().forEach(r->System.err.println(r));
				//
				// var rr = session
				// .executeRead(tx -> tx.run("MATCH (p:GCA) return p.gcac as gcac, p.gcan as
				// gcan").list());
				// rr.stream().forEach(r -> System.err.println(r));

				// var res = session.readTransaction(tx -> {
				// return tx.run(" MATCH (p:Person)-[:ACTED_IN]->(m:Movie)\n"
				// + " WHERE m.title = $title // (1)\n"
				// + " RETURN p.name AS name\n"
				// + " LIMIT 10",
				// Values.parameters("title", "Arthur") // (2)
				// )
				// .list(r -> r.get("name").asString());

			}

		}
	}

	private static void createGcas(Session session) {
		String s = Output.read("gcas.txt");

		parser.gcasParser().list(s).getData().stream().forEach(gca -> {

			String gcac = gca.getValue("GCAC");
			String gcan = gca.getValue("GROUP CALL AREA NAME");

			session.executeWrite(tx -> {
				return tx.run("CREATE (p:GCA {GCAC: $gcac, GCAN: $gcan })",
						Values.parameters("gcac", gcac, "gcan", gcan)).consume();
			});

		});
	}

	private static void deleteAll(Session session) {

		session.executeWrite(tx -> {
			return tx.run("MATCH ()-[r]-() DELETE r").consume();
		});

		session.executeWrite(tx -> {
			return tx.run("MATCH (n) DELETE n").consume();
		});

		// session.executeWrite(tx -> {
		// return tx.run("MATCH (:BTS)-[r]-(:LTEConfig) DELETE r").consume();
		// });
		//
		// session.executeWrite(tx -> {
		// return tx.run("MATCH (:BTS)-[r]-(:LAC) DELETE r").consume();
		// });
		//
		// session.executeWrite(tx -> {
		// return tx.run("MATCH (:GCA)-[r]-(:GCA) DELETE r").consume();
		// });
		//
		// session.executeWrite(tx -> {
		// return tx.run("MATCH (:GCA)-[r]-(:GCREF) DELETE r").consume();
		// });
		//
		//
		// session.executeWrite(tx -> {
		// return tx.run("MATCH (n:GCREF) DELETE n").consume();
		// });
		//
		// session.executeWrite(tx -> {
		// return tx.run("MATCH (n:GCA) DELETE n").consume();
		// });
		//
		// session.executeWrite(tx -> {
		// return tx.run("MATCH (n:LTEConfig) DELETE n").consume();
		// });
		//
		// session.executeWrite(tx -> {
		// return tx.run("MATCH (n:BTS) DELETE n").consume();
		// });
		//
		// session.executeWrite(tx -> {
		// return tx.run("MATCH (n:LAC) DELETE n").consume();
		// });
	}

	private static void createLacs(Session session) {

		String s = Output.read("lacs.txt");

		parser.lacsParser().list(s).getData().stream().forEach(o -> {

			String name = o.getValue("LA", "NAME");
			String lac = o.getValue("LA", "LAC");
			String json = getJson(o);

			session.executeWrite(tx -> {
				return tx.run("CREATE (p:LAC {NAME: $name, LAC: $lac, json:$json })",
						Values.parameters("name", name, "lac", lac, "json", json)).consume();
			});

		});
	}

	private static String getJson(Obj o) {
		try {
			return mapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block

		}
		return null;
	}

	private static void createCells(Session session) {

		String s = Output.read("cells-ide.txt");

		parser.cellsParser().list(s).getData().stream().forEach(o -> {

			String name = o.getValue("BTS", "NAME");
			String no = o.getValue("BTS", "NUMBER");

			String lac = o.getValue("LA", "NAME");

			String bsc = o.getValue("BSC", "NAME");

			String ci = o.getValue("CI");
			String mcc = o.getValue("MCC");
			String mnc = o.getValue("MNC");

			String state = o.getValue("BTS ADMINISTRATIVE STATE");

			String json = getJson(o);

			session.executeWrite(tx -> {
				return tx.run(
						"CREATE (p:BTS {NAME: $name, NUMBER: $no, LAC: $lac, BSC: $bsc, CI: $ci, MCC: $mcc, MNC: $mnc, STATE: $state, json:$json })",
						Values.parameters("name", name, "no", no, "lac", lac, "bsc", bsc, "ci", ci, "mcc", mcc, "mnc",
								mnc, "state", state, "json", json))
						.consume();
			});

			session.executeWrite(tx -> {
				return tx.run("match (bts:BTS {NUMBER: $no}), (lac:LAC {NAME: $lac}) CREATE (bts)-[:BELONGS_TO]->(lac)",
						Values.parameters("no", no, "lac", lac)).consume();
			});

		});
	}

	private static void createLteConfigs(Session session) {

		String s = Output.read("ltes.txt");

		parser.ltesParser().list(s).getData().stream().forEach(o -> {

			String ECI = o.getValue("ECI");
			String BTS = o.getValue("BTS NUMBER");

			String EMCC = o.getValue("EMCC");
			String EMNC = o.getValue("EMNC");

			String NAME = o.getValue("NAME");
			String MCC = o.getValue("MCC");
			String MNC = o.getValue("MNC");
			String CI = o.getValue("CI");

			session.executeWrite(tx -> {
				return tx.run(
						"CREATE (p:LTEConfig {ECI: $ECI, EMCC: $EMCC, EMNC: $EMNC, BTS: $BTS, NAME: $NAME, MCC: $MCC, MNC: $MNC, CI: $CI })",
						Values.parameters("ECI", ECI, "EMCC", EMCC, "EMNC", EMNC, "BTS", BTS, "NAME", NAME, "MCC", MCC,
								"MNC", MNC, "CI", CI))
						.consume();
			});

			session.executeWrite(tx -> {
				return tx.run(
						"match (bts:BTS {NUMBER: $BTS}), (lte:LTEConfig {ECI: $ECI,EMCC:$EMCC, EMNC:$EMNC }) CREATE (lte)-[:CONFIG_FOR]->(bts)",
						Values.parameters("BTS", BTS, "ECI", ECI, "EMCC", EMCC, "EMNC", EMNC)).consume();
			});

		});
	}

	private static void createGcrefs(Session session) {

		String s = Output.read("gca-groups.txt");

		parser.gcrefsParser().list(s).getData().stream().forEach(gca -> {

			String ref = gca.getValue("GCREF");
			String gcac = gca.getValue("GCA CODE");

			String group = gca.getValue("GROUP ID");
			String type = gca.getValue("STYPE");

			String json = getJson(gca);

			session.executeWrite(tx -> {
				return tx.run(
						"CREATE (p:GCREF {GCREF: $gcref, GCAC: $gcac, GROUP: $group, STYPE: $type, label:$label, json:$json })",
						Values.parameters("gcref", ref, "gcac", gcac, "group", group, "type", type, "label",
								group + "-" + type, "json", json))
						.consume();
			});

			session.executeWrite(tx -> {
				return tx.run(
						"match (gca:GCA {GCAC: $gcac}), (gcref:GCREF {GCREF: $gcref, STYPE: $type }) CREATE (gcref)-[:LINKED_TO]->(gca)",
						Values.parameters("gcref", ref, "gcac", gcac, "type", type)).consume();
			});

		});
	}

	private static void query(Driver driver) {
		var result = driver.executableQuery("MATCH (p:Person) RETURN p.name AS name")
				.withConfig(QueryConfig.builder().withDatabase("neo4j").build()).execute();

		// Loop through results and do something with them
		var records = result.records();
		records.forEach(r -> {
			System.out.println(r); // or r.get("name").asString()
		});

		// Summary information
		var summary = result.summary();
		System.out.printf("The query %s returned %d records in %d ms.%n", summary.query(), records.size(),
				summary.resultAvailableAfter(TimeUnit.MILLISECONDS));
	}

}
