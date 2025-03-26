package de.greenstones.gsmr.msc.core;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import de.greenstones.gsmr.msc.ApplicationException;
import de.greenstones.gsmr.msc.core.Command.Params;
import de.greenstones.gsmr.msc.parser.RegexpUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Formats and parses string-based IDs based on a template.
 * Examples:
 * /lacs/${LAC}/${NO} with params {LAC:"123", NO:"456"} -> /lacs/123/456
 * 
 */
@AllArgsConstructor
@Getter
public class IdConverter {
	String idTemplate;
	Map<String, Integer> paddings = new HashMap<>();

	public IdConverter(String idTemplate) {
		super();
		this.idTemplate = idTemplate;
	}

	public IdConverter pad(String prop, int length) {
		paddings.put(prop, length);
		return this;
	}

	public Params fromRequestId(String id) {
		Map<String, String> vars = RegexpUtils.parseTemplate(idTemplate, id);
		if (vars != null)
			return Params.from(vars);

		throw new InvalidIDFormatException(idTemplate);

	}

	public String toRequestId(Params params) {

		Map<String, String> map = params.keySet().stream().collect(Collectors.toMap(k -> k, k -> {
			String v = params.get(k);
			if (paddings != null && paddings.keySet().contains(k)) {
				return leftPad(v, paddings.get(k), '0');
			}
			return v;
		}));

		return RegexpUtils.interpolate(idTemplate, Params.from(map));
	}

	public static class InvalidIDFormatException extends ApplicationException {
		private static final long serialVersionUID = 7901798750223164708L;

		public InvalidIDFormatException(String template) {
			super("Invalid ID format. ID format: " + template);

		}
	}

	public static String leftPad(String input, int length, char padChar) {
		if (input.length() >= length) {
			return input; // No padding needed
		}
		StringBuilder sb = new StringBuilder();
		while (sb.length() < length - input.length()) {
			sb.append(padChar);
		}
		sb.append(input);
		return sb.toString();
	}

}