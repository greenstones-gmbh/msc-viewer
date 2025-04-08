package de.greenstones.gsmr.msc.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.greenstones.gsmr.msc.types.FrontendConfiguration.Column;
import de.greenstones.gsmr.msc.types.FrontendConfiguration.DetailConfig;
import de.greenstones.gsmr.msc.types.FrontendConfiguration.LinkTo;
import de.greenstones.gsmr.msc.types.FrontendConfiguration.ListConfig;
import de.greenstones.gsmr.msc.types.FrontendConfiguration.MapConfig;
import de.greenstones.gsmr.msc.types.FrontendConfiguration.MapLayer;
import de.greenstones.gsmr.msc.types.FrontendConfiguration.NodeConfig;
import de.greenstones.gsmr.msc.types.FrontendConfiguration.Prop;
import de.greenstones.gsmr.msc.types.FrontendConfiguration.Relation;
import de.greenstones.gsmr.msc.types.FrontendConfiguration.TableTab;
import de.greenstones.gsmr.msc.types.FrontendConfiguration.Template;
import de.greenstones.gsmr.msc.types.FrontendConfigurer.Link;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FrontendConfigurationBuilder {

    Map<String, ConfigType> types;

    public List<FrontendConfiguration> build() {
        return types.keySet().stream().filter(k -> types.get(k).getFrontend() != null)
                .map(k -> create(k))
                .collect(Collectors.toList());
    }

    public FrontendConfiguration create(String typeKey) {

        ConfigType type = types.get(typeKey);

        var ui = type.getFrontend();

        FrontendConfiguration c = new FrontendConfiguration();
        c.type = typeKey;
        c.list = new ListConfig();
        c.list.title = ui.getTitle();
        c.list.columns = createColumns(ui.getColumns(), typeKey);

        c.list.setInitialSort(ui.getInitialSort() != null ? ui.getInitialSort() : ui.getColumns().get(0).prop);

        if (ui.detail != null) {
            c.detail = new DetailConfig();

            if (ui.detail.titleTemplate != null) {
                c.detail.title = new Template(ui.detail.titleTemplate, ui.detail.titleMapping, Map.of(), Map.of());
            } else {
                c.detail.title = new Template(type.idConverter.getIdTemplate(), type.getIdMapping().getMappingString(),
                        type.idConverter.getPaddings(), Map.of());
            }

            if (type.getNode() != null) {
                if (ui.detail.graphQueries.size() == 0) {
                    c.detail.qraphQuery(
                            "match p=(:" + type.getNode().getLabel()
                                    + ":${MSC} {id:'${ID}'}) return p");
                    c.detail.qraphQuery(
                            "match p=(:" + type.getNode().getLabel()
                                    + ":${MSC} {id:'${ID}'})--(:${MSC}) return p");
                } else {

                    c.detail.graphQueries = ui.detail.graphQueries.stream().map(q -> {

                        if ("@default".equals(q)) {
                            return "match p=(:" + type.getNode().getLabel()
                                    + ":${MSC} {id:'${ID}'})--(:${MSC}) return p";
                        } else if ("@default-self".equals(q)) {
                            return "match p=(:" + type.getNode().getLabel()
                                    + ":${MSC} {id:'${ID}'})return p";
                        } else if (q.startsWith("@types:")) {
                            List<String> typeNames = Arrays.asList(q.replace("@types:",
                                    "").trim().split(","));

                            String path = typeNames.stream().map(tn -> tn.trim()).map(tn -> {
                                ConfigType configType = types.get(tn);
                                return "(:" + configType.getNode().getLabel() + ":${MSC})";
                            }).collect(Collectors.joining("--"));

                            return "match p=(:" + type.getNode().getLabel()
                                    + ":${MSC} {id:'${ID}'})--" + path + " return p";

                        }

                        return q;
                    }).collect(Collectors.toList());
                }
            }

            if (ui.getDetail().getProps() != null) {
                c.detail.props = ui.getDetail().getProps().stream().map(block -> {
                    return block.stream().map(p -> {

                        if (p == null)
                            return null;

                        Prop prop = new Prop(p.getProp());
                        prop.label = p.getLabel();
                        prop.linkTo = createLink(p.getLink(), typeKey);

                        return prop;
                    }).collect(Collectors.toList());
                }).collect(Collectors.toList());
            }

            Set<String> extraTabsRelations = ui.getDetail().getRelatedTabs().stream().map(tab -> tab.getRelation())
                    .collect(Collectors.toSet());

            List<TableTab> defaultTabs = types.entrySet().stream().filter(e -> !e.getKey().equals(typeKey))
                    .filter(e -> e.getValue().getNode()
                            .getRelations().stream().filter(r -> r.getTarget().equals(typeKey)).count() > 0)
                    .filter(rel -> !extraTabsRelations.contains(rel.getKey()))
                    .map(e -> {
                        TableTab tt = new TableTab();
                        ConfigType targetType = e.getValue();
                        tt.title = targetType.getFrontend().getTitle();
                        tt.initialSort = targetType.getFrontend().getInitialSort() != null
                                ? targetType.getFrontend().getInitialSort()
                                : targetType.getFrontend().getColumns().get(0).getProp();

                        tt.relation = e.getKey();
                        tt.columns = createColumns(e.getValue().getFrontend().getColumns(), e.getKey());
                        return tt;
                    }).toList();
            ;

            List<TableTab> extraTabs = ui.getDetail().getRelatedTabs().stream().map(r -> {
                String targetTypeKey = r.getTargetType();
                ConfigType targetType = types.get(targetTypeKey);

                TableTab tt = new TableTab();
                tt.title = r.getTitle() != null ? r.getTitle() : targetType.getFrontend().getTitle();
                tt.initialSort = targetType.getFrontend().getInitialSort() != null
                        ? targetType.getFrontend().getInitialSort()
                        : targetType.getFrontend().getColumns().get(0).getProp();
                tt.relation = r.getRelation();
                tt.columns = createColumns(
                        r.getColumns().isEmpty() ? targetType.getFrontend().getColumns() : r.getColumns(),
                        targetTypeKey);
                return tt;
            }).toList();

            c.detail.relatedTables.addAll(defaultTabs);
            c.detail.relatedTables.addAll(extraTabs);

        }

        if (type.getNode() != null) {
            c.node = new NodeConfig();
            c.node.typeLabel = type.getNode().getLabel();
            c.node.typeTitle = type.getNode().getDisplayName();
            c.node.valueTitle = type.getNode().getNameTemplate();
            c.node.color = type.getNode().getColor();
            c.node.relations = type.getNode().getRelations().stream().map(r -> {
                return new Relation(r.getTarget(), r.getName());
            }).toList();
        }

        if (type.getLocation() != null) {
            c.map = new MapConfig();
            MapLayer l = new MapLayer();
            l.layer = typeKey;
            l.path = typeKey;
            l.title = type.getLocation().getDefaultLayer().getTitle() != null
                    ? type.getLocation().getDefaultLayer().getTitle()
                    : type.getFrontend().getTitle();
            l.style = type.getLocation().getDefaultLayer().getStyle() != null
                    ? type.getLocation().getDefaultLayer().getStyle()
                    : typeKey;
            l.maxResolution = type.getLocation().getDefaultLayer().getMaxResolution();
            l.prio = type.getLocation().getDefaultLayer().getPrio();
            c.map.layers = new ArrayList<>();
            c.map.layers.add(l);

            if (type.getLocation().getExtraLayers() != null) {
                type.getLocation().getExtraLayers().forEach(el -> {
                    MapLayer l1 = new MapLayer();
                    l1.layer = el.getId();
                    l1.path = typeKey;
                    l1.title = el.getTitle();
                    l1.style = el.getStyle() != null
                            ? el.getStyle()
                            : el.getId();
                    l1.maxResolution = el.getMaxResolution();
                    l1.prio = el.getPrio();

                    c.map.layers.add(l1);
                });
            }
        }

        return c;
    }

    private List<Column> createColumns(
            List<de.greenstones.gsmr.msc.types.FrontendConfigurer.Column> columns,
            String typeKey) {
        return columns.stream().map(s -> {
            Column columnProp = new Column();
            columnProp.prop = s.getProp();
            columnProp.width = s.getWidth();
            columnProp.header = s.getHeader();
            columnProp.linkTo = createLink(s.getLink(), typeKey);
            return columnProp;

        }).collect(Collectors.toList());
    }

    private LinkTo createLink(Link link, String type) {

        if (link == null)
            return null;

        if (link.isSelfDetail()) {
            return createSelfDetailLink(type);
        } else {
            return createTargetLink(link);
        }
    }

    private LinkTo createTargetLink(Link l) {
        LinkTo link = new LinkTo();
        link.setType(l.getType());
        ConfigType targetType = types.get(l.getType());
        link.setId(new Template(targetType.idConverter.getIdTemplate(),
                l.getMapping(), targetType.idConverter.getPaddings(), targetType.getIdMapping().getValueMapping()));
        return link;
    }

    private LinkTo createSelfDetailLink(String typeKey) {
        ConfigType type = types.get(typeKey);
        LinkTo link = new LinkTo();
        link.setType(typeKey);
        link.setId(new Template(type.idConverter.getIdTemplate(), type.getIdMapping().getMappingString(),
                type.idConverter.getPaddings(), type.getIdMapping().getValueMapping()));
        return link;
    }

}
