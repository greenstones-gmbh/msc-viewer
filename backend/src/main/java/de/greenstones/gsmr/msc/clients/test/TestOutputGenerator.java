package de.greenstones.gsmr.msc.clients.test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

import lombok.SneakyThrows;

public class TestOutputGenerator {

	String mssName;

	Handlebars handlebars;
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	public TestOutputGenerator(String mssName) {
		this.mssName = mssName;
		TemplateLoader loader = new ClassPathTemplateLoader();
		loader.setPrefix("/simulator-templates");
		loader.setSuffix(".txt");
		handlebars = new Handlebars(loader);

	}

	@SneakyThrows
	public String generate(String templateName, Map<String, Object> data) {
		Template template = handlebars.compile(templateName);
		Map<String, Object> map = getBaseData();
		map.putAll(data);
		return template.apply(map);
	}

	@SneakyThrows
	public String generate(String templateName, Object data) {
		Template template = handlebars.compile(templateName);
		Map<String, Object> map = getBaseData();
		Context ctx = Context.newContext(data).combine(map);
		return template.apply(ctx);
	}

	private Map<String, Object> getBaseData() {
		Map<String, Object> map = new HashMap<>();
		LocalDateTime now = LocalDateTime.now();
		map.put("MSS_NAME", mssName);
		map.put("DATE", now.format(dateFormatter));
		map.put("TIME", now.format(timeFormatter));
		return map;
	}

}