// Import only the types to allow treeshaking
import type { Node, Relationship, StyledCaption } from "@neo4j-nvl/base";

export interface IGraph {
  nodes: INode[];
  relationships: IRel[];
}

export interface INode {
  id: string;
  labels: string[];
  properties: any;
}

export interface IRel {
  id: string;
  startId: string;
  endId: string;
  type?: string;
  properties?: any;
}

export interface INodeStyle {
  selector: string;
  color?: string | ((n: INode) => string);
  captions?: (n: INode) => StyledCaption[];
  title?: string | ((n: INode) => string);
  titleProp?: string;
  useLabelAsTitle?: boolean;
  label?: string;
  styles?: string[];
  navPath?: (n: INode, context: any) => string;
}

export interface IGraphStyles {
  nodes: INodeStyle[];
}

export function findNodeStyle(n: INode, styles: IGraphStyles) {
  //const selector = n.labels && n.labels.length > 0 ? n.labels[0] : "*";

  const labels: string[] = n.labels && n.labels.length > 0 ? n.labels : ["*"];
  return styles.nodes.find((s) => labels.find((l) => l === s.selector));
}

export function createNodeWithStyles(n: INode, styles: IGraphStyles) {
  const style: INodeStyle = findNodeStyle(n, styles) || { selector: "*" };

  const color = style.color
    ? typeof style.color === "function"
      ? style.color(n)
      : style.color
    : undefined;

  var captionsFn = (n: INode): StyledCaption[] => [
    { value: n.labels?.join(",") || "-" },
    { value: n.id },
  ];

  const titleFn = (n: INode) => {
    //return style.useLabelAsTitle ? [{ value: n.labels?.join(",") }] : [];
    return style.useLabelAsTitle
      ? [{ value: n.labels?.join(",") }]
      : style.label
      ? [{ value: style.label || "-" }]
      : [];
  };

  if (style.captions) {
    if (typeof style.captions === "function") {
      captionsFn = style.captions;
    }
  } else {
    if (style.title) {
      if (typeof style.title === "function") {
        const styleFn = style.title as any;
        captionsFn = (n: INode): StyledCaption[] => [
          ...titleFn(n),
          { value: styleFn(n), styles: style.styles },
        ];
      } else if (typeof style.title === "string") {
        captionsFn = (n: INode): StyledCaption[] => [
          ...titleFn(n),
          { value: style.title as any, styles: style.styles },
        ];
      }
    } else if (style.titleProp) {
      captionsFn = (n: INode): StyledCaption[] => [
        ...titleFn(n),
        {
          value: n.properties[style.titleProp as any],
          styles: style.styles,
        },
      ];
    }
  }
  const captions = captionsFn(n);

  const node: Node = {
    id: n.id,
    color,
    captions,
    //html: document.getElementById("node-" + n.id) || undefined,
  };
  return node;
}

export const createRel = (n: IRel): Relationship => {
  var node: Relationship = {
    id: n.id,
    from: n.startId,
    to: n.endId,
    captionHtml: null as any,
    caption: n.type,
  };
  return node;
};
