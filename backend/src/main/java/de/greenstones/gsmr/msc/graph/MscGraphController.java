package de.greenstones.gsmr.msc.graph;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.greenstones.gsmr.msc.core.Command.CommandOutput;
import de.greenstones.gsmr.msc.graph.MscGraphService.GraphInfo;
import de.greenstones.gsmr.msc.model.Obj;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/graph/msc")
@CrossOrigin
@Slf4j
public class MscGraphController {

	@Autowired
	MscGraphService mscGraphService;

	@GetMapping("/{mscId}")
	public GraphInfo info(@PathVariable String mscId) {
		return mscGraphService.getInfo(mscId);
	}

	@GetMapping("/{mscId}/{sourceType}/{id}/rels/{targetTypes}")
	public Object relationData(@PathVariable String mscId, @PathVariable String sourceType, @PathVariable String id,
			@PathVariable String targetTypes) {
		log.debug("relationData {} {} {}", mscId, sourceType, id, targetTypes);

		List<Obj> list = mscGraphService.getRelatedData(mscId, sourceType, id, targetTypes.split(","));
		CommandOutput<List<Obj>> o = new CommandOutput<>("graph", "", "", list);
		return o;
	}

	@PostMapping("/{mscId}/update")
	@SneakyThrows
	public Object update(@PathVariable String mscId) {
		mscGraphService.updateGraph(mscId, false);
		log.info("update done");
		return Collections.singletonMap("status", "ok");
	}

}
