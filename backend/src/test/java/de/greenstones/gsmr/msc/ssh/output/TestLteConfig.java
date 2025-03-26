package de.greenstones.gsmr.msc.ssh.output;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import de.greenstones.gsmr.msc.core.Command.CommandOutput;
import de.greenstones.gsmr.msc.model.Obj;
import de.greenstones.gsmr.msc.parser.MscParser;

class TestLteConfig {

	@Test
	void test() {

		String s = Output.read("ltes.txt");
		// System.err.println(s);

		MscParser p = new MscParser();
		// System.err.println(p.isCommandExecuted(s));

		// CommandOutput<List<Obj>> o = p.parseList(s, p.objectSeparator("OUTPUT LTE
		// CONFIGURATION"));

		
		Function<String, List<Obj>> parseLteConfigs = cc -> p.splitObjectList(cc,
				p.objectSeparator("OUTPUT LTE CONFIGURATION"), p::parseObj);
		
		Function<String, List<Obj>> parseLteConfigs1 = cc -> replace(cc, "---+","", parseLteConfigs);
		
		Function<String, List<Obj>> parseLteConfigs2 = cc -> mapContent(cc, "(.*?)AMOUNT OF OUTPUTTED OBJECTS.*", parseLteConfigs1);
		
		
		CommandOutput<List<Obj>> o = p.parseCommandOutput(s, parseLteConfigs2);

		 System.err.println(o.getData().size());
		 
		 System.err.println(o.getData().get(0));
		 

		
		//System.err.println(ccc);
		// fail("Not yet implemented");

	}

	public static <Content> Content mapContent(String content, String p, Function<String, Content> transform) {
		Pattern pattern = Pattern.compile(p, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			return transform.apply(matcher.group(1));
		} else {
			throw new RuntimeException("can't parse with regexp:" + p + " content:" + content);
		}
	}

	public static <Content> Content replace(String content, String regexp, String v,
			Function<String, Content> transform) {
		String s = content.replaceAll(regexp, v);
		return transform.apply(s);

	}
}
