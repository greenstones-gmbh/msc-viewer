import { createOpenAI } from "@ai-sdk/openai";
import { Agent } from "@openai/agents";
import { aisdk } from "@openai/agents-extensions/ai-sdk";

import { MscViewerRunContext } from "./MscViewerRunContext";
import { prompt } from "./prompt";
import { queryTool } from "./tools/queryGraphTool";

// const openai = createOpenAI({
//   apiKey: import.meta.env.VITE_OPENAI_API_KEY,
//   baseURL: "https://api.openai.com/v1/",
// });

const openai = createOpenAI({
  apiKey: "1",
  baseURL: "/msc-viewer/api/openai",
});

const model = aisdk(openai("gpt-4.1"));

export const agent = new Agent<MscViewerRunContext>({
  name: "MscViewer Agent",
  instructions: prompt,
  model: model,
  tools: [queryTool],
});
