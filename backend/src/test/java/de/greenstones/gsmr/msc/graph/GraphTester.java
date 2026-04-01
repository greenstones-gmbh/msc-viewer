package de.greenstones.gsmr.msc.graph;

import java.util.Arrays;
import java.util.List;

import de.greenstones.gsmr.msc.parser.MscParser;

public class GraphTester {

	static MscParser p = new MscParser();

	static GraphService graphService = new GraphService();

	public static void main(String[] args) {

		// {
		// List<Record> list = graphService.query("CALL apoc.export.json.all(null,
		// {stream: true, useTypes: true})");
		// list.stream().forEach(r -> {
		// System.err.println(r.get("data").asString());
		// });
		//
		// }

		{
			List<String> asList = Arrays.asList(
					"MATCH (b:BTS:`MSS-PROD-01`) RETURN count(b) AS cellCount");

		}

	}

}
