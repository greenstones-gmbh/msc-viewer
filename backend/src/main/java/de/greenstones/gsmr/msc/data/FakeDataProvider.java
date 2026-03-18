package de.greenstones.gsmr.msc.data;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("fakeDataProvider")
public class FakeDataProvider implements DataProvider {

    @Override
    public Map<String, String> find(String key) {
        return Map.of("P1", "P1_" + key, "P2", "P2_" + key);
    }

    @Override
    public void init() {

    }

    @Override
    public void clear() {

    }

}
