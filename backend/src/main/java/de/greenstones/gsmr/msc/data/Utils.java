package de.greenstones.gsmr.msc.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static Map<String, String> createMapping(String mapping) {
        Map<String, String> mappings = new HashMap<>();
        Arrays.asList(mapping.split(",")).stream() //
                .map(s -> s.trim()) //
                .map(s -> s.split("=")) //
                .forEach(s -> {
                    if (s.length == 2) {
                        mappings.put(s[0], s[1]);
                    } else {
                        mappings.put(s[0], s[0]);
                    }
                });
        return mappings;
    }

    public static Map<String, String> map(String mappingString, Map<String, String> extras) {
        if (mappingString == null)
            return extras;
        var mapping = createMapping(mappingString);
        Map<String, String> data = new HashMap<>();
        mapping.forEach((k, v) -> {
            data.put(k, extras.get(v));
        });
        return data;
    }
}
