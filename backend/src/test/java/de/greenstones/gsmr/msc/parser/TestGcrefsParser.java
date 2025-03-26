package de.greenstones.gsmr.msc.parser;

import static de.greenstones.gsmr.msc.parser.AssertUtils.assertMultiValueProp;
import static de.greenstones.gsmr.msc.parser.AssertUtils.assertPropSection;
import static de.greenstones.gsmr.msc.parser.AssertUtils.assertTableSection;
import static de.greenstones.gsmr.msc.parser.AssertUtils.assertValueProp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.greenstones.gsmr.msc.MscParserConfig;
import de.greenstones.gsmr.msc.core.Command.CommandOutput;
import de.greenstones.gsmr.msc.model.Obj;
import de.greenstones.gsmr.msc.model.Sections.PropSection;
import de.greenstones.gsmr.msc.ssh.output.Output;
import de.greenstones.gsmr.msc.types.ConfigTypeParser;

class TestGcrefsParser {

	ConfigTypeParser parser;

	@BeforeEach
	public void init() {
		MscParser baseParser = new MscParser();
		MscParserConfig config = new MscParserConfig(baseParser);
		parser = config.gcrefsParser();
	}

	@Test
	void testGcrefList() {

		String output = Output.readOutput("MSS-01", "ZHGO");
		CommandOutput<List<Obj>> out = parser.list(output);

		assertEquals("2.34-0", out.getVersion());
		assertEquals("DEMO-MSS-02", out.getInfo());

		assertNotNull(out.getData());

		assertEquals(320, out.getData().size());
		Obj obj = out.getData().get(0);
		assertEquals(1, obj.getSections().size());

		PropSection section = assertPropSection(null, 6, obj.getSections().get(0));

		assertValueProp("GCREF", null, "30100555", section.getProps().get(0));

	}

	@Test
	void testGcref() {
		// String output = Output.read("cells-65102.txt");
		String output = Output.readOutput("MSS-01", "ZHGO:GCREF=10000200:::STYPE=VGCS");
		CommandOutput<Obj> out = parser.obj(output);

		assertEquals("2.34-0", out.getVersion());
		assertEquals("DEMO-MSS-02", out.getInfo());

		assertNotNull(out.getData());
		Obj obj = out.getData();

		assertEquals(7, obj.getSections().size());

		PropSection section = assertPropSection(null, 6, obj.getSections().get(0));

		assertValueProp("GROUP CALL REFERENCE", "GCREF", "10000200", section.getProps().get(0));

		assertPropSection("NETWORK PARAMETERS:", 7, obj.getSections().get(1));
		assertPropSection("A INTERFACE PARAMETERS:", 3, obj.getSections().get(2));
		assertTableSection("INITIATOR CELL LISTS OF THE GROUP IN THE GROUP CALL AREA:", 2, 4, obj.getSections().get(3));

	}

}
