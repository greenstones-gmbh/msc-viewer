package de.greenstones.gsmr.msc.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.greenstones.gsmr.msc.MscParserConfig;
import de.greenstones.gsmr.msc.core.Command.CommandOutput;
import de.greenstones.gsmr.msc.model.Obj;
import de.greenstones.gsmr.msc.model.Props.MultiValueProp;
import de.greenstones.gsmr.msc.model.Props.Prop;
import de.greenstones.gsmr.msc.model.Props.ValueProp;
import de.greenstones.gsmr.msc.model.Sections.PropSection;
import de.greenstones.gsmr.msc.model.Sections.Section;
import de.greenstones.gsmr.msc.model.Sections.TableSection;
import de.greenstones.gsmr.msc.ssh.output.Output;
import de.greenstones.gsmr.msc.types.ConfigTypeParser;

class TestMscParser {

	MscParser baseParser = new MscParser();

	@Test
	void testCells() {

		MscParserConfig config = new MscParserConfig(baseParser);
		ConfigTypeParser parser = config.cellsParser();

		String output = Output.read("cells-ide.txt");
		CommandOutput<List<Obj>> out = parser.list(output);

		assertEquals("17.10-0", out.getVersion());
		assertEquals("DEMO-MSS-02", out.getInfo());

		assertNotNull(out.getData());

		assertEquals(87, out.getData().size());
		Obj obj = out.getData().get(0);
		assertEquals(1, obj.getSections().size());

		PropSection section = assertPropSection(null, 7, obj.getSections().get(0));

		assertMultiValueProp("BTS", "NAME", "BTS53003", "NUMBER", "53003", section.getProps().get(0));
		assertMultiValueProp("BSC", "NAME", "ZUBZBSC01", "NUMBER", "1", section.getProps().get(1));
		assertMultiValueProp("LA", "NAME", "LAC300", "LAC", "300", section.getProps().get(2));
		assertValueProp("MOBILE COUNTRY CODE", "MCC", "998", section.getProps().get(3));
		assertValueProp("MOBILE NETWORK CODE", "MNC", "06", section.getProps().get(4));
		assertValueProp("CELL IDENTITY", "CI", "53003", section.getProps().get(5));
		assertValueProp("BTS ADMINISTRATIVE STATE", null, "UNLOCKED", section.getProps().get(6));
	}

	@Test
	void testCell() {
		String output = Output.read("cells-65102.txt");

		MscParser baseParser = new MscParser();
		MscParserConfig config = new MscParserConfig(baseParser);

		CommandOutput<Obj> out = config.cellsParser().obj(output);

		assertEquals("17.10-0", out.getVersion());
		assertEquals("DEMO-MSS-02", out.getInfo());

		assertNotNull(out.getData());
		Obj obj = out.getData();

		assertEquals(6, obj.getSections().size());

		PropSection section = assertPropSection(null, 7, obj.getSections().get(0));

		assertMultiValueProp("BTS", "NAME", "LDA65102", "NUMBER", "65102", section.getProps().get(0));
		assertMultiValueProp("BSC", "NAME", "-", "NUMBER", "-", section.getProps().get(1));
		assertMultiValueProp("LA", "NAME", "LAC555PANI", "LAC", "555", section.getProps().get(2));
		assertValueProp("MOBILE COUNTRY CODE", "MCC", "998", section.getProps().get(3));
		assertValueProp("MOBILE NETWORK CODE", "MNC", "06", section.getProps().get(4));
		assertValueProp("CELL IDENTITY", "CI", "65102", section.getProps().get(5));
		assertValueProp("BTS ADMINISTRATIVE STATE", null, "LOCKED", section.getProps().get(6));
	}

	@Test
	void testGcas() {
		String output = Output.read("gcas.txt");

		MscParser baseParser = new MscParser();
		MscParserConfig config = new MscParserConfig(baseParser);
		CommandOutput<List<Obj>> out = config.gcasParser().list(output);

		assertEquals("2.12-0", out.getVersion());
		assertEquals("DEMO-MSS-02", out.getInfo());

		assertNotNull(out.getData());

		assertEquals(42, out.getData().size());
		Obj obj = out.getData().get(0);
		assertEquals(1, obj.getSections().size());

		PropSection section = assertPropSection(null, 2, obj.getSections().get(0));

		assertValueProp("GROUP CALL AREA NAME", null, "GCAN82001", section.getProps().get(0));
		assertValueProp("GCAC", null, "82001", section.getProps().get(1));

	}

	@Test
	void testGca() {
		String output = Output.read("gcas-9999.txt");

		MscParserConfig config = new MscParserConfig(baseParser);

		CommandOutput<Obj> out = config.gcasParser().obj(output);

		assertEquals("2.12-0", out.getVersion());
		assertEquals("DEMO-MSS-02", out.getInfo());

		assertNotNull(out.getData());

		Obj obj = out.getData();
		assertEquals(2, obj.getSections().size());

		PropSection section = assertPropSection(null, 2, obj.getSections().get(0));

		assertValueProp("GROUP CALL AREA NAME", "GCAN", "GCAN09999", section.getProps().get(0));
		assertValueProp("GROUP CALL AREA CODE", "GCAC", "9999", section.getProps().get(1));

		assertTableSection("LIST OF CELL IDENTIFICATION LISTS", 2, 1, obj.getSections().get(1));

	}

	public static PropSection assertPropSection(String name, int propCount, Section s) {

		assertNotNull(s);
		assertTrue(s instanceof PropSection, () -> name + " is PropSection, " + s.getClass());
		PropSection ps = (PropSection) s;
		assertEquals(name, ps.getName());
		assertEquals(propCount, ps.getProps().size());

		return ps;

	}

	public static TableSection assertTableSection(String name, int columns, int rows, Section s) {

		assertNotNull(s);
		assertTrue(s instanceof TableSection, () -> name + " is TableSection, " + s.getClass());
		TableSection ps = (TableSection) s;
		assertEquals(name, ps.getName());
		assertEquals(columns, ps.getColumns().size());
		assertEquals(rows, ps.getObjects().size());

		return ps;

	}

	public static void assertValueProp(String name, String shortName, String value, Prop prop) {

		assertNotNull(prop);
		assertTrue(prop instanceof ValueProp, () -> name + " is ValueProp, " + prop.getClass());
		ValueProp mvp = (ValueProp) prop;
		assertEquals(name, mvp.getName());
		assertEquals(shortName, mvp.getShortName());
		assertEquals(value, mvp.getValue());

	}

	public static void assertMultiValueProp(String name, String value1Name, String value1, String value2Name,
			String value2, Prop prop) {

		assertNotNull(prop);
		assertTrue(prop instanceof MultiValueProp, () -> name + " is MultiValueProp");
		MultiValueProp mvp = (MultiValueProp) prop;
		assertEquals(name, mvp.getName());

		assertEquals(2, mvp.getProps().size());

		assertEquals(value1Name, mvp.getProps().get(0).getName());
		assertEquals(value1, mvp.getProps().get(0).getValue());

		assertEquals(value2Name, mvp.getProps().get(1).getName());
		assertEquals(value2, mvp.getProps().get(1).getValue());

	}

	// @Test
	// void testGcas() {
	// String output = Output.read("cells-65102.txt");
	// CommandOutput<Obj> cell = parser.parseOne(output,
	// parser.objectSeparator("BASE TRANSCEIVER STATION"));
	//
	// }

}
