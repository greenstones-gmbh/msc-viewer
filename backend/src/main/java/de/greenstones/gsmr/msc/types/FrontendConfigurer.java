package de.greenstones.gsmr.msc.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class FrontendConfigurer {

    String title;
    List<Column> columns = new ArrayList<>();
    String initialSort;
    DetailConfigurer detail;

    public static FrontendConfigurer create(String title) {
        FrontendConfigurer ui = new FrontendConfigurer();
        ui.title = title;
        return ui;
    }

    public FrontendConfigurer title(String title) {
        this.title = title;
        return this;
    }

    public FrontendConfigurer column(String prop, Customizer<ColumnConfigurer> customizer) {
        Column c = new Column();
        c.prop = prop;
        c.width = "5em";
        columns.add(c);
        customizer.customize(new ColumnConfigurer(c));
        return this;
    }

    public FrontendConfigurer initialSort(String initialSort) {
        this.initialSort = initialSort;
        return this;
    }

    public FrontendConfigurer column(String prop) {
        return column(prop, Customizer.withDefaults());
    }

    public FrontendConfigurer columns(String... props) {
        for (String p : props) {
            column(p);
        }
        return this;
    }

    public FrontendConfigurer detail(Customizer<DetailConfigurer> customizer) {

        this.detail = new DetailConfigurer();
        customizer.customize(this.detail);
        return this;
    }

    @Getter
    @Setter
    public static class Column {
        String prop;
        String header;
        String width;
        Link link;

    }

    @Getter
    @Setter
    public static class Detail {
        // Template title;

        String titleTemplate;
        String titleMapping;

        List<List<Prop>> props = null;
        List<String> graphQueries = new ArrayList<>();

        List<String[]> relatedTabs = new ArrayList<>();

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Link {
        String type;
        String mapping;

        public boolean isSelfDetail() {
            return "@self".equals(type);
        }

        public static Link toSelfDetail() {
            return new Link("@self", null);
        }

    }

    @Getter
    @Setter
    public static class Prop {
        String prop;
        String label;
        Link link;

    }

    @AllArgsConstructor
    public static class ColumnConfigurer {
        Column column;

        public ColumnConfigurer noWidth() {
            column.width = null;
            return this;
        }

        public ColumnConfigurer width(String width) {
            column.width = width;
            return this;
        }

        public ColumnConfigurer header(String header) {
            column.header = header;
            return this;
        }

        public ColumnConfigurer linkTo(String type, String mapping) {
            column.link = new Link(type, mapping);
            return this;
        }

        public ColumnConfigurer linkToDetail() {
            column.link = Link.toSelfDetail();
            return this;
        }

    }

    @AllArgsConstructor
    public class PropConfigurer {
        Prop prop;

        public PropConfigurer label(String label) {
            prop.label = label;
            return this;
        }

        public PropConfigurer linkTo(String type, String mapping) {
            prop.link = new Link(type, mapping);
            return this;
        }

    }

    @NoArgsConstructor
    @Getter
    public class DetailConfigurer {

        String titleTemplate;
        String titleMapping;

        List<List<Prop>> props = null;
        List<String> graphQueries = new ArrayList<>();
        List<TableTabConfigurer> relatedTabs = new ArrayList<>();

        public DetailConfigurer title(String template, String mapping) {
            this.titleTemplate = template;
            this.titleMapping = mapping;
            return this;
        }

        public DetailConfigurer qraphQuery(String query) {
            this.graphQueries.add(query);
            return this;
        }

        public DetailConfigurer relatedTab(String typePath, Customizer<TableTabConfigurer> customizer) {
            TableTabConfigurer tab = new TableTabConfigurer(typePath.split(","));
            this.relatedTabs.add(tab);
            customizer.customize(tab);
            return this;
        }

        public DetailConfigurer relatedTab(String... typePath) {
            return relatedTab(Arrays.asList(typePath).stream().collect(Collectors.joining(",")),
                    Customizer.withDefaults());
        }

        public DetailConfigurer qraphQueryWithTypes(String... types) {
            this.graphQueries.add("@types:" + Arrays.asList(types).stream().collect(Collectors.joining(",")));
            return this;
        }

        public DetailConfigurer defaultGraphQuery() {
            this.graphQueries.add("@default-self");
            this.graphQueries.add("@default");
            return this;
        }

        public DetailConfigurer props(String... props) {
            for (String p : props) {
                prop(p);
            }
            return this;
        }

        public DetailConfigurer prop(String prop) {
            return prop(prop, Customizer.withDefaults());
        }

        public DetailConfigurer prop(String prop, Customizer<PropConfigurer> customizer) {

            if (this.props == null) {
                this.props = new ArrayList<>();
                this.props.add(new ArrayList<>());
            }

            Prop p = new Prop();
            p.prop = prop;
            this.props.get(this.props.size() - 1).add(p);
            customizer.customize(new PropConfigurer(p));
            return this;
        }

        public DetailConfigurer propBlock() {
            this.props.add(new ArrayList<>());
            return this;
        }

        public DetailConfigurer propSeparator() {
            this.props.get(this.props.size() - 1).add(null);
            return this;
        }

    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @ToString
    public static class TableTabConfigurer {
        @NonNull
        String[] typePath;
        String title;
        List<Column> columns = new ArrayList<>();

        public String getTargetType() {
            return typePath[typePath.length - 1];
        }

        public String getRelation() {
            return Arrays.asList(typePath).stream().collect(Collectors.joining(","));
        }

        public TableTabConfigurer title(String title) {
            this.title = title;
            return this;
        }

        public TableTabConfigurer column(String prop, Customizer<ColumnConfigurer> customizer) {
            Column c = new Column();
            c.prop = prop;
            c.width = "5em";
            columns.add(c);
            customizer.customize(new ColumnConfigurer(c));
            return this;
        }

        public TableTabConfigurer column(String prop) {
            return column(prop, Customizer.withDefaults());
        }
    }
}