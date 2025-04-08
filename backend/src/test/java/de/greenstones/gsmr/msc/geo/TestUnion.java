package de.greenstones.gsmr.msc.geo;

import java.util.List;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.DataUtilities;
import org.geotools.data.geojson.GeoJSONWriter;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.locationtech.jts.geom.Geometry;

import de.greenstones.gsmr.msc.gis.Utils;
import lombok.SneakyThrows;

public class TestUnion {
    @SneakyThrows
    public static void main(String[] args) {

        // cells.put("BTS10000", new CellGeometry(8.631705, 50.172309, 1000));
        // cells.put("BTS10001", new CellGeometry(8.628027, 50.176661, 1500));
        // cells.put("BTS10002", new CellGeometry(8.605501, 50.180791, 2000));
        // cells.put("BTS10003", new CellGeometry(8.636469, 50.188740, 2000));

        final SimpleFeatureType TYPE = DataUtilities.createType(
                "Location",
                "the_geom:Geometry:srid=3857,"
                        + "name:String,"
                        + "number:Integer");

        System.out.println("TYPE:" + TYPE);
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);

        Geometry g1 = Utils.pointFromLatlng(8.631705, 50.172309).buffer(1000);
        Geometry g2 = Utils.pointFromLatlng(8.628027, 50.176661).buffer(1500);
        Geometry g3 = Utils.pointFromLatlng(8.605501, 50.180791).buffer(2000);
        Geometry g4 = Utils.pointFromLatlng(8.636469, 50.188740).buffer(2000);

        List<Geometry> geoms = java.util.Arrays.asList(g1, g2, g3, g4);

        List<SimpleFeature> features = geoms.stream().map(g -> {
            featureBuilder.add(g);
            featureBuilder.add("aaaaa");
            featureBuilder.add(123);
            SimpleFeature feature = featureBuilder.buildFeature(null);
            return feature;
        }).toList();

        SimpleFeatureCollection col = DataUtilities.collection(features);
        String geoJsonString = GeoJSONWriter.toGeoJSON(col);
        System.err.println(geoJsonString);

    }
}
