package de.greenstones.gsmr.msc.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

public class ListResults {

    public static List<Map<String, Object>> create(List<Record> results) {

        List<Map<String, Object>> items = new ArrayList<>();

        results.stream().forEach(r -> {
            Map<String, Object> _values = new HashMap<>();
            r.keys().forEach(k -> {

                Value v = r.get(k);
                if (v.type().name().equals("NODE")) {
                    Node node = v.asNode();
                    _values.put(k, convertNode(node));
                } else if (v.type().name().equals("RELATION")) {
                    Relationship rel = v.asRelationship();
                    // g.add(rel);
                    _values.put(k, convertRelationship(rel));

                } else if (v.type().name().equals("PATH")) {
                    Path path = v.asPath();
                    Map<String, Object> vv = new HashMap<>();
                    path.forEach(s -> {
                        vv.put("start", convertNode(s.start()));
                        vv.put("rel", convertRelationship(s.relationship()));
                        vv.put("end", convertNode(s.end()));
                    });
                    _values.put(k, vv);

                } else {
                    _values.put(k, v.asObject());

                }

            });

            items.add(_values);

        });

        return items;
    }

    public static Map<String, Object> convertNode(Node node) {
        Map<String, Object> nodeMap = new HashMap<>();
        nodeMap.put("id", node.elementId());
        nodeMap.put("labels", node.labels());

        Map<String, Object> asMap = new HashMap<>(node.asMap());
        asMap.remove("fulltext");
        asMap.remove("json");

        nodeMap.put("properties", asMap);
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
