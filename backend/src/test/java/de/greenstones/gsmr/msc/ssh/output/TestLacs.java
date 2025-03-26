package de.greenstones.gsmr.msc.ssh.output;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.greenstones.gsmr.msc.core.Command.CommandOutput;
import de.greenstones.gsmr.msc.model.Obj;
import de.greenstones.gsmr.msc.parser.MscParser;

class TestLacs {

	@Test
	void test() {

		String s = Output.read("lacs.txt");
		System.err.println(s);

		MscParser p = new MscParser();



		CommandOutput<List<Obj>> o = p.parseList(s, p.objectSeparator("LOCATION AREA"));
		System.err.println(o.getData().size());

		// fail("Not yet implemented");

	}

}
