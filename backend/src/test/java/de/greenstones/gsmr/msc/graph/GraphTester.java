package de.greenstones.gsmr.msc.graph;

import de.greenstones.gsmr.msc.graph.GraphService.Graph;
import de.greenstones.gsmr.msc.parser.MscParser;

public class GraphTester {

	static MscParser p = new MscParser();

	static GraphService graphService = new GraphService();

	public static void main(String[] args) {

//		{
//			List<Record> list = graphService.query("CALL apoc.export.json.all(null, {stream: true, useTypes: true})");
//			list.stream().forEach(r -> {
//				System.err.println(r.get("data").asString());
//			});
//
//		}

		{
			Graph g = graphService.graph("match ph=(n:Movie{title: 'Hoffa'})-[]-(p:Person) return *");
			System.err.println(g.desc());
		}

	}

}
