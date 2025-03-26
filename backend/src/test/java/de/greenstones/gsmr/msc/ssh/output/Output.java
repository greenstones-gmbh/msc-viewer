package de.greenstones.gsmr.msc.ssh.output;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.SneakyThrows;

public class Output {

	static Path BASE_PATH = Path.of("./src/test/responses");

	@SneakyThrows
	public static String read1(String path) {

		InputStream rs = Output.class.getResourceAsStream(path);

		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		for (int length; (length = rs.read(buffer)) != -1;) {
			result.write(buffer, 0, length);
		}
		return result.toString("UTF-8").replaceAll("\n", "\r\n");

	}

	@SneakyThrows
	public static void write(String command, String fileName) {
		Files.writeString(BASE_PATH.resolve(fileName), command);
	}

	@SneakyThrows
	public static String read(String fileName) {
		return Files.readString(BASE_PATH.resolve(fileName));
	}

	@SneakyThrows
	public static String readOutput(String instance, String cmd) {
		return Files.readString(BASE_PATH.resolve(instance).resolve(cmd + ";.txt"));
	}

}
