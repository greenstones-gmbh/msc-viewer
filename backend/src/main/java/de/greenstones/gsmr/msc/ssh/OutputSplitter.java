package de.greenstones.gsmr.msc.ssh;

import java.util.regex.Pattern;

public class OutputSplitter {

	String prompt;
	Pattern pattern;

	public static String PROMPT_$_IGNORE_COLORS = ".*\\$ " + "(\u001B\\[[;\\d]*m)?" + "$";
	public static String PROMPT_$ = ".*\\$ $";

	public OutputSplitter(String prompt) {
		this.prompt = prompt;
		this.pattern = Pattern.compile(prompt, Pattern.DOTALL);

	}

	public boolean isCommandFinished(String output) {
		//OutputTransform.printUnicode(output);
		return pattern.matcher(output).matches();
	}

	public static OutputSplitter withPrompt() {
		return new OutputSplitter(PROMPT_$_IGNORE_COLORS);
	}

	public static OutputSplitter withRegExp(String regexp) {
		return new OutputSplitter(regexp);
	}
}