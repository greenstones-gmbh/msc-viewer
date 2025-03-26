package de.greenstones.gsmr.msc.ssh.output;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.greenstones.gsmr.msc.core.Command.CommandOutput;
import de.greenstones.gsmr.msc.model.Obj;
import de.greenstones.gsmr.msc.parser.MscParser;

class TestCells {

	@Test
	void test() {

		String s = Output.read("cells-ide.txt");
		
		MscParser p = new MscParser();
		// System.err.println(p.isCommandExecuted(s));

		CommandOutput<String> output = p.parseCommandOutput(s, c -> c);
		// System.err.println(output.getContent());

		{
			CommandOutput<List<String>> o = p.parseCommandOutput(s,
					content -> p.splitObjectList(content, p.objectSeparator("BASE TRANSCEIVER STATION"), c -> c));

			System.err.println(o.getData().size());

			System.err.println("->");
			System.err.println(o.getData().get(1));
			System.err.println("-<");

			parseCell(o.getData().get(1));

		}

		{
			CommandOutput<List<Obj>> o = p.parseList(s, "BASE TRANSCEIVER STATION");
			// System.err.println(o.getContent());
			assertTrue(o.getData().size() > 0);
		}
		// fail("Not yet implemented");

	}

	public static void parseCell(String o) {

		List<String> lines = Arrays.asList(o.split("\r\n"));
		System.err.println(lines);

	}
}
