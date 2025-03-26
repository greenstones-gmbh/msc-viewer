package de.greenstones.gsmr.msc;

import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.greenstones.gsmr.msc.core.Command.CommandOutput;
import de.greenstones.gsmr.msc.model.Obj;
import de.greenstones.gsmr.msc.parser.MscParser;
import de.greenstones.gsmr.msc.parser.RegexpUtils;
import de.greenstones.gsmr.msc.types.ConfigTypeParser;
import de.greenstones.gsmr.msc.types.ConfigTypeParserBuilder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Configuration for the MSC Viewer
 */
@Configuration
@RequiredArgsConstructor
@NoArgsConstructor
public class MscParserConfig {

	@Autowired
	@Setter
	@NonNull
	MscParser parser;

	@Bean
	public ConfigTypeParser lacsParser() {
		return new ConfigTypeParserBuilder(parser).listSeparator("LOCATION AREA").build();
	}

	@Bean
	public ConfigTypeParser cellsParser() {
		return new ConfigTypeParserBuilder(parser).listSeparator("BASE TRANSCEIVER STATION").build();
	}

	@Bean
	public ConfigTypeParser celllistsParser() {
		return new ConfigTypeParser() {
			@Override
			public CommandOutput<List<Obj>> list(String content) {
				return parser.parseTable(content);
			}

			@Override
			public CommandOutput<Obj> obj(String content) {
				return parser.parseCommandOutput(content, cc -> {
					List<String> sections = parser.parseSections(cc, "OUTPUT CELL LIST", "CELL IDENTIFICATION LIST");
					Obj obj = new Obj();
					obj.getSections().add(parser.parsePropSection(sections.get(0)));
					obj.getSections().add(parser.parseTableSection(sections.get(1)));
					return obj;
				});
			}
		};

	}

	@Bean
	public ConfigTypeParser ltesParser() {
		return new ConfigTypeParser() {
			@Override
			public CommandOutput<List<Obj>> list(String content) {

				Function<String, List<Obj>> parseLteConfigs = cc -> parser.splitObjectList(cc,
						parser.objectSeparator("OUTPUT LTE CONFIGURATION"), parser::parseObj);

				Function<String, List<Obj>> parseLteConfigs1 = cc -> parser.replace(cc, "---+", "", parseLteConfigs);

				Function<String, List<Obj>> parseLteConfigs2 = cc -> parser.mapContent(cc,
						"(.*?)AMOUNT OF OUTPUTTED OBJECTS.*",
						parseLteConfigs1);

				return parser.parseCommandOutput(content, parseLteConfigs2);

			}

			@Override
			public CommandOutput<Obj> obj(String content) {
				Function<String, List<Obj>> parseLteConfigs = cc -> parser.splitObjectList(cc,
						parser.objectSeparator("OUTPUT LTE CONFIGURATION"), parser::parseObj);

				Function<String, List<Obj>> parseLteConfigs1 = cc -> parser.replace(cc, "---+", "", parseLteConfigs);

				return parser.parseCommandOutput(content, cc -> {
					return parseLteConfigs1.apply(cc).get(0);
				});
			}
		};

	}

	@Bean
	public ConfigTypeParser gcasParser() {

		return new ConfigTypeParser() {
			@Override
			public CommandOutput<List<Obj>> list(String content) {
				return parser.parseTable(content);
			}

			@Override
			public CommandOutput<Obj> obj(String content) {
				return parser.parseCommandOutput(content, cc -> {
					List<String> sections = RegexpUtils.groups(
							"OUTPUT GROUP CALL AREA\r\n\r\n(.*?)\r\n\r\n(LIST OF CELL IDENTIFICATION LISTS:.*?)\r\n$",
							cc);

					Obj obj = new Obj();
					obj.getSections().add(parser.parsePropSection(sections.get(0)));
					obj.getSections().add(parser.parseTableSection(sections.get(1)));
					return obj;
				});
			}
		};

	}

	@Bean
	public ConfigTypeParser gcrefsParser() {

		return new ConfigTypeParser() {
			@Override
			public CommandOutput<List<Obj>> list(String content) {
				return parser.parseTable(content);
			}

			@Override
			public CommandOutput<Obj> obj(String content) {
				return parser.parseCommandOutput(content, cc -> {

					List<String> sectionContents = parser.parseSections(cc, "OUTPUT GROUP CALL REFERENCE",
							"NETWORK PARAMETERS",
							"A INTERFACE PARAMETERS", "TEMPORARY DATA",
							"INITIATOR CELL LISTS OF THE GROUP IN THE GROUP CALL AREA", "DISPATCHER LIST",
							"VOICE RECORDER LIST", "MSS LIST");

					Obj obj = new Obj();
					obj.getSections()
							.add(parser.parsePropSection(sectionContents.get(0).replaceAll("\r\n\r\n", "\r\n")));
					obj.getSections().add(parser.parsePropSection(sectionContents.get(1)));
					obj.getSections().add(parser.parsePropSection(sectionContents.get(2)));
					// obj.getSections().add(parser.parsePropSection(sectionContents.get(3)));

					obj.getSections().add(parser.parseTableSection(sectionContents.get(4)));
					obj.getSections().add(parser.parseTableSection(sectionContents.get(5)));
					obj.getSections().add(parser.parseTableSection(sectionContents.get(6)));
					obj.getSections().add(parser.parseTableSection(sectionContents.get(7)));

					return obj;
				});
			}
		};

	}

}
