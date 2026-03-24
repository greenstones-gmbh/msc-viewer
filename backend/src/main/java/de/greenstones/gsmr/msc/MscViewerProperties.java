package de.greenstones.gsmr.msc;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Component
@ConfigurationProperties("msc-viewer")
@Data
public class MscViewerProperties {

	List<Msc> instances;

	List<User> users;

	@Data
	public static class User {
		String user;
		String password;
	}

	@Data
	public static class Msc {
		String id;
		String host;
		int port = 22;
		String user = null;

		List<Badge> badges;
		String schema;
		Simulate simulate;

		@JsonIgnore
		String password;

		@JsonIgnore
		Map<String, FeatureProviderConfig> gis;

		@JsonIgnore
		Map<String, DataProviderConfig> dataProviders;

	}

	@Data
	public static class Badge {
		String label;
		String style;
	}

	@Data
	public static class Simulate {
		String dataset;
	}

	@Data
	public static class FeatureProviderConfig {
		String path;
		String type;
		String crs;
		String key;
		String x;
		String y;
	}

	@Data
	public static class DataProviderConfig {
		String bean;
		Path path;
		String key;
	}

}
