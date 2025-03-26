package de.greenstones.gsmr.msc.ssh.output;

import java.util.List;

import de.greenstones.gsmr.msc.core.Command.CommandOutput;
import de.greenstones.gsmr.msc.parser.MscParser;

public class TestMscObject {
	public static void main(String[] args) {

		String s = Output.read("cells_ide.txt");

		MscParser p = new MscParser();

		CommandOutput<List<String>> o = p.parseCommandOutput(s,
				content -> p.splitObjectList(content, p.objectSeparator("BASE TRANSCEIVER STATION"), c -> c));

		System.err.println("->");
		String c = o.getData().get(0);
		System.err.println(c);
		System.err.println("-<");

		p.parseObj(c);

	}

	
}
