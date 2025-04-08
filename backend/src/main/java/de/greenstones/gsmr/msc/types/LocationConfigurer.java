package de.greenstones.gsmr.msc.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.locationtech.jts.geom.Geometry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class LocationConfigurer {

    String nameProp = "NAME";

    Layer defaultLayer = new Layer();
    List<Layer> extraLayers = new ArrayList<>();

    boolean loader;
    String idTemplate;

    public LocationConfigurer extraLayer(String id, String layerTitle, String style, Integer maxResolution, int prio) {
        this.extraLayers.add(new Layer(id, layerTitle, style, maxResolution, prio));
        return this;
    }

    public LocationConfigurer layerTitle(String title) {
        this.defaultLayer.title = title;
        return this;
    }

    public LocationConfigurer layerPrio(int prio) {
        this.defaultLayer.prio = prio;
        return this;
    }

    public LocationConfigurer layerStyle(String style) {
        this.defaultLayer.style = style;
        return this;
    }

    public LocationConfigurer layerMaxResolution(Integer maxResolution) {
        this.defaultLayer.maxResolution = maxResolution;
        return this;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Layer {
        String id;
        String title;
        String style;
        Integer maxResolution = 3000;
        int prio = 100;
    }

    // ---

    public LocationConfigurer useFeatureProvider() {
        this.loader = true;
        return this;
    }

    public LocationConfigurer idTemplate(String idTemplate) {
        this.idTemplate = idTemplate;
        return this;
    }

    // -----

    String[] baseLocationTypePath;
    GeometryBuilder geometryBuilder;

    Double buffer = 400.;
    Double simplifyDistance = 300.;

    List<String[]> includeTypes = new ArrayList<>();
    boolean includeBaseFeatures = true;

    String[] props;

    public LocationConfigurer baseLocationType(String... baseLocationTypePath) {
        this.baseLocationTypePath = baseLocationTypePath;
        return this;
    }

    public LocationConfigurer includeBaseFeatures(boolean includeBaseFeatures) {
        this.includeBaseFeatures = includeBaseFeatures;
        return this;
    }

    public LocationConfigurer geomrty(GeometryBuilder geometryBuilder) {
        this.geometryBuilder = geometryBuilder;
        return this;
    }

    public LocationConfigurer buffer(Double buffer) {
        this.buffer = buffer;
        return this;
    }

    public LocationConfigurer props(String... props) {
        this.props = props;
        return this;
    }

    public LocationConfigurer nameProp(String nameProp) {
        this.nameProp = nameProp;
        return this;
    }

    public LocationConfigurer simplifyDistance(Double simplifyDistance) {
        this.simplifyDistance = simplifyDistance;
        return this;
    }

    public LocationConfigurer includeTypes(String... includeTypes) {
        this.includeTypes.add(includeTypes);
        return this;
    }

    interface GeometryBuilder {
        Geometry build(Collection<Geometry> geometries);
    }

    public boolean isDerived() {
        return !loader;
    }
}