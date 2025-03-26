package de.greenstones.gsmr.msc.types;

import java.util.List;

import de.greenstones.gsmr.msc.core.Command.CommandOutput;
import de.greenstones.gsmr.msc.model.Obj;
import de.greenstones.gsmr.msc.parser.MscParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class ConfigTypeParserBuilder {

    @NonNull
    MscParser parser;

    ConfigTypeParser p;

    public ConfigTypeParserBuilder() {
        this(new MscParser());
    }

    public ConfigTypeParserBuilder listSeparator(String separator) {
        this.p = new ConfigTypeParser() {
            @Override
            public CommandOutput<List<Obj>> list(String content) {
                return parser.list(content, separator);
            }

            @Override
            public CommandOutput<Obj> obj(String content) {
                return parser.obj(content, separator);
            }

        };
        return this;
    }

    public ConfigTypeParser build() {
        return p;
    }

}
