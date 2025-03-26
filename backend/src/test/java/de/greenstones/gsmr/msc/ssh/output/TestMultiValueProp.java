package de.greenstones.gsmr.msc.ssh.output;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import de.greenstones.gsmr.msc.model.Props.MultiValueProp;
import de.greenstones.gsmr.msc.model.Props.ValueProp;

class TestMultiValueProp {

	Pattern multiValueProp = Pattern.compile("(\\w+)\\s+?(\\w+) :\\s?(\\w+)\\s+?(\\w+)\\s+?:\\s?(\\w+).*?");

	@Test
	void line1() {

		String line = "BTS   NAME :LDA65102                     NUMBER  :65102";

		// BSC NAME : - NUMBER : -


		MultiValueProp p = parse(multiValueProp, line);
		
		assertNotNull(p);
		assertEquals("BTS", p.getName());
		assertTrue(p.getProps().size()==2);
		assertEquals("NAME",p.getProps().get(0).getName());
		assertEquals("LDA65102",p.getProps().get(0).getValue());
		assertEquals("NUMBER",p.getProps().get(1).getName());
		assertEquals("65102",p.getProps().get(1).getValue());
		
	}
	
	
	
	@Test
	void line2() {

		String line = "BTS   NAME :LDA65102                     NUMBER  :65102";

		// BSC NAME : - NUMBER : -


		MultiValueProp p = parse(multiValueProp, line);
		
		assertNotNull(p);
		assertEquals("BTS", p.getName());
		assertTrue(p.getProps().size()==2);
		assertEquals("NAME",p.getProps().get(0).getName());
		assertEquals("LDA65102",p.getProps().get(0).getValue());
		assertEquals("NUMBER",p.getProps().get(1).getName());
		assertEquals("65102",p.getProps().get(1).getValue());
		
	}
	
	
	@Test
	void emptyLine() {

		String line = "BSC NAME : - NUMBER : -";

		// BSC NAME : - NUMBER : -


		MultiValueProp p = parse(Pattern.compile("(\\w+)\\s+?(\\w+) :\\s?(\\w+|-)\\s+?(\\w+)\\s+?:\\s?(\\w+|-).*?"), line);
		
		assertNotNull(p);
		assertEquals("BSC", p.getName());
		assertTrue(p.getProps().size()==2);
		assertEquals("NAME",p.getProps().get(0).getName());
		assertEquals("-",p.getProps().get(0).getValue());
		assertEquals("NUMBER",p.getProps().get(1).getName());
		assertEquals("-",p.getProps().get(1).getValue());
		
	}

	public static MultiValueProp parse(Pattern pattern, String line) {

		System.err.println(line);
		Matcher m = pattern.matcher(line);
		if (m.find()) {
			
			for (int i = 0; i <= m.groupCount(); i++) {
				System.err.println(i+" "+m.group(i));
			}
			
			MultiValueProp p = new MultiValueProp(m.group(1));
			p.getProps().add(new ValueProp(m.group(2), null, m.group(3)));
			p.getProps().add(new ValueProp(m.group(4), null, m.group(5)));

			return p;
		}

		return null;

	}
}
