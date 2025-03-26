package de.greenstones.gsmr.msc.graph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

import lombok.SneakyThrows;

public class Neo4jToJson {
	
	@SneakyThrows
    public static void main(String[] args) {
        // Erstelle eine Verbindung zu Neo4j
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "admin123"));
        Session session = driver.session();

        // Führe eine Cypher-Abfrage aus, die Knoten und Beziehungen zurückgibt
        String query = "MATCH (n)-[r]->(m) RETURN n, r, m";

        try {
            // Ergebnis verarbeiten
            Result result = session.run(query);

            // Listen zur Speicherung von Knoten und Beziehungen
            List<Map<String, Object>> nodes = new ArrayList<>();
            List<Map<String, Object>> relationships = new ArrayList<>();

            // Über die Ergebnisse iterieren
            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next();

                // Hole Knoten n und m
                Node nodeN = record.get("n").asNode();
                Node nodeM = record.get("m").asNode();

                // Füge die Knoten als JSON-ähnliche Maps hinzu
                nodes.add(convertNodeToMap(nodeN));
                nodes.add(convertNodeToMap(nodeM));

                // Hole Beziehung r
                Relationship rel = record.get("r").asRelationship();

                // Füge die Beziehung als JSON-ähnliche Map hinzu
                relationships.add(convertRelationshipToMap(rel));
            }

            // Entferne doppelte Knoten
            nodes = new ArrayList<>(new HashSet<>(nodes));

            // Erstelle das finale JSON-Objekt
            JSONObject jsonResult = new JSONObject();
            jsonResult.put("nodes", new JSONArray(nodes));
            jsonResult.put("relationships", new JSONArray(relationships));

            // Ausgabe des JSON-Ergebnisses
            System.out.println(jsonResult.toString(2));

        } finally {
            // Sitzung schließen
            session.close();
            driver.close();
        }
    }

    // Hilfsmethode zum Konvertieren eines Knotens in eine Map
    private static Map<String, Object> convertNodeToMap(Node node) {
        Map<String, Object> nodeMap = new HashMap<>();
        nodeMap.put("id", node.id());
        nodeMap.put("labels", node.labels());
        nodeMap.put("properties", node.asMap());
        return nodeMap;
    }

    // Hilfsmethode zum Konvertieren einer Beziehung in eine Map
    private static Map<String, Object> convertRelationshipToMap(Relationship relationship) {
        Map<String, Object> relMap = new HashMap<>();
        relMap.put("id", relationship.id());
        relMap.put("startNodeId", relationship.startNodeId());
        relMap.put("endNodeId", relationship.endNodeId());
        relMap.put("type", relationship.type());
        relMap.put("properties", relationship.asMap());
        return relMap;
    }
}
