import { RunContext, tool } from "@openai/agents";
import z from "zod";
import { fetchGraph } from "../../clickapp-bootstrap/graph/GraphView";
import { MscViewerRunContext } from "../MscViewerRunContext";

export const queryTool = tool({
  name: "query_graph",
  description: `execute neo4j cypher query given in natural language for a single MSC`,
  parameters: z.object({
    query: z
      .string()
      .describe(
        "valid neo4j cypher query. Always add the current MSC-ID as a label, for exaple [BTS.`MSS-01`] ",
      ),
  }),
  execute: async ({ query }, runContext?: RunContext<MscViewerRunContext>) => {
    console.log("query_graph", query);

    const r = await fetchGraph("/msc-viewer/api/graph/queryList", query);
    console.log(r);

    return {
      status: `Query executed`,
      data: r,
    };
  },
});
