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

public class SimpleFeatureProvider implements FeatureProvider {

    MathTransform transform;
    boolean isCircle = true;

    Map<String, SimpleFeature> features;
    SimpleFeatureType type;

    @SneakyThrows
    public SimpleFeatureProvider(String crs) {
        CoordinateReferenceSystem sourceCRS = CRS.decode(crs);
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");
        transform = CRS.findMathTransform(sourceCRS, targetCRS, true);
        String spec = "the_geom:Geometry:srid=4326,subtype:String,radius:double,name:String";
        type = DataUtilities.createType(
                "Data",
                spec);
        features = new HashMap<>();

    }

    public void add(String key, double x, double y, double radius) {

        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(type);

        double[] p = transform(x, y);
        Geometry g1 = Utils.pointFromLatlng(p[0], p[1]);

        featureBuilder.add(g1);
        featureBuilder.add(isCircle ? "circle" : "");
        featureBuilder.add(isCircle ? radius : "");
        featureBuilder.add(key);

        SimpleFeature feature = featureBuilder.buildFeature(key);
        features.put(key, feature);

    }

    public SimpleFeature find(String key) {
        return features.get(key);
    }

    public void init() {
    }

    @SneakyThrows
    public double[] transform(double x, double y) {
        double[] srcPt = { x, y };
        double[] destPt = new double[2];
        transform.transform(srcPt, 0, destPt, 0, 1);
        return destPt;

    }

    public static void main(String[] args) {
        SimpleFeatureProvider p = new SimpleFeatureProvider("EPSG:21781");

        p.add("BTS10000", 8.631705, 50.172309, 1000.);
        p.add("BTS10001", 8.628027, 50.176661, 1500);
        p.add("BTS10002", 8.605501, 50.180791, 2000);
        p.add("BTS10003", 8.636469, 50.188740, 2000);

        SimpleFeature simpleFeature = p.find("BTS10000");
        System.err.println(simpleFeature);
        System.err.println("ok");
    }
}
