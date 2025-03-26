package de.greenstones.gsmr.msc;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.greenstones.gsmr.msc.core.MscInstance;
import de.greenstones.gsmr.msc.core.MscResolver;
import de.greenstones.gsmr.msc.types.ConfigType;

//@SpringBootTest
class MscConnectorApplicationTests {

	@Autowired
	MscResolver mscResolver;

	@Test
	void contextLoads() {
		System.err.println(mscResolver);

		MscInstance mscInstance = mscResolver.find("MSS-01");
		Map<String, ConfigType> types = mscInstance.getTypes();

		// List<FrontendConfiguration> list = FrontendConfiguration.create(types);

		// System.err.println(list.get(0).getList());

	}

}
