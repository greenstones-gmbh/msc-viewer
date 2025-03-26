package de.greenstones.gsmr.msc.schemas;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.greenstones.gsmr.msc.types.ConfigType;
import de.greenstones.gsmr.msc.types.ConfigTypeBuilder;
import de.greenstones.gsmr.msc.types.ConfigTypeParser;

/**
 * Configuration for the MSC Viewer
 */
@Configuration
public class GSMRSchema {

	@Autowired
	ConfigTypeParser lacsParser;

	@Autowired
	ConfigTypeParser cellsParser;

	@Autowired
	ConfigTypeParser celllistsParser;

	@Autowired
	ConfigTypeParser ltesParser;

	@Autowired
	ConfigTypeParser gcasParser;

	@Autowired
	ConfigTypeParser gcrefsParser;

	public ConfigType lacs() {

		return new ConfigTypeBuilder()
				.listCommand("ZELO")//
				.detailCommand("ZELO:LAC=${LAC},MCC=${MCC},MNC=${MNC}") //
				.parser(lacsParser) //
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
				.listCommand("ZEPO::IDE")//
				.detailCommand("ZEPO:NO=${NO}") //
				.parser(cellsParser) //
				.defaultId("NO=BTS|NUMBER") //
				.node(n -> n //
						.typeLabel("BTS")
						.propMapping(
								"NAME=BTS|NAME,NUMBER=BTS|NUMBER,LAC=LA|LAC,LA_NAME=LA|NAME,BSC=BSC|NAME,CI,MCC,MNC")
						.color("blue")//
						.nameTemplate("${NUMBER}")
						.relation("lacs", "BELONGS_TO", "LAC=LA|LAC,MCC,MNC")) //

				.frontend(f -> f //
						.title("Cells and BTSs") //
						.column("BTS|NUMBER") //
						.column("BTS|NAME", col -> col.noWidth().linkToDetail()) //
						.column("BSC|NUMBER") //
						.column("BSC|NAME") //
						.column("LA|LAC") //
						.column("LA|NAME", cl -> cl.linkTo("lacs", "LAC=LA|LAC,MCC,MNC"))//
						.columns("MCC", "MNC", "CI")
						.column("BTS ADMINISTRATIVE STATE", col -> col.header("STATE"))

						.detail(d -> d.title("${NAME}", "NAME=BTS|NAME") //
								.defaultGraphQuery()
								.qraphQueryWithTypes("cell-lists", "gcas")
								.qraphQueryWithTypes("cell-lists", "gcrefs")
								.props("BTS|NAME", "BTS|NUMBER")//
								.propSeparator()//
								.prop("LA|LAC")//
								.prop("LA|NAME", pr -> pr.linkTo("lacs", "LAC=LA|LAC,MCC,MNC"))
								.propBlock()//
								.props("MCC", "MNC", "CI")//
								.propBlock()//
								.props("BSC|NAME", "BSC|NUMBER")//
								.propSeparator().prop("BTS ADMINISTRATIVE STATE", prop -> prop.label("STATE"))

								.relatedTab("cell-lists", tab -> tab //
										.column("CLID", col -> col.linkToDetail().header("CELL LIST ID")) //
										.column("CELL LIST NAME", col -> col.noWidth()) //

								)
								.relatedTab("cell-lists,gcas", tab -> tab.title("GCAs"))//
								.relatedTab("cell-lists,gcrefs", tab -> {
									tab.title("Initiator in GCREFs")
											.column("GCREF", col -> col.linkToDetail().width("12em")) //
											.column("GCAC")//
											.column("GCAN", col -> col.noWidth().linkTo("gcas", "GCAC"))//
											.column("GRID")//
											.column("GRPNAME", col -> col.noWidth())//
											.column("STYPE", col -> col.noWidth());
								})

						))
				.build();

	}

	public ConfigType celllists() {

		return new ConfigTypeBuilder()
				.listCommand("ZHCO")//
				.detailCommand("ZHCO:CLNAME=${CLNAME}") //
				.parser(celllistsParser) //
				.defaultId("CLNAME=CELL LIST NAME") //
				.node(n -> n //
						.typeLabel("CELL_LIST")
						.propMapping(
								"CLNAME=CELL LIST NAME,CLID") //
						.useDetails() //
						.color("#aaa")//
						.nameTemplate("${CLID}") //
						.displayName("CELL LIST")
						.relation("cells",
								"CONTAINS", "CELL IDENTIFICATION LIST", "MCC,MNC,LAC,CI")) //

				.frontend(f -> f //
						.title("Cell Lists") //
						.column("CELL LIST ID", col -> col.linkToDetail()) //
						.column("CELL LIST NAME", col -> col.noWidth()) //

						.detail(d -> d //
								.title("${NAME}", "NAME=CELL LIST NAME") //
								.defaultGraphQuery()//
								.prop("CLNAME")//
								.propBlock()//
								.props("CLID")//
								.relatedTab("cells")//

								.relatedTab("gcas", tab -> tab.title("GCAs"))//
								.relatedTab("gcrefs", tab -> {
									tab.title("Initiator in GCREFs")
											.column("GCREF", col -> col.linkToDetail().width("12em")) //
											.column("GCAC")//
											.column("GCAN", col -> col.noWidth().linkTo("gcas", "GCAC"))//
											.column("GRID")//
											.column("GRPNAME", col -> col.noWidth())//
											.column("STYPE", col -> col.noWidth());
								})

						))
				.build();

	}

	public ConfigType ltes() {

		return new ConfigTypeBuilder()
				.listCommand("ZEPJ:ALL")//
				.detailCommand("ZEPJ:ECGI:ECI=${ECI},EMCC=${EMCC},EMNC=${EMNC}") //
				.parser(ltesParser) //
				.defaultId("ECI,EMCC,EMNC") //
				.node(n -> n //
						.typeLabel("LTEConfig")
						.propMapping(
								"ECI,EMCC,EMNC,BTS=BTS NUMBER,NAME,MCC,MNC,CI")
						.color("green")//
						.nameTemplate("${ECI}")
						.displayName("LTE Conf")//
						.relation(
								"cells",
								"CONFIG_FOR", "NO=BTS NUMBER")) //

				.frontend(f -> f //
						.title("LTE Configs") //

						.column("ECI", col -> col.linkToDetail()) //
						.columns("EMCC", "EMNC") //
						.column("BTS NUMBER", col -> col.header("BTS"))//
						.column("NAME", col -> col.header("BTS NAME").noWidth().linkTo("cells", "NO=BTS NUMBER"))//
						.columns("MCC", "MNC", "CI")//

						.detail(d -> d //
								.title("ECI ${ECI}", "ECI")

								.prop("BTS NUMBER", prop -> prop.linkTo("cells", "NO=BTS NUMBER"))//
								.prop("BTS NAME")//
								.propBlock()//
								.props("CI", "MCC", "MNC")//
								.propSeparator()//
								.prop("LAC", prop -> prop.linkTo("lacs", "LAC,MCC,MNC"))//
								.propBlock()//
								.props("ECI", "EMCC", "EMNC")//

						))
				.build();
	}

	public ConfigType gcas() {

		return new ConfigTypeBuilder()
				.listCommand("ZHAO")//
				.detailCommand("ZHAO:GCAC=${GCAC}") //
				.parser(gcasParser) //
				.defaultId("GCAC") //
				.node(n -> n //
						.typeLabel("GCA")
						.propMapping(
								"GCAC,GCAN=GROUP CALL AREA NAME")
						.useDetails() //
						.color("#ffc107")//
						.nameTemplate("${GCAC}") //
						.relation(
								"cell-lists",
								"USES", "LIST OF CELL IDENTIFICATION LISTS", "CLID") //
				) //

				.frontend(f -> f //
						.title("Group Call Areas") //

						.column("GCAC", col -> col.linkToDetail()) //
						.column("GROUP CALL AREA NAME", col -> col.noWidth())//

						.detail(d -> d //

								.title("${GCAN}", "GCAN") //
								.prop("GCAC")//
								.propBlock()//
								.prop("GCAN")//
								.relatedTab("cell-lists", "cells")//
								.relatedTab("gcrefs", tab -> {
									tab.title("GCREFs")
											.column("GCREF", col -> col.linkToDetail().width("12em")) //
											.column("GRID")//
											.column("GRPNAME", col -> col.noWidth())//
											.column("STYPE", col -> col.noWidth());
								})

						))
				.build();
	}

	public ConfigType gcrefs() {

		Map<String, String> stypeMapping = Map.of("VOICE GROUP CALL SERVICE", "VGCS", "VOICE BROADCAST SERVICE", "VBS");

		return new ConfigTypeBuilder()
				.listCommand("ZHGO")//
				.detailCommand("ZHGO:GCREF=${GCREF}:::STYPE=${STYPE}") //
				.parser(gcrefsParser) //
				.id("GCREF,STYPE", "GCREF=${GCREF},STYPE=${STYPE}", null, stypeMapping) //

				// no details
				// .node(n -> n //
				// .typeLabel("GCREF")
				// .propMapping(
				// "GCREF,GCAC=GCA CODE,GROUP=GROUP ID,STYPE")
				// .color("#cff4fc")//
				// .nameTemplate("${GROUP}-${STYPE}") //
				// .relation("gcas",
				// "LINKED_TO", "GCAC=GCA CODE")

				// ) //
				// use details
				.node(n -> n //
						.typeLabel("GCREF")
						.useDetails() //
						.propMapping(
								"GCREF,GCAC,GROUP=GRID,STYPE",
								Map.of("VOICE GROUP CALL SERVICE", "VGCS", "VOICE BROADCAST SERVICE", "VBS"))
						.color("#cff4fc")//
						.nameTemplate("${GROUP}-${STYPE}") //
						.relation("gcas",
								"LINKED_TO", "GCAC")
						.relation("cell-lists",
								"INITIATOR_CELL", "INITIATOR CELL LISTS OF THE GROUP IN THE GROUP CALL AREA",
								"CLNAME=CELL LIST NAME")//
				) //

				.frontend(f -> f //
						.title("Group Call Refs") //

						.column("GCREF", col -> col.linkToDetail().width("12em")) //
						.column("GCA CODE")//
						.column("GCA NAME", col -> col.noWidth().linkTo("gcas", "GCAC=GCA CODE"))//
						.column("GROUP ID")//
						.column("GROUP NAME", col -> col.noWidth())//
						.column("STYPE")

						.detail(d -> d //
								.title("GCREF ${GCREF}", "GCREF") //
								.defaultGraphQuery()//
								.qraphQueryWithTypes("cell-lists", "cells")
								.prop("GCREF")//
								.prop("SERVICE TYPE")//
								.propBlock()//
								.prop("GCAN")//
								.prop("GCAC", prop -> prop.linkTo("gcas", "GCAC"))//
								.propBlock()//
								.props("GROUP NAME", "GROUP ID")//

								.relatedTab("cell-lists,cells", tab -> tab //
										.title("Initiator Cells") //

								)
								.relatedTab("cell-lists", tab -> tab //
										.title("Initiator Cell Lists") //

										.column("CLID", col -> col.linkToDetail().header("CELL LIST ID")) //
										.column("CELL LIST NAME", col -> col.noWidth()) //

								)//

						))
				.build();
	}

	@Bean
	public Map<String, ConfigType> gsmrConfigTypes() {
		Map<String, ConfigType> types = new LinkedHashMap<>();
		types.put("lacs", lacs());
		types.put("cells", cells());
		types.put("cell-lists", celllists());
		types.put("ltes", ltes());
		types.put("gcas", gcas());
		types.put("gcrefs", gcrefs());

		return types;

	}

}
