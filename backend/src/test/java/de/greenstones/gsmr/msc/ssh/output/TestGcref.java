package de.greenstones.gsmr.msc.ssh.output;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.greenstones.gsmr.msc.core.Command.CommandOutput;
import de.greenstones.gsmr.msc.model.Obj;
import de.greenstones.gsmr.msc.model.Sections.Section;
import de.greenstones.gsmr.msc.parser.MscParser;

class TestGcref {

	MscParser p = new MscParser();

	@Test
	void test() {

		String s = Output.read("gca-groups-85004507.txt");

		// System.err.println(s);

		// CommandOutput<String> o = p.parseCommandOutput(s, ss -> p.mapContent(ss,
		// "OUTPUT.*?\\n(.*)", a -> a.trim()));

		CommandOutput<String> o = p.parseCommandOutput(s, ss -> ss);

		String c = o.getData();
		System.err.println(c);
//		System.err.println("-----");
//		
//		
		
		
		List<String> sectionContents =p.parseSections(c,"OUTPUT GROUP CALL REFERENCE", "NETWORK PARAMETERS", "A INTERFACE PARAMETERS", "TEMPORARY DATA",
				"INITIATOR CELL LISTS OF THE GROUP IN THE GROUP CALL AREA", "DISPATCHER LIST", "VOICE RECORDER LIST",
				"MSS LIST");
		
		
		Section section1 = p.parsePropSection(sectionContents.get(0));
		System.err.println(section1);
//		
		// System.err.println(parts.get(1));
//		System.err.println("----");
//		
//		
//		
//		
		Obj obj=new Obj();
		obj.getSections().add(p.parsePropSection(sectionContents.get(0)));
		obj.getSections().add(p.parsePropSection(sectionContents.get(1)));
		obj.getSections().add(p.parsePropSection(sectionContents.get(2)));
		obj.getSections().add(p.parsePropSection(sectionContents.get(4)));
		
		obj.getSections().add(p.parseTableSection(sectionContents.get(5)));
		obj.getSections().add(p.parseTableSection(sectionContents.get(6)));
		obj.getSections().add(p.parseTableSection(sectionContents.get(7)));
		
		
		System.err.println(obj);

//		List<String> sectionContents = Arrays.asList(o.split("\r\n\r\n"));
//		List<Section> sections = sectionContents.stream().map(section -> parseSection(section))
//				.collect(Collectors.toList());
//		return new MscOutput.Obj(sections);
//		
//		
//		 OutputTransform.printUnicode(o.getData());
//		 
//		 Obj obj = p.parseObj(o.getData());
		// System.err.println(obj);

		// System.err.println(o.getData());
//		CommandOutput<List<Obj>> o = p.parseList(s, p.objectSeparator("LOCATION AREA"));
//		System.err.println(o.getData().size());

		// fail("Not yet implemented");

//		MscTable table = p.parseTable(o.getData());
//		System.err.println(table);
//		

		// Obj obj = p.parseObj(s);

		// System.err.println(obj);

	}

}
