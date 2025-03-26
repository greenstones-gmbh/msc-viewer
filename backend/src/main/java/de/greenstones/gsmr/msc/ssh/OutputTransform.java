package de.greenstones.gsmr.msc.ssh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OutputTransform {

	List<Function<String, String>> transforms = new ArrayList<>();

	public OutputTransform add(Function<String, String> fn) {
		transforms.add(fn);
		return this;
	}

	public String transform(String output) {
		if (transforms != null) {
			String o = output;
			for (Function<String, String> function : transforms) {
				o = function.apply(o);
			}
			return o;
		}
		return output;
	}

	public static String removeCopyPasteSeq(String output) {
		return output.replaceAll("\u001B\\[\\?2004[h|l]", "");

	}

	public static String removeColors(String output) {
		return output.replaceAll("\u001B\\[[;\\d]*m", "");
	}

	public static String removeCommandAndPrompt(String output) {
		return output.replaceFirst("^(.+)\\R", "").replaceFirst("\\R(.+)$", "");
	}

	@SafeVarargs
	public static OutputTransform create(Function<String, String>... fns) {
		OutputTransform t = new OutputTransform();
		t.transforms = Arrays.asList(fns);
		return t;
	}

	public static OutputTransform createCommandTransform() {
		return create(OutputTransform::removeCopyPasteSeq, OutputTransform::removeColors,
				OutputTransform::removeCommandAndPrompt);
	}

	public static String transform(String output, OutputTransform transform) {
		if (transform != null) {
			return transform.transform(output);
		}
		return output;
	}

	public static void printUnicode(String input) {
		StringBuilder visibleString = new StringBuilder();

		for (char c : input.toCharArray()) {
			if (Character.isISOControl(c)) {

				if (c == '\n' || c == '\r') {
					if(c == '\n') {
						visibleString.append("\\n");	
					}
					if(c == '\r') {
						visibleString.append("\\r");	
					}
					
					visibleString.append(c);
				} else

					// Wenn es ein Steuerzeichen ist, zeige den Unicode-Wert
					visibleString.append("<U+").append(String.format("%04X", (int) c)).append(">");
			} else if (c == '\u200B') {
				// Spezielle Behandlung für "Zero Width Space"
				visibleString.append("[ZWSP] "); // Zero Width Space
			} else if (c == '\u00A0') {
				// Behandlung für non-breaking space
				visibleString.append("[NBSP] "); // Non-Breaking Space
			} else {
				// Anderen Zeichen unverändert hinzufügen
				visibleString.append(c);
			}
		}

		System.err.println(visibleString.toString()+"<--");
	}
}