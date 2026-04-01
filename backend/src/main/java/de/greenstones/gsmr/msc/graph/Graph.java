package de.greenstones.gsmr.msc.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

public class Graph {

    Set<Node> nodes = new HashSet<>();
    Set<Relationship> relationships = new HashSet<>();
    Map<String, Object> values = new HashMap<>();

    public void add(Node node) {
        nodes.add(node);
    }

    public void add(Relationship rel) {
        relationships.add(rel);
    }

    public void add(String key, Value v) {
        values.put(key, v.asObject());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("nodes", nodes.stream().map(n -> convertNode(n)).collect(Collectors.toList()));
        m.put("relationships",
                relationships.stream().map(n -> convertRelationship(n)).collect(Collectors.toList()));
        m.put("values",
                values);
        return m;
    }

    public String stats() {
        return "graph\nnodes:\n" + nodes.size() + "\nrelationships: " + relationships.size() + "\nvalues:"
                + values.size();
    }

    public String desc() {
        String n1 = nodes.stream().map(n -> "\t\t[" + n.labels().iterator().next() + ":"
                + n.get(n.keys().iterator().next()).asString() + "]").collect(Collectors.joining(",\n"));
        return "graph:\n\tnodes:\n " + n1 + "\n\trelationships: " + relationships.size() + "\n\tvalues: " + values;
    }

    public static Graph create(List<Record> results) {
        Graph g = new Graph();
        results.stream().forEach(r -> {
            r.keys().forEach(k -> {
                Value v = r.get(k);
                if (v.type().name().equals("NODE")) {
                    Node node = v.asNode();
                    g.add(node);

                } else if (v.type().name().equals("RELATION")) {
                    Relationship rel = v.asRelationship();
                    g.add(rel);

                } else if (v.type().name().equals("PATH")) {
                    Path path = v.asPath();

                    path.nodes().forEach(n -> g.add(n));
                    path.relationships().forEach(n -> g.add(n));

                } else {
                    g.add(k, v);

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