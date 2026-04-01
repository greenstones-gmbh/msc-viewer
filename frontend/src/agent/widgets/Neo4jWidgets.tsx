import { QuickTable } from "@greenstones/qui-bootstrap";
import { useColumnGenerator } from "@greenstones/qui-core";
import { Card } from "react-bootstrap";
import { INode } from "../../clickapp-bootstrap/graph/Graph";
import { useConfigTypes } from "../../msc/types/ConfigTypesContext";
import { formatAny } from "../../msc/types/utils";
import { Widgets } from "@greenstones/qui-ai";

export const Neo4jWidgets: Widgets = {
  "neo4j-node": ({ node, ...props }: any) => {
    let data;
    console.log("node", node);

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

    return <NodeComponent node={data} callId={"callId"} />;
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

function NodeComponent({ node, callId }: { node: INode; callId: string }) {
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
          {" "}
          {callId}
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

function NodesComponent({ nodes }: { nodes: INode[] }) {
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
      {JSON.stringify(nodes)}
      <QuickTable items={nodes} columns={cols} />
    </div>
  );
}
