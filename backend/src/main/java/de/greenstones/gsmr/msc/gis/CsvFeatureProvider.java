package de.greenstones.gsmr.msc.gis;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;

import lombok.SneakyThrows;

public class CsvFeatureProvider implements FeatureProvider {

    Path dataFile;
    MathTransform transform;

    String key;
    String x;
    String y;
    boolean isCircle = true;
    double radius = 3500;

    Map<String, SimpleFeature> features;

    @SneakyThrows
    public CsvFeatureProvider(String dataFilePath, String crs, String key, String x, String y) {
        CoordinateReferenceSystem sourceCRS = CRS.decode(crs);
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");
        transform = CRS.findMathTransform(sourceCRS, targetCRS, true);
        this.dataFile = Path.of(dataFilePath);
        this.key = key;
        this.x = x;
        this.y = y;

    }

    public SimpleFeature find(String key) {
        return features.get(key);
    }

    @SneakyThrows
    public void init() {
        List<String[]> alllines = Files.readAllLines(dataFile).stream()
                .map(l -> l.split(","))
                .toList();

        List<String> headers = Arrays.asList(alllines.get(0)).stream().map(h -> h.replaceAll("\"", "")).toList();
        List<String[]> lines = alllines.subList(1, alllines.size());
        // for (int i = 0; i < headers.size(); i++) {
        // System.err.println(i + "\t" + headers.get(i));
        // }

        String spec = "the_geom:Geometry:srid=4326,subtype:String,radius:double,"
                + headers.stream().map(h -> h + ":String").collect(Collectors.joining(","));

        // log.info("spec {}", spec);

        SimpleFeatureType TYPE = DataUtilities.createType(
                "Data",
                spec);
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);

        this.features = new HashMap<>();

        lines.forEach(l -> {
            try {
                String kkey = get(headers, l, key);

                double xx = Double.parseDouble(get(headers, l, x));
                double yy = Double.parseDouble(get(headers, l, y));

                double[] p = transform(xx, yy);
                Geometry g1 = Utils.pointFromLatlng(p[1], p[0]);

                featureBuilder.add(g1);
                featureBuilder.add(isCircle ? "circle" : "");
                featureBuilder.add(isCircle ? radius : "");

                headers.forEach(h -> {
                    featureBuilder.add(get(headers, l, h));
                });

                SimpleFeature feature = featureBuilder.buildFeature(kkey);
                features.put(kkey, feature);

            } catch (Exception e) {
                // System.err.println("Error: " + Arrays.asList(l));
                // System.err.println(e);
                // e.printStackTrace();

            }

        });

    }

    protected String get(List<String> headers, String[] line, String key) {
        return line[headers.indexOf(key)].replaceAll("\"", "");
    }

    @SneakyThrows
    public double[] transform(double x, double y) {
        double[] srcPt = { x, y };
        double[] destPt = new double[2];
        transform.transform(srcPt, 0, destPt, 0, 1);
        return destPt;

    }

}
