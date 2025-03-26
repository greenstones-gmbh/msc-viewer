package de.greenstones.gsmr.msc.schemas;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.greenstones.gsmr.msc.parser.MscParser;
import de.greenstones.gsmr.msc.types.ConfigType;
import de.greenstones.gsmr.msc.types.ConfigTypeBuilder;
import de.greenstones.gsmr.msc.types.ConfigTypeParserBuilder;

/**
 * Simple schema for demo purposes
 */
@Configuration
public class SimpleSchema {

	public ConfigType lacs() {

		return new ConfigTypeBuilder()
				.listCommand("ZELO")//
				.detailCommand("ZELO:LAC=${LAC},MCC=${MCC},MNC=${MNC}") //
				.parser(new ConfigTypeParserBuilder(new MscParser()).listSeparator("LOCATION AREA").build()) //
				.defaultId("LAC=LA|LAC,MCC,MNC", Map.of("LAC", 5)) //
				.node(n -> n //
						.typeLabel("LAC")
						.propMapping("NAME=LA|NAME,LAC=LA|LAC,MCC,MNC")
						.color("orange")//
						.nameTemplate("${LAC}")) //
				.frontend(f -> f //
						.title("Location Areas") //
						.column("LA|LAC") //
						.column("LA|NAME", col -> col.noWidth().linkToDetail()) //
						.columns("MCC", "MNC", "AT") //

						.detail(d -> d //
								.title("${NAME}", "NAME=LA|NAME") //
								.prop("LA|LAC")//
								.prop("LA|NAME")//
								.propBlock()//
								.prop("MCC")//
								.prop("MNC")))
				.build();

	}

	public ConfigType cells() {

		return new ConfigTypeBuilder()
				.listCommand("ZEPO::IDE") // MSC command to load the list of cells
				.detailCommand("ZEPO:NO=${NO}") // MSC command to load the cell with the given number
				.parser(new ConfigTypeParserBuilder() // Parser for the list of cells
						.listSeparator("BASE TRANSCEIVER STATION") // Separator for the list of cells
						.build()) //
				.defaultId("NO=BTS|NUMBER") //
				.node(n -> n //
						.typeLabel("BTS")
						.propMapping(
								"NAME=BTS|NAME,NUMBER=BTS|NUMBER,LAC=LA|LAC,LA_NAME=LA|NAME,BSC=BSC|NAME,CI,MCC,MNC")
						.color("blue")//
						.nameTemplate("${NUMBER}")
						.relation("lacs", "BELONGS_TO", "LAC=LA|LAC,MCC,MNC")) //

				.frontend(f -> f //
						.title("Cells and BTSs") // list page title
						.column("BTS|NUMBER") // table columns
						.column("BTS|NAME", col -> col.noWidth().linkToDetail()) //
						.column("BSC|NUMBER") //
						.column("BSC|NAME") //
						.column("LA|LAC") //
						.column("LA|NAME")//
						.columns("MCC", "MNC", "CI")
						.column("BTS ADMINISTRATIVE STATE", col -> col.header("STATE"))

						.detail(d -> d //
								.title("${NAME}", "NAME=BTS|NAME") // detail page title template
								.props("BTS|NAME", "BTS|NUMBER")// properties to display
								.propSeparator()//
								.prop("LA|LAC")//
								.prop("LA|NAME") //
								.propBlock()//
								.props("MCC", "MNC", "CI")//
								.propBlock()//
								.props("BSC|NAME", "BSC|NUMBER")//
								.propSeparator().prop("BTS ADMINISTRATIVE STATE", prop -> prop.label("STATE"))

						))
				.build();

	}

	@Bean
	public Map<String, ConfigType> simpleConfigTypes() {
		Map<String, ConfigType> types = new LinkedHashMap<>();
		types.put("lacs", lacs());
		types.put("cells", cells());
		return types;

	}

}
