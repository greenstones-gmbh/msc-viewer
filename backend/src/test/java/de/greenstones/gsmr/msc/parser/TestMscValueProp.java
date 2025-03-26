package de.greenstones.gsmr.msc.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import de.greenstones.gsmr.msc.model.Props.ValueProp;

class TestMscValueProp {

	static Pattern valuePropWithShortName = Pattern.compile("(.*?) ?\\.+ ?\\((\\w+)\\)\\.* ?: ?(.*)");

	@Test
	void testCells() {

		String s1 = "GROUP CALL AREA NAME ..... (GCAN)...: GCAN09999";
		String s2 = "MOBILE COUNTRY CODE ....................(MCC)... :998  ";
		String s3 = "CELL LIST NAME .................(CLNAME): CLIST2000";

		AssertUtils.assertValueProp("GROUP CALL AREA NAME", "GCAN", "GCAN09999", parse(s1));
		AssertUtils.assertValueProp("MOBILE COUNTRY CODE", "MCC", "998", parse(s2));
		AssertUtils.assertValueProp("CELL LIST NAME", "CLNAME", "CLIST2000", parse(s3));

	}

	public static ValueProp parse(String line) {

		System.err.println(line);
		Matcher m = valuePropWithShortName.matcher(line);
		if (m.find()) {

			for (int i = 0; i <= m.groupCount(); i++) {
				System.err.println(i + " " + m.group(i));
			}

			ValueProp p = new ValueProp(m.group(1).trim(), m.group(2).trim(), m.group(3).trim());

			return p;
		}

		return null;

	}

}
