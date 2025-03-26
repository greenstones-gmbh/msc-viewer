package de.greenstones.gsmr.msc.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.greenstones.gsmr.msc.model.Props.MultiValueProp;
import de.greenstones.gsmr.msc.model.Props.Prop;
import de.greenstones.gsmr.msc.model.Props.ValueProp;
import de.greenstones.gsmr.msc.model.Sections.PropSection;
import de.greenstones.gsmr.msc.model.Sections.Section;
import de.greenstones.gsmr.msc.model.Sections.TableSection;

public class AssertUtils {
    public static PropSection assertPropSection(String name, int propCount, Section s) {

        assertNotNull(s);
        assertTrue(s instanceof PropSection, () -> name + " is PropSection, " + s.getClass());
        PropSection ps = (PropSection) s;
        assertEquals(name, ps.getName());
        assertEquals(propCount, ps.getProps().size());

        return ps;

    }

    public static TableSection assertTableSection(String name, int columns, int rows, Section s) {

        assertNotNull(s);
        assertTrue(s instanceof TableSection, () -> name + " is TableSection, " + s.getClass());
        TableSection ps = (TableSection) s;
        assertEquals(name, ps.getName());
        assertEquals(columns, ps.getColumns().size());
        assertEquals(rows, ps.getObjects().size());

        return ps;

    }

    public static void assertValueProp(String name, String shortName, String value, Prop prop) {

        assertNotNull(prop);
        assertTrue(prop instanceof ValueProp, () -> name + " is ValueProp, " + prop.getClass());
        ValueProp mvp = (ValueProp) prop;
        assertEquals(name, mvp.getName());
        assertEquals(shortName, mvp.getShortName());
        assertEquals(value, mvp.getValue());

    }

    public static void assertMultiValueProp(String name, String value1Name, String value1, String value2Name,
            String value2, Prop prop) {

        assertNotNull(prop);
        assertTrue(prop instanceof MultiValueProp, () -> name + " is MultiValueProp");
        MultiValueProp mvp = (MultiValueProp) prop;
        assertEquals(name, mvp.getName());

        assertEquals(2, mvp.getProps().size());

        assertEquals(value1Name, mvp.getProps().get(0).getName());
        assertEquals(value1, mvp.getProps().get(0).getValue());

        assertEquals(value2Name, mvp.getProps().get(1).getName());
        assertEquals(value2, mvp.getProps().get(1).getValue());

    }

}
