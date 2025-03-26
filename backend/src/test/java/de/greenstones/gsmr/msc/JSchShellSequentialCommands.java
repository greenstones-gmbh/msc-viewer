package de.greenstones.gsmr.msc;

import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class JSchShellSequentialCommands {

	public static void main(String[] args) {
		int port = 22; // Standard SSH-Port

		String host = "mss.gs.de";
		String user = "user1";
		String password = "pwd1";

		try {
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, port);
			session.setPassword(password);

			// Sicherheitsüberprüfung deaktivieren (für Testzwecke)
			session.setConfig("StrictHostKeyChecking", "no");

			session.connect();

			// Shell-Channel öffnen
			Channel channel = session.openChannel("shell");
			channel.setInputStream(null);
			// ((ChannelShell)channel).setPty(false);

			// Input und Output für den Shell-Channel einrichten
			OutputStream outputStream = channel.getOutputStream();
			InputStream inputStream = channel.getInputStream();

			channel.connect();

			// Array der Befehle
			String[] commands = { "ls\n", "pwd\n", "whoami\n" };

			// Befehle nacheinander senden
			for (String command : commands) {
				System.out.println("Sende Befehl: " + command);
				outputStream.write(command.getBytes());
				outputStream.flush();

				// Warte auf die Ausgabe des aktuellen Befehls
				// waitForCommandOutput(inputStream, channel);
				String c = waitForPrompt(inputStream, channel, "$ ", command);
				System.out.println("------->");
				System.out.println(c);
				System.out.println("-------<");
			}

			channel.disconnect();
			session.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String waitForPrompt(InputStream inputStream, Channel channel, String prompt, String command)
			throws Exception {
		byte[] buffer = new byte[1024];
		StringBuilder output = new StringBuilder();

		while (true) {
			// Prüfe, ob es Daten zum Lesen gibt
			while (inputStream.available() > 0) {
				int i = inputStream.read(buffer, 0, 1024);
				if (i < 0)
					break;
				// Füge die Ausgabe zum StringBuilder hinzu
				String chunk = new String(buffer, 0, i);

				// String ansiEscapePattern = "\u001B\\[[;\\d]*m";
				String ansiEscapePattern = "\u001B\\[[;\\d]*[ -/]*[@-~]";

				// String cleanOutput = chunk.replaceAll(ansiEscapePattern, "");
				String cleanOutput = chunk;
				System.out.println(cleanOutput);

				// output.append(chunk);
				output.append(cleanOutput);

				// System.out.print(chunk); // Gebe die Ausgabe in der Konsole aus

				// System.out.println("\n----->");

				// System.out.println(">>>"+output.toString()+"<<<<");
				// System.out.println("-----<");

				// System.out.println("!!!");

				// Überprüfe, ob der Prompt erreicht wurde
				if (output.toString().endsWith(prompt)) {
					return cleanOutput(output.toString(), command, prompt); // Beende die Schleife, wenn der Prompt
																			// erreicht ist
				}
			}

			// Prüfe, ob der Kanal geschlossen ist oder keine neuen Daten ankommen
			if (channel.isClosed()) {
				if (inputStream.available() > 0)
					continue;
				System.out.println("Exit Status: " + channel.getExitStatus());
				break;
			}
			Thread.sleep(500); // Kurze Pause, um weitere Daten abzuwarten
		}
		return cleanOutput(output.toString(), command, prompt);
	}

	private static String cleanOutput(String output, String command, String prompt) {
		// Entferne ANSI-Escape-Sequenzen
		// String ansiEscapePattern = "\\u001B\\[[;\\d]*m";
		// String cleanedOutput = output.replaceAll(ansiEscapePattern, "");

		String cleanedOutput = output;

		// Entferne den Befehl selbst (nur das erste Vorkommen)
		// cleanedOutput = cleanedOutput.replaceFirst(command, "");

		// Entferne den Prompt am Ende
		// if (cleanedOutput.endsWith(prompt)) {
		// cleanedOutput = cleanedOutput.substring(0, cleanedOutput.length() -
		// prompt.length());
		// }

		// System.err.println(command);
		// System.err.println(output);

		return cleanedOutput.trim(); // Rückgabe der bereinigten Ausgabe
	}
}
