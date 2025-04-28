// Lazily import only the needed parts for treeshaking
import type { NVL, Node } from "@neo4j-nvl/base";
import {
  InteractiveNvlWrapper,
  type MouseEventCallbacks,
} from "@neo4j-nvl/react";
// Mark as dynamic import to allow treeshaking
const NVLComponents = () => import("@neo4j-nvl/react");

import { useEffect, useMemo, useRef } from "react";
import { Button, ButtonGroup } from "react-bootstrap";
import { BsFullscreen, BsZoomIn, BsZoomOut } from "react-icons/bs";
import { useNavigate } from "react-router-dom";
import {
  IGraph,
  IGraphStyles,
  INode,
  createNodeWithStyles,
  createRel,
  findNodeStyle,
} from "./Graph";

export function BaseGraphView({
  graph,
  styles = { nodes: [] },
  expandFn,
  context,
}: {
  graph: IGraph;
  styles?: IGraphStyles;
  expandFn?: (node: INode) => Promise<IGraph>;
  context?: any;
}) {
  const graphRef = useRef<IGraph>(graph);

  useEffect(() => {
    graphRef.current = graph;
    zoomAll();
  }, [graph]);

  const nvlRef = useRef<NVL>(null);
  const navigate = useNavigate();

  const { nodes, rels }: any = useMemo(() => {
    console.log("StaticGraphView.init", graph);

    if (!!graph) {
      const nn = graph.nodes.map((n) => createNodeWithStyles(n, styles));
      const rels = graph.relationships.map(createRel);
      return { nodes: nn, rels: rels };
    }
    return { nodes: [], rels: [] };
  }, [graph, styles]);

  const zoomAll = () => {
    nvlRef.current?.fit(
      nvlRef.current?.getNodes().map((n: any) => n.id),
      {}
    );
  };

  const zoomOut = () => {
    nvlRef.current?.setZoom(nvlRef.current?.getScale() * 0.7);
  };

  const zoomIn = () => {
    nvlRef.current?.setZoom(nvlRef.current?.getScale() * 1.3);
  };

  const findNode = (node: Node) =>
    graphRef.current?.nodes.find((n) => n.id === node.id);

  const mouseEventCallbacks: MouseEventCallbacks = {
    onZoom: true,
    onPan: true,
    onDrag: true,
    onHover: true,
    onNodeClick(node, hitElements, event) {
      if (event.detail === 1) {
        const n = findNode(node);
        console.log("onNodeClick", node, n);

        if (n && expandFn) {
          const fn = async () => {
            const g = await expandFn?.(n);
            if (g) {
              const existingNodeIds =
                graphRef.current?.nodes.map((n: any) => n.id) || [];
              const existingRelIds =
                graphRef.current?.relationships.map((n: any) => n.id) || [];

              const newNodes = g.nodes.filter(
                (n) => existingNodeIds.indexOf(n) === -1
              );

              const newRels = g.relationships.filter(
                (n) => existingRelIds.indexOf(n) === -1
              );

              graphRef.current.nodes = [
                ...graphRef.current?.nodes,
                ...newNodes,
              ];

              graphRef.current.relationships = [
                ...graphRef.current?.relationships,
                ...newRels,
              ];

              const nn = newNodes.map((n) => createNodeWithStyles(n, styles));
              const rr = newRels.map(createRel);

              nvlRef.current?.addAndUpdateElementsInGraph(nn, rr);
            }
          };
          fn();
        }
      }
    },
    onNodeDoubleClick(node, hitElements, event) {
      const n = findNode(node);
      console.log("onNodeDoubleClick", node, n);
      if (n) {
        const style = findNodeStyle(n, styles);
        const path = style?.navPath?.(n, context);
        if (path) navigate(path);
      }
    },
    // onHover(element, hitElements, event) {
    //   if (element != null) {
    //     //console.log("onHover", element, hitElements, event);
    //     const s = nvlRef.current?.getPositionById(element.id);
    //     console.log("pos ", element, s);
    //   }
    // },
  };
  const nvlCallbacks = {
    onLayoutDone: () => {
      //console.log("onLayoutDone");
    },
    onLayoutComputing(isComputing: boolean) {
      // console.log("onLayoutComputing " + isComputing);
      // if (!isComputing) {
      //   //zoomAll();
      // }
    },
  };

  return (
    <div className="flex-fill d-flex flex-column ">
      {/* <div>
        {nodes?.map((n: any) => (
          <div id={`node-${n.id}`}>{n.id}</div>
        ))}
      </div> */}
      <div style={{}} className="bg-light border1 rounded  w-100 h-100 mt-1">
        <InteractiveNvlWrapper
          ref={nvlRef}
          nvlOptions={{
            useWebGL: false,
            renderer: "canvas",
            minZoom: 1,
            allowDynamicMinZoom: true,
            initialZoom: 2,
            layout: "d3Force",
            layoutTimeLimit: 0.5,
          }}
          nodes={nodes}
          rels={rels}
          mouseEventCallbacks={mouseEventCallbacks}
          nvlCallbacks={nvlCallbacks}
        >
          <div
            style={{
              position: "absolute",
              top: 20,
              left: 20,
              zIndex: 100000,
            }}
            className="p-1 rounded border shadow-sm bg-white1 "
          >
            <ButtonGroup vertical>
              <Button variant="light" onClick={zoomAll}>
                <BsFullscreen />
              </Button>
              <Button variant="light" onClick={zoomIn}>
                <BsZoomIn />
              </Button>
              <Button variant="light" onClick={zoomOut}>
                <BsZoomOut />
              </Button>
            </ButtonGroup>
          </div>
          <div
            style={{
              position: "absolute",
              top: 20,
              right: 20,
              zIndex: 100000,
              fontSize: 12,
            }}
            className="text-muted p-1 rounded1 border1 shadow-sm1 bg-white1 text-end fw-lighter"
          >
            <span>Click on node to expand childs</span>
            <br />
            <span>Double click to navigate</span>
          </div>
        </InteractiveNvlWrapper>
      </div>
    </div>
  );
}

// export function GraphViewOld({
//   query,
//   queries,
// }: {
//   query?: string;
//   queries?: string[];
// }) {
//   const nvlRef = useRef<NVL>(null);
//   const navigate = useNavigate();
//   const q = query || queries?.join(";") || "";
//   const [expandedNodes, setExpandedNodes] = useState<any>({});
//   const { data } = useGraph("http://localhost:8080/api/graph/query", q);
//   console.log(data);
//   const { nodes, rels }: any = useMemo(() => {
//     console.log("GraphView.init", data);
//     if (!!data) {
//       const nn = data.nodes.map(createNode);
//       const rels = data.relationships.map(createRel);
//       return { nodes: nn, rels: rels };
//     }
//     return { nodes: [], rels: [] };
//   }, [data]);
//   const zoomAll = () => {
//     nvlRef.current?.fit(
//       nvlRef.current?.getNodes().map((n: any) => n.id),
//       {}
//     );
//   };
//   const zoomOut = () => {
//     nvlRef.current?.setZoom(nvlRef.current?.getScale() * 0.7);
//   };
//   const zoomIn = () => {
//     nvlRef.current?.setZoom(nvlRef.current?.getScale() * 1.3);
//   };
//   const findRelsByNodeId = (id: string): Relationship[] => {
//     return (
//       nvlRef.current
//         ?.getRelationships()
//         .filter((r) => r.from === id || r.to === id) ?? []
//     );
//   };
//   const mouseEventCallbacks: MouseEventCallbacks = {
//     onZoom: true,
//     onPan: true,
//     onDrag: true,
//     onHover: true,
//     onNodeClick(node, hitElements, event) {
//       console.log("select", node);
//       //node.selected = true;
//       if (expandedNodes[node.id]) {
//         const toNodeIds =
//           nvlRef.current
//             ?.getRelationships()
//             .filter((r) => r.from === node.id)
//             .map((r) => r.to) || [];
//         const fromNodeIds =
//           nvlRef.current
//             ?.getRelationships()
//             .filter((r) => r.to === node.id)
//             .map((r) => r.from) || [];
//         const nodesToRemove = [...fromNodeIds, ...toNodeIds];
//         const aa = nodesToRemove.filter((n) => findRelsByNodeId(n).length < 2);
//         nvlRef.current?.removeNodesWithIds(aa);
//         //setExpandedNodes({ ...expandedNodes, [node.id]: false });
//       } else {
//         fetchGraph(
//           "http://localhost:8080/api/graph/query",
//           `match p=(s where elementId(s)="${node.id}")-[]-() return p`
//         ).then((ddd) => {
//           console.log("load", ddd);
//           const nodeIds = nodes.map((n: any) => n.id);
//           const relIds = rels.map((n: any) => n.id);
//           const nn = ddd.nodes
//             .map(createNode)
//             .filter((n) => nodeIds.indexOf(n) === -1);
//           const rr = ddd.relationships
//             .map(createRel)
//             .filter((n) => relIds.indexOf(n) === -1);
//           nvlRef.current?.addAndUpdateElementsInGraph(nn, rr);
//           //node.captions = [...(node?.captions || []), { value: "[-]" }];
//           //setExpandedNodes({ ...expandedNodes, [node.id]: true });
//         });
//       }
//     },
//     onNodeDoubleClick(node, hitElements, event) {
//       //navigate(`/${node.id}s`);
//     },
//   };
//   const nvlCallbacks = {
//     onLayoutDone: () => {
//       console.log("onLayoutDone");
//     },
//     onLayoutComputing(isComputing: boolean) {
//       console.log("onLayoutComputing " + isComputing);
//       if (!isComputing) {
//         //zoomAll();
//       }
//     },
//   };
//   return (
//     <div className="flex-fill d-flex flex-column ">
//       <div style={{}} className="bg-light border1 rounded  w-100 h-100 mt-1">
//         <InteractiveNvlWrapper
//           ref={nvlRef}
//           nvlOptions={{
//             useWebGL: false,
//             renderer: "canvas",
//             minZoom: 1,
//             allowDynamicMinZoom: true,
//             initialZoom: 2,
//             layout: "d3Force",
//             //layoutTimeLimit: 10,
//           }}
//           nodes={nodes}
//           rels={rels}
//           mouseEventCallbacks={mouseEventCallbacks}
//           nvlCallbacks={nvlCallbacks}
//         >
//           <div
//             style={{
//               position: "absolute",
//               top: 20,
//               left: 20,
//               zIndex: 100000,
//             }}
//             className="p-1 rounded border shadow-sm bg-white1 "
//           >
//             <ButtonGroup vertical>
//               <Button variant="light" onClick={zoomAll}>
//                 <BsFullscreen />
//               </Button>
//               <Button variant="light" onClick={zoomIn}>
//                 <BsZoomIn />
//               </Button>
//               <Button variant="light" onClick={zoomOut}>
//                 <BsZoomOut />
//               </Button>
//             </ButtonGroup>
//           </div>
//         </InteractiveNvlWrapper>
//       </div>
//     </div>
//   );
// }
