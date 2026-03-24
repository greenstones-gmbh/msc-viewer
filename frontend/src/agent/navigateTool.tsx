import { RunContext, tool } from "@openai/agents";

import z from "zod";
import { ApplicationRunContext } from "./ApplicationRunContext";

export const navigateTool = tool({
  name: "navigate_to",
  description: `Navigate to the page inside the app`,
  parameters: z.object({
    to: z.string().describe("navigate to this path"),
  }),
  execute: async ({ to }, runContext?: RunContext<ApplicationRunContext>) => {
    console.log("navigate_to", to);

    const ctx = runContext?.context;
    ctx?.navigate(to);

    return `Navigated to ${to}`;
  },
});
