package de.greenstones.gsmr.msc;

public class Test2 {

	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_RESET = "\u001B[0m";
	
	public static void main(String[] args) {
		
		String s = ANSI_RED + "This text is red!" + ANSI_RESET+" sadasd";
		
		System.out.println(s);
		System.out.println(s.replaceAll("\u001B\\[\\d+m", ""));
		
		System.out.println(convertInvisibleChars(s));
		
		
	}

	
	   public static String convertInvisibleChars(String input) {
	        StringBuilder visibleString = new StringBuilder();

	        for (char c : input.toCharArray()) {
	            if (Character.isISOControl(c)) {
	            	
	            	if(c== '\n' || c== '\r') {
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

	        return visibleString.toString();
	    }
	
}
