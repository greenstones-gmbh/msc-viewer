package de.greenstones.gsmr.msc.parser;

import static de.greenstones.gsmr.msc.parser.AssertUtils.assertMultiValueProp;
import static de.greenstones.gsmr.msc.parser.AssertUtils.assertPropSection;
import static de.greenstones.gsmr.msc.parser.AssertUtils.assertValueProp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.greenstones.gsmr.msc.MscParserConfig;
import de.greenstones.gsmr.msc.core.Command.CommandOutput;
import de.greenstones.gsmr.msc.model.Obj;
import de.greenstones.gsmr.msc.model.Sections.PropSection;
import de.greenstones.gsmr.msc.ssh.output.Output;
import de.greenstones.gsmr.msc.types.ConfigTypeParser;

class TestCellsParser {

	ConfigTypeParser parser;

	@BeforeEach
	public void init() {
		MscParser baseParser = new MscParser();
		MscParserConfig config = new MscParserConfig(baseParser);
		parser = config.cellsParser();
	}

	@Test
	void testCells() {

		String output = Output.readOutput("MSS-01", "ZEPO::IDE");
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
		// String output = Output.read("cells-65102.txt");
		String output = Output.readOutput("MSS-01", "ZEPO:NO=1000");
		CommandOutput<Obj> out = parser.obj(output);

		assertEquals("17.10-0", out.getVersion());
		assertEquals("DEMO-MSS-02", out.getInfo());

		assertNotNull(out.getData());
		Obj obj = out.getData();

		assertEquals(6, obj.getSections().size());

		PropSection section = assertPropSection(null, 7, obj.getSections().get(0));

		assertMultiValueProp("BTS", "NAME", "BTS1000", "NUMBER", "1000", section.getProps().get(0));
		assertMultiValueProp("BSC", "NAME", "ZUBZBSC01", "NUMBER", "1", section.getProps().get(1));
		assertMultiValueProp("LA", "NAME",
				"LAC3333T", "LAC", "3333", section.getProps().get(2));
		assertValueProp("MOBILE COUNTRY CODE", "MCC", "999", section.getProps().get(3));
		assertValueProp("MOBILE NETWORK CODE", "MNC", "99", section.getProps().get(4));
		assertValueProp("CELL IDENTITY", "CI", "1000", section.getProps().get(5));
		assertValueProp("BTS ADMINISTRATIVE STATE", null, "UNLOCKED", section.getProps().get(6));
	}

}
