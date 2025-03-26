package de.greenstones.gsmr.msc.ssh.output;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import de.greenstones.gsmr.msc.parser.MscParser;

class TestCell {

	MscParser parser = new MscParser();
	
	@Test
	void test() {

		String output = Output.read("cells-65102.txt");
		
		
		Pattern pattern = Pattern.compile("" + //
				".*?LOADING PROGRAM VERSION (.*?)[\r\n]+" + //
				"\\s*MSCi\\s*(\\w[^\\s]*)\\s+(.*?)\\s+(.*?)[\r\n]+" + //
				"(.*)COMMAND EXECUTED.*?", Pattern.DOTALL);

		Matcher matcher = pattern.matcher(output);
		if (matcher.find()) {
			System.err.println(matcher.group(1));
			System.err.println(matcher.group(2));
			System.err.println(matcher.group(3));
			System.err.println(matcher.group(4));
		} 
//				
//		CommandOutput<Obj> one = parser.parseOne(output, parser.objectSeparator("OUTPUT GROUP CALL AREA"));
//		
//		
//		
//		CommandOutput<Obj> list = parser.parseOne(s);
//		
//		System.err.println(list.getData());
//		assertTrue(list.getData()!=null);
//		
	
	}

	public static void parseCell(String o) {

		List<String> lines = Arrays.asList(o.split("\r\n"));
		System.err.println(lines);

	}
}
