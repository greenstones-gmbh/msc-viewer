package de.greenstones.gsmr.msc.ssh.output;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.greenstones.gsmr.msc.MscParserConfig;
import de.greenstones.gsmr.msc.core.Command.CommandOutput;
import de.greenstones.gsmr.msc.model.Obj;
import de.greenstones.gsmr.msc.parser.MscParser;

class TestGcas {

	@Test
	void test() {

		String s = Output.read("gcas.txt");
		// System.err.println(s);

		MscParser baseParser = new MscParser();
		MscParserConfig config = new MscParserConfig(baseParser);

		// CommandOutput<String> o = p.parseCommandOutput(s, ss -> p.mapContent(ss,
		// "OUTPUT.*?\\n(.*)", a -> a.trim()));

		// System.err.println(o.getData());

		// System.err.println(o.getData());
		// CommandOutput<List<Obj>> o = p.parseList(s, p.objectSeparator("LOCATION
		// AREA"));
		// System.err.println(o.getData().size());

		// fail("Not yet implemented");

		// MscTable table = p.parseTable(o.getData());
		// System.err.println(table);
		//

		CommandOutput<List<Obj>> t = config.gcrefsParser().list(s);
		System.err.println(t.getData());

		t.getData().forEach(
				gca -> System.err.println(gca.getValue("GCAC") + "   " + gca.getValue("GROUP CALL AREA NAME")));

	}

}
