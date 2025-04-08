package de.greenstones.gsmr.msc.gis;

import org.geotools.api.feature.simple.SimpleFeature;

public interface FeatureProvider {

    public SimpleFeature find(String key);

    public void init();

}
