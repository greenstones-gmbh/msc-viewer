package de.greenstones.gsmr.msc.gis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;

import de.greenstones.gsmr.msc.core.Command.Params;
import de.greenstones.gsmr.msc.graph.GraphNodeService;
import de.greenstones.gsmr.msc.graph.MscGraphService.RelatedNodes;
import de.greenstones.gsmr.msc.parser.RegexpUtils;
import de.greenstones.gsmr.msc.types.LocationConfigurer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class FeatureService {

    Map<String, LocationConfigurer> types;
    GraphNodeService nodeService;
    Map<String, FeatureProvider> featureProviders;

    public List<SimpleFeature> getAll(String type) {
        LocationConfigurer locationConfig = getConfig(type);
        log.info("getAll {}, {}", type, locationConfig.isDerived());
        if (locationConfig.isDerived()) {

            List<RelatedNodes> relatedNodes = nodeService.getAllRelatedNodes(type,
                    locationConfig.getBaseLocationTypePath());

            return getFeaturesFromRelatedNodes(type, relatedNodes);
        } else {
            List<Map<String, Object>> nodes = nodeService.getNodes(type);
            return loadFeatures(type, locationConfig, nodes);
        }
    }

    private LocationConfigurer getConfig(String type) {
        LocationConfigurer locationConfig = types.get(type);
        return locationConfig;
    }

    public List<SimpleFeature> getOne(String type, String id) {

        LocationConfigurer locationConfig = getConfig(type);
        log.info("getOne {}, {}, {}", type, id, locationConfig.isDerived());
        if (locationConfig.isDerived()) {

            List<RelatedNodes> relatedNodes = nodeService.getRelatedNodes(type, id,
                    locationConfig.getBaseLocationTypePath());

            return getFeaturesFromRelatedNodes(type, relatedNodes);
        } else {
            Map<String, Object> node = nodeService.getNode(type, id);
            return loadFeatures(type, locationConfig, Arrays.asList(node));
        }
    }

    // ------------

    protected List<SimpleFeature> getFeaturesFromRelatedNodes(
            String type,
            List<RelatedNodes> relatedNodes) {

        LocationConfigurer locationConfig = getConfig(type);

        String[] baseTypePath = locationConfig.getBaseLocationTypePath();
        String targetType = baseTypePath[baseTypePath.length - 1];

        LocationConfigurer targetTypeConfig = getConfig(targetType);
        if (targetTypeConfig.isDerived()) {
            return relatedNodes.stream().flatMap(rn -> {
                var baseFeatures = getFeaturesFromNodes(targetType, rn.targets());
                return createLocationFeatures(type, rn.source(), baseFeatures).stream();
            }).toList();
        } else {
            return relatedNodes.stream()
                    .flatMap(rn -> {
                        var features = loadFeatures(targetType, targetTypeConfig,
                                rn.targets().stream().toList());
                        return createLocationFeatures(type, rn.source(), features).stream();
                    })
                    .toList();

        }

    }

    protected List<SimpleFeature> getFeaturesFromNodes(String type,
            Set<Map<String, Object>> nodes) {

        return nodes.stream().flatMap(node -> {
            var baseFeatures = getBaseFeatures(type, node);
            return createLocationFeatures(type, node, baseFeatures).stream();
        }).toList();

    }

    protected List<SimpleFeature> getBaseFeatures(String type,
            Map<String, Object> node) {

        LocationConfigurer locationConfig = getConfig(type);

        if (locationConfig.isDerived()) {
            List<RelatedNodes> relatedNodes = nodeService.getRelatedNodes(type, (String) node.get("id"),
                    locationConfig.getBaseLocationTypePath());

            return getFeaturesFromRelatedNodes(type, relatedNodes);
        } else {
            return loadFeatures(type, locationConfig, Arrays.asList(node));
        }

    }

    protected List<SimpleFeature> createLocationFeatures(String type,
            Map<String, Object> node, List<SimpleFeature> baseFeatures) {

        LocationConfigurer locationConfig = getConfig(type);

        if (locationConfig.isDerived()) {

            List<Geometry> geoms = baseFeatures.stream().filter(f -> f != null).map(f -> {
                Geometry g = (Geometry) f.getDefaultGeometry();
                g = "circle".equals(f.getAttribute("subtype")) ? g.buffer((double) f.getAttribute("radius")) : g;
                return g;
            }).toList();

            Geometry union = Utils.union(geoms).buffer(locationConfig.getBuffer());

            union = DouglasPeuckerSimplifier.simplify(union, 300);

            List<SimpleFeature> features = new ArrayList<>();
            SimpleFeature mainFeature = create(type, locationConfig, node, union);
            features.add(mainFeature);
            if (locationConfig.isIncludeBaseFeatures()) {
                features.addAll(baseFeatures);
            }

            return features;
        } else {
            return baseFeatures;
        }
    }

    // ----

    private List<SimpleFeature> loadFeatures(String type, LocationConfigurer locationConfig,
            List<Map<String, Object>> nodes) {
        Set<String> vars = RegexpUtils.getVarNames(locationConfig.getIdTemplate());

        // log.info("loadFeatures {} size:{}", type, nodes.size());

        return nodes.stream().map(n -> {
            var params = n.entrySet().stream().filter(e -> vars.contains(e.getKey()))
                    .collect(Collectors.toMap(k -> k.getKey(), k -> "" + k.getValue()));
            String id = RegexpUtils.interpolate(locationConfig.getIdTemplate(), Params.from(params));

            SimpleFeature location = featureProviders.get(type).find(id);

            if (location == null)
                return null;

            var m = new HashMap<String, Object>(n);
            m.put("radius", location.getAttribute("radius"));
            m.put("subtype", location.getAttribute("subtype"));
            Geometry g1 = (Geometry) location.getDefaultGeometry();
            return create(type, locationConfig, m, g1);

        }).toList();

    }

    public record FeatureDto(Map<String, Object> data, Geometry geometry) {
    }

    @SneakyThrows
    public static SimpleFeature create(String type, LocationConfigurer locationConfig, Map<String, Object> data,
            Geometry geometry) {

        String extraFields = "";
        if (locationConfig.getProps() != null) {
            extraFields = "," + Arrays.asList(locationConfig.getProps()).stream().map(p -> {
                if (p.contains(":"))
                    return p;
                return p + ":String";
            }).collect(Collectors.joining(","));
        }

        String spec = "the_geom:Geometry:srid=4326,subtype:String,_type:String,LINK:String,NAME:String" + extraFields;

        // log.info("spec {}", spec);

        SimpleFeatureType TYPE = DataUtilities.createType(
                "Data",
                spec);
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
        featureBuilder.add(geometry);
        featureBuilder.add(data.get("subtype"));
        featureBuilder.add(type);
        featureBuilder.add("/" + type + "/" + data.get("id"));
        featureBuilder.add(data.get(locationConfig.getNameProp()));

        if (locationConfig.getProps() != null) {
            Arrays.asList(locationConfig.getProps()).forEach(p -> {
                if (p.contains(":")) {
                    String n = p.split(":")[0];
                    featureBuilder.add(data.get(n));
                } else {
                    featureBuilder.add(data.get(p));
                }
            });
        }

        return featureBuilder.buildFeature(null);
    }

}
