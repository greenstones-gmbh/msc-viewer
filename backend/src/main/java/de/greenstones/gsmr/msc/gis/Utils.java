package de.greenstones.gsmr.msc.gis;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.data.DataUtilities;
import org.geotools.data.geojson.GeoJSONWriter;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import lombok.SneakyThrows;

public class Utils {

    static GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

    @SneakyThrows
    public static double[] latlngToMercator(double[] srcPt) {
        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857");
        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);
        double[] destPt = new double[2];
        transform.transform(srcPt, 0, destPt, 0, 1);
        return destPt;
    }

    @SneakyThrows
    public static double[] latlngToMercator(double y, double x) {
        double[] srcPt = { x, y };
        return latlngToMercator(srcPt);
    }

    @SneakyThrows
    public static Point pointFromLatlng(double x, double y) {
        double[] coords = latlngToMercator(x, y);
        Point point = geometryFactory.createPoint(new Coordinate(coords[1], coords[0]));
        return point;
    }

    // @SneakyThrows
    // public static List<Point> createFromLatlng(List<double[]> points) {
    // return points.stream().map(c -> pointFromLatlng(c[0], c[1])).toList();
    // }

    static Geometry union(Collection<Geometry> geometryCollection) {

        GeometryCollection geometryCollection2 = geometryFactory
                .createGeometryCollection(geometryCollection.toArray(new Geometry[0]));

        return geometryCollection2.union();
    }

    @SneakyThrows
    public static String toGeoJSON(List<SimpleFeature> features) {
        SimpleFeatureCollection col = DataUtilities.collection(features);
        String geoJsonString = GeoJSONWriter.toGeoJSON(col);
        return geoJsonString;
    }

}
