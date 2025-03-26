package de.greenstones.gsmr.msc.ssh.output;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestProp {
public static void main(String[] args) {
	String output="BTS NAME .............................. (NAME).. :LDA65001  ";
	//BSC   NAME : -                           NUMBER  : -
	System.err.println(output);
	
	//Pattern p = Pattern.compile("(.*?) ?\\.+ ?\\((\\w+)\\)\\.*",Pattern.DOTALL);
	Pattern p = Pattern.compile("(.*?) ?\\.+ ?\\((\\w+)\\)\\.* :(.*)",Pattern.DOTALL);
	//Pattern p = Pattern.compile("(.*?) ?\\.+\\((\\w+)\\)\\.* :(.*)",Pattern.DOTALL);
	
		Matcher matcher =p.matcher(output);
	if (matcher.find()) {
		System.err.println(matcher.group(1)+"<");
		System.err.println(matcher.group(2)+"<");
//		System.err.println(matcher.group(3)+"<");
//		System.err.println(matcher.group(4)+"<");
//		System.err.println(matcher.group(5)+"<");
	} else {
		System.err.println("not match");
	}
}
}
