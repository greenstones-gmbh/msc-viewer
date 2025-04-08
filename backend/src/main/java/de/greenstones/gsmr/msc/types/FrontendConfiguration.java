package de.greenstones.gsmr.msc.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class FrontendConfiguration {

    String type;
    ListConfig list;
    DetailConfig detail;
    NodeConfig node;
    MapConfig map;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class MapConfig {
        List<MapLayer> layers;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class MapLayer {
        String layer;
        String title;
        String path;
        String style;
        Integer maxResolution;
        int prio;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class Template {
        String template;
        String mapping;
        Map<String, Integer> paddings;
        Map<String, String> valueMapping;

    }

    @Getter
    @Setter
    static class LinkTo {
        String type;
        Template id;
    }

    @Getter
    @Setter
    static class Prop {
        String prop;
        String label;
        LinkTo linkTo;

        Prop(String prop) {
            this.prop = prop;
        }
    }

    @Getter
    @Setter
    static class TableTab {
        String title;
        List<Column> columns;
        String initialSort;
        String relation;
    }

    @Getter
    @Setter
    static class ListConfig {
        List<Column> columns = new ArrayList<>();
        String title;
        String initialSort;

        public Column column(String prop) {
            Column c = new Column();
            c.prop = prop;
            c.width = "5em";
            columns.add(c);
            return c;
        }
    }

    @Getter
    @Setter
    static class Column {
        String prop;
        String header;
        String width;
        LinkTo linkTo;

        public Column noWidth() {
            width = null;
            return this;
        }

        public void linkTo(String type, String template, String mapping, Map<String, Integer> paddings) {
            linkTo = new LinkTo();

            linkTo.type = type;
            linkTo.id = new Template();
            linkTo.id.template = template;
            linkTo.id.mapping = mapping;
            linkTo.id.paddings = paddings;
        }
    }

    @Getter
    @Setter
    static class DetailConfig {
        Template title;
        List<List<Prop>> props = new ArrayList<>();
        List<String> graphQueries = new ArrayList<>();
        List<TableTab> relatedTables = new ArrayList<>();

        public DetailConfig() {
            props.add(new ArrayList<>());
        }

        public DetailConfig qraphQuery(String query) {
            graphQueries.add(query);
            return this;
        }

        public Prop prop(String prop) {
            Prop p = new Prop(prop);
            props.get(props.size() - 1).add(p);
            return p;
        }

        public void propBlock() {
            props.add(new ArrayList<>());
        }
    }

    @Getter
    @Setter
    static class NodeConfig {
        String typeLabel;
        String typeTitle;
        String valueTitle;
        List<Relation> relations;
        String color;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class Relation {
        String targetType;
        String name;
    }

}
