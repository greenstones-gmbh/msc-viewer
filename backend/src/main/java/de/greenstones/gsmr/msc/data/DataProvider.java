package de.greenstones.gsmr.msc.data;

import java.util.Map;

public interface DataProvider {

    public Map<String, String> find(String key);

    public void init();

    public void clear();

}
