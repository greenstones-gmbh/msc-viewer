import { useParams } from "react-router-dom";
import { BaseGraphView } from "../clickapp-bootstrap/graph/BaseGraphView";
import { IGraph, IGraphStyles } from "../clickapp-bootstrap/graph/Graph";

import { Page, PageHeader } from "@clickapp/qui-bootstrap";

export default function DashboardPage({
  graph,
  styles,
}: {
  graph: IGraph;
  styles: IGraphStyles;
}) {
  const { mscId } = useParams();
  return (
    <Page header={<PageHeader>{mscId} Schema</PageHeader>}>
      <BaseGraphView graph={graph} styles={styles} />
    </Page>
  );
}
