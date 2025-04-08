package de.greenstones.gsmr.msc.types;

import java.util.Map;
import java.util.stream.Collectors;

import de.greenstones.gsmr.msc.core.Command;
import de.greenstones.gsmr.msc.core.IdConverter;
import de.greenstones.gsmr.msc.core.ParamMapping;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ConfigTypeBuilder {

    String listCommand;
    String detailCommand;
    ConfigTypeParser parser;

    IdConverter idConverter;
    ParamMapping idMapping;
    NodeConfigurer node;
    FrontendConfigurer frontend;

    LocationConfigurer location;

    public ConfigType build() {
        return new ConfigType(new Command<>(listCommand, parser::list), new Command<>(detailCommand, parser::obj),
                idConverter, idMapping, node,
                frontend, location);
    }

    public ConfigTypeBuilder parser(ConfigTypeParser parser) {
        this.parser = parser;
        return this;
    }

    public ConfigTypeBuilder listCommand(String command) {
        this.listCommand = command;
        return this;
    }

    public ConfigTypeBuilder detailCommand(String command) {
        this.detailCommand = command;
        return this;
    }

    public ConfigTypeBuilder id(String idMapping, String idTemplate,
            Map<String, Integer> paddings) {
        this.idMapping = ParamMapping.from(idMapping);
        this.idConverter = new IdConverter(idTemplate, paddings);
        return this;
    }

    public ConfigTypeBuilder id(String idMapping, String idTemplate,
            Map<String, Integer> paddings, Map<String, String> valueMappings) {
        this.idMapping = ParamMapping.from(idMapping);
        this.idMapping.setValueMapping(valueMappings);
        this.idConverter = new IdConverter(idTemplate, paddings);
        return this;
    }

    public ConfigTypeBuilder id(String idMapping, String idTemplate) {
        return id(idMapping, idTemplate, Map.of());
    }

    public ConfigTypeBuilder defaultId(String idMapping, Map<String, Integer> paddings) {
        this.idMapping = ParamMapping.from(idMapping);
        String temp = null;
        if (this.idMapping.getKeys().size() == 1) {
            temp = "${" + this.idMapping.getKeys().get(0) + "}";
        } else {
            temp = this.idMapping.getKeys().stream().map(k -> k + "=${" + k + "}").collect(Collectors.joining(","));
        }
        return id(idMapping, temp, paddings);
    }

    public ConfigTypeBuilder defaultId(String idMapping) {
        return defaultId(idMapping, Map.of());
    }

    public ConfigTypeBuilder node(Customizer<NodeConfigurer> customizer) {
        this.node = new NodeConfigurer();
        customizer.customize(node);
        return this;
    }

    public ConfigTypeBuilder frontend(Customizer<FrontendConfigurer> customizer) {
        this.frontend = new FrontendConfigurer();
        customizer.customize(frontend);
        return this;
    }

    public ConfigTypeBuilder location(Customizer<LocationConfigurer> customizer) {
        this.location = new LocationConfigurer();
        customizer.customize(this.location);
        return this;
    }

}