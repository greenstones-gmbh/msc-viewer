package de.greenstones.gsmr.msc.graph;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

public class Neo4jToJson {

    @SneakyThrows
    public static void main(String[] args) {
        // Erstelle eine Verbindung zu Neo4j
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "admin123"));
        Session session = driver.session();

        ObjectMapper m = new ObjectMapper();

        // Führe eine Cypher-Abfrage aus, die Knoten und Beziehungen zurückgibt
        // String query = "MATCH
        // (c:BTS:`MSS-PROD-01`)-[:BELONGS_TO]->(l:LAC:`MSS-PROD-01`) RETURN l.LAC AS
        // LAC, l.NAME AS LA_NAME, COUNT(c) AS CELLS_COUNT ORDER BY CELLS_COUNT DESC";
        // String query = "MATCH (l:LAC:`MSS-PROD-01`) RETURN l";
        // String query = "MATCH (l:LAC:`MSS-PROD-01`) RETURN COUNT(l) AS LACS_COUNT";
        // String query = "MATCH p=(BTS)-[]->(l) RETURN p,l LIMIT 25";
        String query = "MATCH (b:BTS:`MSS-PROD-01`) RETURN b.LAC AS lac, count(b) AS cellCount ORDER BY cellCount DESC";
        try {
            // Ergebnis verarbeiten
            Result result = session.run(query);

            var a = ListResults.create(result.list());
            System.err.println(a);

            String writeValueAsString = m.writeValueAsString(a);
            System.err.println(writeValueAsString);

        } finally {
            // Sitzung schließen
            session.close();
            driver.close();
        }
    }

}
