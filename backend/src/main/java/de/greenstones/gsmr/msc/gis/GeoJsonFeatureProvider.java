package de.greenstones.gsmr.msc.gis;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.data.geojson.GeoJSONReader;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeoJsonFeatureProvider implements FeatureProvider {

    Path dataFile;
    String key;

    GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

    Map<String, SimpleFeature> features;

    @SneakyThrows
    public GeoJsonFeatureProvider(String dataFilePath, String key) {
        this.dataFile = Path.of(dataFilePath);
        this.key = key;
    }

    public SimpleFeature find(String key) {
        SimpleFeature simpleFeature = features.get(key);
        return simpleFeature;
    }

    @SneakyThrows
    public void init() {
        log.info("init {}", dataFile);
        this.features = new HashMap<>();

        List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);

        for (String line : lines) {
            if (line.isBlank())
                continue;
            try {
                SimpleFeature feature = GeoJSONReader.parseFeature(line);
                if (feature == null)
                    continue;
                Geometry geom = (Geometry) feature.getDefaultGeometry();
                if (geom != null) {

                    Coordinate[] coordinates = (geom instanceof Polygon)
                            ? ((Polygon) geom).getExteriorRing().getCoordinates()
                            : geom.getCoordinates();

                    var cs = Arrays.asList(coordinates).stream()
                            .map(Utils::toMercator)
                            .toArray(i -> new Coordinate[i]);

                    feature.setDefaultGeometry(geometryFactory
                            .createPolygon(cs));
                }
                Object keyValue = feature.getAttribute(key);
                if (keyValue != null) {

                    if ("1705".equals(keyValue)) {
                        if ("Schwamendingen".equals(feature.getAttribute("RNS_Name"))) {
                            features.put(keyValue.toString(), feature);
                        }
                    } else
                        features.put(keyValue.toString(), feature);
                }
            } catch (Exception e) {
                log.warn("can't parse line {}", line.substring(0, 100), e);
            }
        }

        log.info("init.done {} {}", dataFile, features.size());
    }

}
