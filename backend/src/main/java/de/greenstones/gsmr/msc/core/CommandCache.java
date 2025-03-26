package de.greenstones.gsmr.msc.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class CommandCache {

	Path path;

	@SneakyThrows
	@PostConstruct
	public void init() {
		if (!Files.exists(path)) {
			log.info("create cache dir: {}", path);
			Files.createDirectories(path);
		} else {
			log.info("cache dir: {} exists", path);
		}
	}

	@SneakyThrows
	public void write(String cmd, String content) {
		Files.writeString(path.resolve(cmd + ".txt"), content);
	}

	@SneakyThrows
	public boolean contains(String cmd) {
		return Files.exists(path.resolve(cmd + ".txt"));
	}

	@SneakyThrows
	public String read(String cmd) {
		return Files.readString(path.resolve(cmd + ".txt"));
	}

	@SneakyThrows
	public void clear() {
		log.info("clear cache: {}", path);
		Files.list(path).forEach(p -> {
			try {
				Files.delete(p);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

}