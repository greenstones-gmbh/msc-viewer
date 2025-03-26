package de.greenstones.gsmr.msc.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import de.greenstones.gsmr.msc.core.Command.CommandOutput;
import de.greenstones.gsmr.msc.model.Obj;
import de.greenstones.gsmr.msc.model.Props.MultiValueProp;
import de.greenstones.gsmr.msc.model.Props.Prop;
import de.greenstones.gsmr.msc.model.Props.UnknownProp;
import de.greenstones.gsmr.msc.model.Props.ValueProp;
import de.greenstones.gsmr.msc.model.Sections;
import de.greenstones.gsmr.msc.model.Sections.PropSection;
import de.greenstones.gsmr.msc.model.Sections.Section;
import de.greenstones.gsmr.msc.model.Sections.TableSection;
import de.greenstones.gsmr.msc.model.Table;

@Component
public class MscParser {

	public static Pattern valuePropWithShortName = Pattern.compile("(.*?) ?\\.+ ?\\((\\w+)\\)\\.* ?: ?(.*)");

	public CommandOutput<Obj> obj(String output, String separator) {
		return parseOne(output, objectSeparator(separator));
	}

	public CommandOutput<List<Obj>> list(String output, String separator) {
		CommandOutput<List<Obj>> list = parseList(output, objectSeparator(separator));
		return list;
	}

	// ---------------------------------

	public List<String> parseSections(String output, String name, String... sectionNames) {
		String p1 = Arrays.asList(sectionNames).stream().map(g -> "(" + g + ":.*?)\r\n").collect(Collectors.joining());

		List<String> sectionContents = RegexpUtils.groups(name + "\r\n\r\n(.*?)\r\n\r\n" + p1 + "$", output);

		return sectionContents;

	}

	public boolean isCommandExecuted(String output) {
		Pattern p = Pattern.compile(".*COMMAND EXECUTED.*", Pattern.DOTALL);
		return p.matcher(output).matches();
	}

	public <Content> CommandOutput<Content> parseCommandOutput(String output, Function<String, Content> transform) {
		if (isCommandExecuted(output)) {
			Pattern pattern = Pattern.compile("" + //
					".*?LOADING PROGRAM VERSION (.*?)[\r\n]+" + //
					"\\s*MSCi\\s*(\\w[^\\s]*)\\s+(.*?)\\s+(.*?)[\r\n]+" + //
					"(.*)COMMAND EXECUTED.*?", Pattern.DOTALL);

			Matcher matcher = pattern.matcher(output);
			if (matcher.find()) {
				String date = matcher.group(3) + " " + matcher.group(4);
				CommandOutput<Content> o = new CommandOutput<>(matcher.group(1), matcher.group(2), date,
						transform.apply(matcher.group(5)));
				return o;
			} else {
				throw new RuntimeException("can't parse command output: " + output);
			}

		}
		throw new RuntimeException("command not executed: " + output);

	}

	public CommandOutput<List<Obj>> parseTable(String output) {
		CommandOutput<List<Obj>> o = parseCommandOutput(output, ss -> mapContent(ss, "OUTPUT.*?\\n(.*)", a -> {
			Table tableContent = parseTableContent(a);
			return tableContent.toList();
		}));
		return o;
	}

	// parse object list

	public CommandOutput<List<Obj>> parseList(String output, String name) {
		CommandOutput<List<Obj>> o = parseCommandOutput(output, cc -> splitObjectList(cc, name, this::parseObj));
		return o;
	}

	public CommandOutput<Obj> parseOne(String output, String name) {
		CommandOutput<Obj> o = parseCommandOutput(output, cc -> {
			List<Obj> ll = splitObjectList(cc, name, this::parseObj);
			return ll.get(0);
		});
		return o;
	}

	public <Content> List<Content> splitObjectList(String content, String regexp, Function<String, Content> transform) {
		return Arrays.asList(content.split(regexp)).stream().map(x -> x.trim()).filter(x -> !x.isBlank())
				.map(c -> transform.apply(c)).collect(Collectors.toList());
	}

	public String objectSeparator(String name) {
		return "(.*" + name + ".*?)\r\n";
	}

	public Obj parseObj(String o) {
		List<String> sectionContents = Arrays.asList(o.split("\r\n\r\n"));
		List<Section> sections = sectionContents.stream().map(section -> parsePropSection(section))
				.collect(Collectors.toList());
		return new Obj(sections);

	}

	public Sections.PropSection parsePropSection(String section) {
		List<String> lines = Arrays.asList(section.split("\r\n"));

		String name = null;

		if (!lines.isEmpty()) {
			String firstLine = lines.get(0);
			if (isSectionHeader(firstLine)) {
				name = firstLine;
				lines = lines.stream().skip(1).collect(Collectors.toList());
			}

		}

		List<Prop> props = lines.stream().map(this::parseProp).filter(p -> p != null).collect(Collectors.toList());
		return new PropSection(name, props);

	}

	public TableSection parseTableSection(String section) {
		List<String> parts = RegexpUtils.groups("(.*?):\r\n\r\n(.*?)$", section);
		String name = parts.get(0);
		String tablecontent = parts.get(1);
		Table table = parseTableContent(tablecontent);
		return new TableSection(name, table.getHeaders(), table.toList());

	}

	public Prop parseProp(String o) {
		// Pattern valuePropWithShortName = Pattern.compile("(.*?) ?\\.+
		// ?\\((\\w+)\\)\\.* :(.*)");
		Pattern valueProp = Pattern.compile("(.*?) \\.+ :(.*)");
		Pattern multiValueProp = Pattern.compile("(\\w+)\\s+?(\\w+) :\\s?(\\w+|-)\\s+?(\\w+)\\s+?:\\s?(\\w+|-).*?");

		Matcher m = valuePropWithShortName.matcher(o);
		if (m.find()) {
			return new ValueProp(m.group(1).trim(), m.group(2).trim(), m.group(3).trim());
		}

		m = valueProp.matcher(o);
		if (m.find()) {
			return new ValueProp(m.group(1).trim(), null, m.group(2).trim());
		}

		m = multiValueProp.matcher(o);
		if (m.find()) {
			MultiValueProp p = new MultiValueProp(m.group(1));
			p.getProps().add(new ValueProp(m.group(2), null, m.group(3)));
			p.getProps().add(new ValueProp(m.group(4), null, m.group(5)));

			return p;
		}

		return new UnknownProp(o);

	}

	public boolean isSectionHeader(String o) {
		Pattern p = Pattern.compile("(.*?):$");
		Matcher m = p.matcher(o);
		return m.find();
	}

	public <Content> Content mapContent(String content, String p, Function<String, Content> transform) {
		Pattern pattern = Pattern.compile(p, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			return transform.apply(matcher.group(1));
		} else {
			throw new RuntimeException("can't parse with regexp:" + p + " content:" + content);
		}
	}

	public <Content> Content replace(String content, String regexp, String v, Function<String, Content> transform) {
		String s = content.replaceAll(regexp, v);
		return transform.apply(s);

	}

	public Table parseTableContent(String output) {
		String[] lines = output.trim().split("\n");
		for (int i = 0; i < lines.length; i++) {
			lines[i] = lines[i].trim();

		}
		Pattern p = Pattern.compile("(\\S+)");
		Matcher matcher = p.matcher(lines[1]);

		List<Integer[]> splits = new ArrayList<Integer[]>();
		while (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {

				splits.add(new Integer[] { matcher.start(i), matcher.end(i) });
			}
		}

		if (!splits.isEmpty()) {

			Table table = new Table();
			for (Integer[] is : splits) {
				int end = Math.min(is[1], lines[0].length());
				String s = lines[0].substring(is[0], end).trim().replace(":", "");

				table.getHeaders().add(s);
			}

			for (int i = 2; i < lines.length; i++) {
				List<String> row = new ArrayList<String>();
				String line = lines[i];
				// System.err.println(line);
				for (Integer[] is : splits) {
					int end = Math.min(is[1], line.length());
					String s = line.substring(is[0], end).trim();
					row.add(s);
				}
				table.getRows().add(row);
			}

			return table;
		}

		return null;

	}

}
