import { useParams } from "react-router-dom";
import {
  IGraph,
  IGraphStyles,
  INode,
  INodeStyle,
  IRel,
} from "../../clickapp-bootstrap/graph/Graph";
import { useConfigTypes } from "./ConfigTypesContext";
import { formatAny } from "./utils";

export function useConfigTypesGraph() {
  const { mscId } = useParams();
  const types = useConfigTypes();

  const nodes = types
    .filter((t) => t.node)
    .map(
      (t) =>
        ({
          id: t.type,
          labels: [t.node?.typeLabel],
          properties: [{ mscId }],
        } as INode)
    );

  const relationships = types
    .filter((t) => t.node?.relations)
    .flatMap((t) => {
      return (
        t.node?.relations?.map(
          (r) =>
            ({
              id: `${t.type}-[${r.name || "-"}]-${r.targetType}`,
              startId: t.type,
              endId: r.targetType,
              type: r.name,
            } as IRel)
        ) || []
      );
    });

  const nodeStyles = types
    .filter((t) => t.node)
    .map(
      (t) =>
        ({
          selector: t.node?.typeLabel,
          color: t.node?.color,
          title: t.node?.typeTitle || t.node?.typeLabel,
          navPath: (n) => `/${mscId}/${t.type}`,
        } as INodeStyle)
    );

  const styles: IGraphStyles = { nodes: nodeStyles };
  const graph: IGraph = { nodes, relationships };
  return { graph, styles };
}

export function useGraphStyles() {
  const { mscId } = useParams();
  const types = useConfigTypes();

  const nodeStyles = types
    .filter((t) => t.node)
    .map(
      (t) =>
        ({
          selector: t.node?.typeLabel,
          color: t.node?.color,

          captions: (n) => [
            { value: t.node?.typeTitle || t.node?.typeLabel },
            {
              value: t.node?.valueTitle
                ? format(t.node?.valueTitle!, n)
                : n.properties.id,
              styles: ["bold"],
            },
          ],

          navPath: (n) => `/${mscId}/${t.type}/${n.properties.id}`,
        } as INodeStyle)
    );

  nodeStyles.push({
    selector: "*",
    captions: (n) => [{ value: n.labels?.join(",") || "-" }, { value: n.id }],
  });

  const styles: IGraphStyles = { nodes: nodeStyles };
  return styles;
  //return MscGraphStyles;
}

function format(template: string, node: INode) {
  return formatAny(template, node.properties);
}
