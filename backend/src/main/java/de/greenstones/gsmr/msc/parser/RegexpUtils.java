package de.greenstones.gsmr.msc.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexpUtils {

	public static List<String> groups(String regexp, String content) {
		// OutputTransform.printUnicode(content);
		Pattern p = Pattern.compile(regexp, Pattern.DOTALL);
		Matcher m = p.matcher(content);
		List<String> parts = new ArrayList<>();
		if (m.find()) {
			for (int i = 1; i <= m.groupCount(); i++) {
				// System.err.println((i-1)+">"+m.group(i)+"<");
				parts.add(m.group(i));
			}
		} else {
			return null;
		}
		return parts;
	}

	public static List<List<String>> allGroups(String regexp, String content) {
		Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(content);
		List<List<String>> items = new ArrayList<>();
		while (m.find()) {
			List<String> groups = new ArrayList<>();
			for (int i = 1; i <= m.groupCount(); i++) {
				groups.add(m.group(i));
			}
			items.add(groups);

		}
		return items;
	}

	public static Set<String> getVarNames(String template) {
		String var = "\\$\\{(\\w+)\\}";
		return allGroups(var, template).stream().flatMap(l -> l.stream())
				.collect(Collectors.toSet());
	}

	public static Map<String, String> parseTemplate(String template, String content) {
		String var = "\\$\\{(\\w+)\\}";
		List<String> vars = allGroups(var, template).stream().flatMap(l -> l.stream())
				.collect(Collectors.toList());
		String regexp = template.replaceAll(var, "(?<$1>.*)");
		// System.err.println(">>>"+regexp);
		Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(content);
		if (m.find()) {
			Map<String, String> groups = new HashMap<>();

			for (String v : vars) {
				groups.put(v, m.group(v));
			}
			return groups;
		}
		return null;

	}

	public static String interpolate(String template, Map<String, String> values) {
		Map<String, String> valuesMap = Optional.ofNullable(values).orElse(Map.of());
		// Regular expression to find placeholders in the form ${variableName}
		Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
		Matcher matcher = pattern.matcher(template);

		// Extract placeholders from the template
		Set<String> placeholders = matcher.results().map(matchResult -> matchResult.group(1))
				.collect(Collectors.toSet());

		// Extract keys from the map
		Set<String> mapKeys = valuesMap.keySet();

		// Check for placeholders without corresponding map entries
		Set<String> missingKeys = placeholders.stream().filter(placeholder -> !mapKeys.contains(placeholder))
				.collect(Collectors.toSet());

		// Check for map entries without corresponding placeholders
		Set<String> extraKeys = mapKeys.stream().filter(key -> !placeholders.contains(key)).collect(Collectors.toSet());

		// If there are any missing or extra keys, throw an exception
		if (!missingKeys.isEmpty() || !extraKeys.isEmpty()) {
			throw new IllegalArgumentException("Mismatch between placeholders and map keys. " + "Missing keys: "
					+ missingKeys + ", Extra keys: " + extraKeys);
		}

		// Reset matcher to start replacement
		matcher.reset();
		StringBuffer result = new StringBuffer();

		// Replace placeholders with corresponding values from the map
		while (matcher.find()) {
			String variableName = matcher.group(1);
			String replacement = valuesMap.get(variableName);
			matcher.appendReplacement(result, replacement);
		}
		matcher.appendTail(result);
		return result.toString();
	}
}
