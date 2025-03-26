package de.greenstones.gsmr.msc.ssh.output;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.greenstones.gsmr.msc.core.Command.CommandOutput;
import de.greenstones.gsmr.msc.model.Obj;
import de.greenstones.gsmr.msc.model.Sections.TableSection;
import de.greenstones.gsmr.msc.model.Table;
import de.greenstones.gsmr.msc.parser.MscParser;
import de.greenstones.gsmr.msc.parser.RegexpUtils;

class TestCellLists {

	@Test
	void test() {

		String s = Output.read("cell_lists.txt");
		// System.err.println(s);

		MscParser p = new MscParser();

		// CommandOutput<String> o = p.parseCommandOutput(s, ss -> p.mapContent(ss,
		// "OUTPUT.*?\\n(.*)", a -> a.trim()));

		// System.err.println(o.getData());

		// System.err.println(o.getData());
//		CommandOutput<List<Obj>> o = p.parseList(s, p.objectSeparator("LOCATION AREA"));
//		System.err.println(o.getData().size());

		// fail("Not yet implemented");

//		MscTable table = p.parseTable(o.getData());
//		System.err.println(table);
//		

		// CommandOutput<List<Obj>> t = p.cellLists(s);
		// System.err.println(t.getData());

		// CommandOutput<Obj> t1 = p.cellList(Output.read("cell_lists-2000.txt"));
		// System.err.println(t1.getData());

		// t.getData().forEach(gca->System.err.println(gca.getValue("GCAC")+" "+
		// gca.getValue("GROUP CALL AREA NAME")));

		CommandOutput<Obj> o = p.parseCommandOutput(Output.read("cell_lists-2000.txt"), cc -> {

			List<String> sections = p.parseSections(cc, "OUTPUT CELL LIST", "CELL IDENTIFICATION LIST");

			Obj obj = new Obj();
			// obj.getSections().add(parsePropSection(sections.get(0)));
			// obj.getSections().add(parseTableSection(sections.get(1)));

			String ss = sections.get(1);
			System.err.println(ss);

			
			List<String> parts = RegexpUtils.groups("(.*?):\r\n\r\n(.*?)$", ss);
			String name = parts.get(0);
			String tablecontent = parts.get(1);
			
			System.err.println(tablecontent);
			
			Table table = p.parseTableContent(tablecontent);
			
			TableSection tableSection = p.parseTableSection(ss);
			System.err.println(tableSection);

			return obj;
		});

	}

}
