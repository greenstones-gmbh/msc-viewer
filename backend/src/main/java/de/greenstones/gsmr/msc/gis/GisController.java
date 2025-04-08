package de.greenstones.gsmr.msc.gis;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.greenstones.gsmr.msc.core.MscResolver;
import de.greenstones.gsmr.msc.graph.GraphNodeService.DefaultGraphNodeService;
import de.greenstones.gsmr.msc.graph.MscGraphService;
import de.greenstones.gsmr.msc.types.ConfigType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/gis/msc")
@CrossOrigin
@Slf4j
public class GisController {

	@Autowired
	MscGraphService mscGraphService;

	@Autowired
	MscResolver mscs;

	// LocationService cellLocationService = new LocationService() {
	// public java.util.Map<String, Location> load() {
	// Map<String, Location> cells = new HashMap<>();
	// cells.put("BTS10000", new Location(8.631705, 50.172309, 1000));
	// cells.put("BTS10001", new Location(8.628027, 50.176661, 1500));
	// cells.put("BTS10002", new Location(8.605501, 50.180791, 2000));
	// cells.put("BTS10003", new Location(8.636469, 50.188740, 2000));
	// return cells;
	// };

	// };

	@CrossOrigin
	@GetMapping("/{mscId}/{type}")
	@SneakyThrows
	public Object list(@PathVariable String mscId, @PathVariable String type) {

		Map<String, ConfigType> types = mscs.find(mscId).getTypes();
		var featureProviders = mscs.find(mscId).getFeatureProviders();

		var ltypes = types.entrySet().stream().filter(e -> e.getValue().getLocation() != null)
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getLocation()));

		FeatureService ls = new FeatureService(ltypes,
				new DefaultGraphNodeService(mscGraphService, mscId), featureProviders);

		return Utils.toGeoJSON(ls.getAll(type));
	}

	@CrossOrigin
	@GetMapping("/{mscId}/{type}/{id}")
	@SneakyThrows
	public Object list(@PathVariable String mscId, @PathVariable String type, @PathVariable String id) {

		Map<String, ConfigType> types = mscs.find(mscId).getTypes();
		var featureProviders = mscs.find(mscId).getFeatureProviders();

		var ltypes = types.entrySet().stream().filter(e -> e.getValue().getLocation() != null)
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getLocation()));

		FeatureService ls = new FeatureService(ltypes,
				new DefaultGraphNodeService(mscGraphService, mscId), featureProviders);
		var v = ls.getOne(type, id);

		return Utils.toGeoJSON(v);

	}

}
