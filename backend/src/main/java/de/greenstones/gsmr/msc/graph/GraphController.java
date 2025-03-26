package de.greenstones.gsmr.msc.graph;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GraphController {

	@Autowired
	GraphService graphService;

	@GetMapping("/api/graph/query")
	@CrossOrigin	
	public Map<String, Object> graph(@RequestParam String q) {
		List<String> gs = Arrays.asList(q.split(";")).stream().filter(s -> !s.trim().isBlank())
				.collect(Collectors.toList());
		return graphService.graph(gs).toMap();
	}

}
