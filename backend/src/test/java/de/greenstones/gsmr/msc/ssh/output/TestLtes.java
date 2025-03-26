package de.greenstones.gsmr.msc.ssh.output;

import org.junit.jupiter.api.Test;

import de.greenstones.gsmr.msc.parser.MscParser;

class TestLtes {

	@Test
	void test() {

		String s = Output.read("lacs.txt");
		System.err.println(s);

		MscParser p = new MscParser();
		
		
		// System.err.println(p.isCommandExecuted(s));

//		CommandOutput<List<Obj>> o = p.parseList(s, p.objectSeparator("LOCATION AREA"));
//		System.err.println(o.getData());

		// fail("Not yet implemented");

	}

}
