package de.greenstones.gsmr.msc.graph;

import static org.mockito.ArgumentMatchers.booleanThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.greenstones.gsmr.msc.core.MscInstance;
import de.greenstones.gsmr.msc.core.MscResolver;
import de.greenstones.gsmr.msc.model.Obj;
import de.greenstones.gsmr.msc.types.ConfigType;

@SpringBootTest
class GraphTests {

	@Autowired
	MscResolver mscResolver;

	@Test
	void contextLoads() {

		MscInstance mscInstance = mscResolver.find("MSS-01");

		Map<String, ConfigType> types = mscInstance.getTypes();

		boolean force = false;

		Map<String, List<Obj>> execute = mscInstance.execute(shell -> {

			return types.keySet().stream().filter(k -> types.get(k).getNode() != null)
					.collect(Collectors.toMap(k -> k, k -> {
						ConfigType type = types.get(k);
						List<Obj> data = type.getNode().loadData(shell, k, force);
						// data.stream().map(type.getGraphNode().getPropsMapping()::map).collect(Collectors.toList());

						System.err.println(k + " " + type.getNode());
						return data;
					}));

		});

	}

}
