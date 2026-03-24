import { RunContext, tool } from "@openai/agents";
import z from "zod";
import { fetchGraph } from "../clickapp-bootstrap/graph/GraphView";
import { ApplicationRunContext } from "./ApplicationRunContext";

export const queryGraphTool = tool({
  name: "query_graph",
  description: `execute neo4j cypher query given in natural language. Include query in response`,
  parameters: z.object({
    query: z.string().describe("valid cypher query"),
  }),
  execute: async (
    { query },
    runContext?: RunContext<ApplicationRunContext>,
  ) => {
    console.log("query_graph", query);

    const r = await fetchGraph("/msc-viewer/api/graph/query", query);
    console.log(r);

    return {
      status: `Query executed`,
      data: r,
    };
  },
});
