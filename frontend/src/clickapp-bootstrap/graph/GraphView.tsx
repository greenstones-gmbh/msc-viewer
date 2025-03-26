import { useParams } from "react-router-dom";

import { encodeAsLabel } from "../../msc/graphUtils";
import { MscContext } from "../../msc/MsgObj";
import { BaseGraphView } from "./BaseGraphView";
import { IGraph, IGraphStyles } from "./Graph";

import { fetchJson, useFetch } from "@clickapp/qui-core";
import { Spinner } from "react-bootstrap";

const GRAPH_URL = "/msc-viewer/api/graph/query";

const fetchGraph = async (url: string, query: string): Promise<IGraph> => {
  return await fetchJson(
    url +
      "?" +
      new URLSearchParams({
        q: query,
      })
  );
};

export function GraphView({
  query,
  queries,
  styles,
}: {
  query?: string;
  queries?: string[];
  styles: IGraphStyles;
}) {
  const { mscId } = useParams();

  const context: MscContext = { mscId: mscId || "-" };

  const q = query || queries?.join(";") || "";

  const qqq =
    GRAPH_URL +
    "?" +
    new URLSearchParams({
      q,
    });

  console.log("Graph", query);

  const { data, isPending } = useFetch(qqq, {}, [qqq]);
  //if (isPending || !data) return <div>Loading....</div>;
  const mscLabel = encodeAsLabel(mscId);
  return (
    <>
      {!isPending && (
        <BaseGraphView
          graph={data || { nodes: [], relationships: [] }}
          styles={styles}
          context={context}
          expandFn={async (n) =>
            fetchGraph(
              GRAPH_URL,
              `match p=(s where elementId(s)="${n.id}")-[]-(:${mscLabel}) return p`
            )
          }
        />
      )}

      {isPending && (
        <div className="my-auto mx-auto ">
          <div
            className="text-center text-secondary bg-white p-4 rounded "
            style={{ marginTop: -100 }}
          >
            <span>
              <Spinner animation="grow" variant="secondary" />
              <br />
              Please wait...
            </span>
          </div>
        </div>
      )}
    </>
  );
}
