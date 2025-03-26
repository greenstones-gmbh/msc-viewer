package de.greenstones.gsmr.msc.core;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.greenstones.gsmr.msc.model.Obj;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Describes mapping of MSC object fields to a map. It is used to extract and
 * rename fields and values.
 */
@RequiredArgsConstructor
public class ParamMapping {

	@Getter
	@NonNull
	String mappingString;
	@NonNull
	List<String[]> mapping;

	@Getter
	@Setter
	Map<String, String> valueMapping = null;

	/**
	 * Extract and rename the fields.
	 * 
	 * @param obj
	 * @return
	 */
	public Map<String, String> map(Obj obj) {

		return mapping.stream().collect(Collectors.toMap(k -> k[0], k -> {
			// System.err.println(Arrays.asList(k));
			if (k.length == 2) {
				return mapValue(obj.getValue(k[1]));
			}
			if (k.length == 3) {
				return mapValue(obj.getValue(k[1], k[2]));
			}
			return mapValue(obj.getValue(k[0]));

		}));
	}

	protected String mapValue(String v) {
		if (valueMapping == null)
			return v;
		if (v == null)
			return v;
		String v1 = valueMapping.get(v);
		return v1 != null ? v1 : v;
	}

	public List<String> getKeys() {
		return mapping.stream().map(c -> c[0]).toList();
	}

	/**
	 * Creates a ParamMapping from a string.
	 * 
	 * Examples:
	 * "CI,MCC,MNC" - > Extracts the fields CI, MCC, MNC
	 * "LAC=LA" - > Extracts the field LA and renamed it to LAC
	 * "NAME=BTS|NAME" - > Extracts the field from the line like "BTS........ NAME:
	 * aaaa NO:1" and renamed it to NAME
	 * 
	 * @param mapping
	 * @return
	 */
	public static ParamMapping from(String mapping) {
		List<String[]> ll = Arrays.asList(mapping.split(",")).stream() //
				.map(s -> s.trim()) //
				.map(s -> s.split("=")) //
				.map(s -> {
					if (s.length == 2) {
						String[] names = s[1].split("\\|");
						if (names.length == 2) {
							return new String[] { s[0], names[0], names[1] };
						}
					}
					return s;
				}) //
				.collect(Collectors.toList());
		return new ParamMapping(mapping, ll);
	}

}