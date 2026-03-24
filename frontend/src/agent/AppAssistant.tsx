import { createOpenAI } from "@ai-sdk/openai";
import {
  AssistantButton,
  LocalSession,
  useChat,
  Widgets,
} from "@greenstones/qui-ai";
import type { Session } from "@openai/agents";
import { Agent } from "@openai/agents";
import { aisdk } from "@openai/agents-extensions/ai-sdk";
import { createContext, useContext } from "react";

import { useNavigate, useParams } from "react-router-dom";
import { queryGraphTool } from "./queryGraphTool";
import { instractions } from "./instructions";
import { ApplicationRunContext } from "./ApplicationRunContext";
import { Card, CardBody, Col, Row } from "react-bootstrap";
import { useConfigTypes } from "../msc/types/ConfigTypesContext";
import { INode } from "../clickapp-bootstrap/graph/Graph";
import { formatAny, formatTemplate } from "../msc/types/utils";
import { formatText } from "../msc/types/ConfigType";
import { QuickTable } from "@greenstones/qui-bootstrap";
import { useColumnBuilder, useColumnGenerator } from "@greenstones/qui-core";

const openai = createOpenAI({
  apiKey: import.meta.env.VITE_OPENAI_API_KEY,
  baseURL: "https://api.openai.com/v1/",
});

const model = aisdk(openai("gpt-4.1-mini"));
const session = new LocalSession("app-assistant");

const agent = new Agent<ApplicationRunContext>({
  name: "App Agent",
  instructions: instractions,
  model: model,
  tools: [queryGraphTool],
});

export interface ApplicationAgent {
  agent: Agent<ApplicationRunContext>;
  session: Session;
}

export const ApplicationAgentContext = createContext<ApplicationAgent>(
  {} as any,
);

export const defaultApplicationAgent = { agent, session };

export function useApplicationAgent() {
  return useContext(ApplicationAgentContext);
}

export function ApplicationAgentButton() {
  const chat = useAppChat();
  return <AssistantButton chat={chat} />;
}

const widgets: Widgets = {
  "neo4j-node": ({ node, ...props }: any) => {
    let data;
    try {
      data = JSON.parse(node.properties.node);
    } catch (error) {
      console.error(error);
    }

    if (!data)
      return (
        <div>
          Can't parse node <pre>{node.properties.node}</pre>
        </div>
      );

    return <NodeComponent node={data} />;
  },

  "neo4j-nodes": ({ node, ...props }: any) => {
    let data;
    try {
      data = JSON.parse(node.properties.nodes);
    } catch (error) {
      console.error(error);
    }

    if (!data)
      return (
        <div>
          Can't parse node <pre>{node.properties.nodes}</pre>
        </div>
      );

    return <NodesComponent nodes={data} />;
  },
};

export function NodeComponent({ node }: { node: INode }) {
  const types = useConfigTypes();

  console.log("node", node);

  const configType = types.find(
    (t) => node.labels.indexOf(t.node?.typeLabel || "") !== -1,
  );
  console.log("configType", configType);

  return (
    <Card className="my-3 bg-light">
      <Card.Header>
        <span className="h4">
          {configType?.node?.typeTitle || configType?.node?.typeLabel}{" "}
          {formatAny(configType?.node?.valueTitle || "---", node.properties)}
        </span>
      </Card.Header>
      <Card.Body>
        {Object.keys(node.properties).map((k) => (
          <div key={k}>
            {k}: {node.properties[k]}
          </div>
        ))}
      </Card.Body>
    </Card>
  );
}

export function NodesComponent({ nodes }: { nodes: INode[] }) {
  const types = useConfigTypes();

  console.log("nodes", nodes);
  const cols = useColumnGenerator(nodes);

  if (!nodes) return <div>Empty nodes</div>;

  const configType = types.find(
    (t) => nodes[0].labels?.indexOf(t.node?.typeLabel || "") !== -1,
  );

  console.log("configType", configType);
  if (!configType) return <div>no config type</div>;

  return (
    <div>
      <h4>{configType?.node?.typeTitle || configType?.node?.typeLabel}</h4>
      <QuickTable items={nodes} columns={cols} />
    </div>
  );
}

export function useAppChat() {
  const { agent, session } = useApplicationAgent();
  const navigate = useNavigate();
  const { mscId } = useParams();
  const chat = useChat<ApplicationRunContext>({
    agent,
    session,
    provider: () => {
      return {
        context: { navigate: (to: string) => navigate(`${to}`) },
        state: {
          currentMsc: mscId,
          availableMscs: "MSS-DEV-01,MSS-DEV-02,MSS-PROD-01",
        },
      };
    },
    widgets,
  });
  return chat;
}
