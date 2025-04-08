package de.greenstones.gsmr.msc;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.greenstones.gsmr.msc.MscViewerProperties.FeatureProviderConfig;
import de.greenstones.gsmr.msc.clients.MscClient;
import de.greenstones.gsmr.msc.clients.SshMscClient;
import de.greenstones.gsmr.msc.clients.test.TestData;
import de.greenstones.gsmr.msc.clients.test.TestMscClient;
import de.greenstones.gsmr.msc.clients.test.TestOutputGenerator;
import de.greenstones.gsmr.msc.clients.test.TestOutputService;
import de.greenstones.gsmr.msc.core.CommandCache;
import de.greenstones.gsmr.msc.core.MscInstance;
import de.greenstones.gsmr.msc.core.MscResolver;
import de.greenstones.gsmr.msc.core.MscService;
import de.greenstones.gsmr.msc.gis.CsvFeatureProvider;
import de.greenstones.gsmr.msc.gis.FeatureProvider;
import de.greenstones.gsmr.msc.types.ConfigType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration for the MSC Viewer
 */
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class MscViewerConfig {

	@Autowired
	MscViewerProperties props;

	@Setter
	@Value("${msc-viewer.cache.path}")
	Path cachePath;

	@Autowired
	Map<String, ConfigType> gsmrConfigTypes;

	@Autowired
	Map<String, ConfigType> simpleConfigTypes;

	@Bean
	public MscResolver mscResolver() {

		Map<String, MscInstance> instances = props.getInstances().stream()
				.collect(Collectors.toMap(msc -> msc.id, msc -> {

					MscClient c = null;
					TestData testData = null;
					if (msc.getSimulate() != null) {
						testData = TestData.create(msc.getSimulate().getDataset());
						c = new TestMscClient(
								new TestOutputService(new TestOutputGenerator(msc.id),
										testData));
					} else {
						c = new SshMscClient(msc.getHost(), msc.getPort(), msc.getUser(), msc.getPassword());
					}

					CommandCache cache = new CommandCache(cachePath.resolve(msc.id));
					cache.init();
					MscService s = new MscService(c, cache);

					if (testData != null) {
						cache.clear();
					}

					Map<String, ConfigType> configTypes = "simple".equals(msc.getSchema()) ? simpleConfigTypes
							: gsmrConfigTypes;

					var featureProviders = msc.getGis() != null ? msc.getGis().entrySet().stream()
							.collect(Collectors.toMap(e -> e.getKey(), e -> {
								FeatureProviderConfig conf = e.getValue();
								FeatureProvider p = new CsvFeatureProvider(conf.path, conf.crs, conf.key, conf.x,
										conf.y);
								p.init();
								return p;
							})) : new HashMap<String, FeatureProvider>();

					if (featureProviders.isEmpty() && testData != null) {
						featureProviders = testData.getFeatureProviders();
					}

					MscInstance r = new MscInstance(configTypes, s, featureProviders);

					log.info("MSC Instance {} created",
							msc.id);

					return r;
				}));

		MscResolver mscs = new MscResolver(instances);
		return mscs;
	}

}
