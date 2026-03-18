package de.greenstones.gsmr.msc.data;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CsvDataProvider implements DataProvider {

    @NonNull
    Path dataFile;

    @NonNull
    String key;

    private Map<String, Map<String, String>> items;

    @Override
    public Map<String, String> find(String key) {
        return items.get(key);
    }

    @Override
    @SneakyThrows
    public void init() {
        List<String[]> alllines = Files.readAllLines(dataFile).stream()
                .map(l -> l.split(","))
                .toList();

        List<String> headers = Arrays.asList(alllines.get(0)).stream().map(h -> h.replaceAll("\"", "")).toList();
        List<String[]> lines = alllines.subList(1, alllines.size());

        log.info("init {} {} lines:{} ", dataFile, headers, lines.size());

        items = new HashMap<>();

        for (int j = 0; j < lines.size(); j++) {
            String[] l = lines.get(j);
            try {
                String kkey = get(headers, l, key);
                Map<String, String> data = new HashMap<>();
                headers.forEach(h -> {
                    data.put(h, get(headers, l, h));
                });
                data.remove(kkey);
                items.put(kkey, data);
            } catch (Exception e) {
                log.warn("can't process line {} {}", j, Arrays.asList(l), e);

            }
        }

        log.info("init.done {} size:{} ", dataFile, items.size());
    }

    @Override
    public void clear() {
        items.clear();
    }

    protected String get(List<String> headers, String[] line, String key) {
        var index = headers.indexOf(key);
        if (index >= line.length)
            return null;
        return line[index].replaceAll("\"", "");
    }

}
