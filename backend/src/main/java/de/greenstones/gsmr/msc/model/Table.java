package de.greenstones.gsmr.msc.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.greenstones.gsmr.msc.model.Props.Prop;
import de.greenstones.gsmr.msc.model.Props.ValueProp;
import de.greenstones.gsmr.msc.model.Sections.PropSection;
import de.greenstones.gsmr.msc.model.Sections.Section;
import lombok.Getter;

@Getter
public class Table {
	List<String> headers = new ArrayList<String>();
	List<List<String>> rows = new ArrayList<List<String>>();

	@Override
	public String toString() {
		return headers + "\n" + rows;
	}

	public String get(int i, String key) {
		int index = headers.indexOf(key);
		return rows.get(i).get(index);
	}

	public Map<String, String> getRow(int i) {
		Map<String, String> row = new HashMap<String, String>();
		for (int j = 0; j < headers.size(); j++) {
			row.put(headers.get(j), rows.get(i).get(j));

		}
		return row;
	}

	public List<Obj> toList() {

		List<Obj> obj = rows.stream().map(row -> {

			List<Prop> props = new ArrayList<>();

			for (int i = 0; i < headers.size(); i++) {
				String header = headers.get(i);
				String value = row.get(i);
				props.add(new ValueProp(header, null, value));
			}

			PropSection s = new PropSection(null, props);
			List<Section> sections = Arrays.asList(s);
			Obj o = new Obj(sections);

			return o;
		}).collect(Collectors.toList());
		return obj;

	}
}