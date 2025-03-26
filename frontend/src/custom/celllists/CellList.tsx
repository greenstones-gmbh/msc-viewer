import { Tab } from "react-bootstrap";
import { useParams } from "react-router-dom";

import { EmbeddedTable } from "../../msc/EmbeddedTable";
import { GraphTabTitle } from "../../msc/GraphTabTitle";
import {
  createMscSortKey,
  MscObj,
  MscPropValue,
  mscUrl,
  prop,
  useMscInstance,
  useMscObj,
} from "../../msc/MsgObj";

import { createDetails } from "@clickapp/qui-core";
import { GraphView } from "../../clickapp-bootstrap/graph/GraphView";
import { useGraphContext } from "../../msc/MscGraphContext";
import { MscGraphStyles } from "../CustomGraphStyles";
import { encodeAsLabel } from "../../msc/graphUtils";
import MscObjPage from "../../msc/MscObjPage";
import { useBtsColumns } from "../cells/Cells";
import { useGcaColumns } from "../gcas/Gcas";

const detailModel = createDetails<MscObj>((b) => {
  b.addLine(prop("CLNAME"));
  b.block();
  b.addLine(prop("CLID"));
});

export function CellList() {
  const { id } = useParams();
  const mscId = useMscInstance();

  const url = mscUrl(mscId, `cell-lists/${id}`);

  const objData = useMscObj(url);

  const { graphAvailable } = useGraphContext();
  const defaultTab = graphAvailable ? "graph" : "settings";

  const cellColumns = useBtsColumns(mscId);
  const gcaColumns = useGcaColumns();

  return (
    <MscObjPage
      objData={objData}
      detailModel={detailModel}
      defaultTab={defaultTab}
      parentLabel="Cell Lists"
      parentPath="/cell-lists"
      title={(v) => <MscPropValue obj={v} name="CELL LIST NAME" />}
      tabs={(cell) =>
        graphAvailable
          ? graphTabs(mscId!, id!, cellColumns, gcaColumns)
          : undefined
      }
    />
  );
}

function graphTabs(
  mscId: string,
  id: string,
  cellColumns: any,
  gcaColumns: any
) {
  const mscLabel = encodeAsLabel(mscId);
  return [
    <Tab eventKey="graph" title={<GraphTabTitle title="Graph" />}>
      <GraphView
        styles={MscGraphStyles}
        query={`match p=(:CELL_LIST:${mscLabel} {CLNAME:'${id}'})--(:${mscLabel}) return p`}
      />
    </Tab>,
    <Tab eventKey="gcas" title={<GraphTabTitle title="GCAs" />}>
      {id && (
        <EmbeddedTable
          url={`/api/graph/msc/${mscId}/cell-lists/${id}/rels/gcas`}
          columns={gcaColumns}
          initialSortKey={createMscSortKey("BTS", "NUMBER")}
        />
      )}
    </Tab>,
    <Tab eventKey="btss" title={<GraphTabTitle title="BTSs" />}>
      {id && (
        <EmbeddedTable
          url={`/api/graph/msc/${mscId}/cell-lists/${id}/rels/cells`}
          columns={cellColumns}
          initialSortKey={createMscSortKey("BTS", "NUMBER")}
        />
      )}
    </Tab>,
  ];
}

// export function __CellList1() {
//   const { id, mscId } = useParams();
//   const url = mscUrl(mscId!, `cell-lists/${id}`);

//   const { graphAvailable } = useGraphContext();
//   const defaultTab = graphAvailable ? "graph" : "settings";

//   const { data, reload, isPending, error } = useMscObj(url);

//   const { data: cell, ...cmd } = data || {};

//   console.log("Gca", id, data);
//   const context: MscContext = { mscId: mscId || "-" };
//   const mscLabel = encodeAsLabel(mscId);
//   const cellColumns = useBtsColumns();
//   return (
//     <Page
//       loading={isPending}
//       error={error}
//       breadcrumb={
//         <Breadcrumb>
//           <Breadcrumb.Item linkAs={Link} linkProps={{ to: `/${mscId}` }}>
//             {mscId}
//           </Breadcrumb.Item>
//           <Breadcrumb.Item
//             linkAs={Link}
//             linkProps={{ to: `/${mscId}/cell-lists` }}
//           >
//             Cell Lists
//           </Breadcrumb.Item>
//           <Breadcrumb.Item active>
//             <MscPropValue obj={cell!} name="CELL LIST NAME" />
//           </Breadcrumb.Item>
//         </Breadcrumb>
//       }
//       header={
//         <MscPageHeader cmd={cmd} reload={reload}>
//           <MscPropValue obj={cell!} name="CELL LIST NAME" />
//         </MscPageHeader>
//       }
//     >
//       <DetailModelView model={detailModel} value={cell} />

//       <Tabs defaultActiveKey={defaultTab} className="mt-4">
//         {graphAvailable && (
//           <Tab eventKey="graph" title={<GraphTabTitle title="Graph" />}>
//             <GraphView
//               query={`match p=(:CELL_LIST:${mscLabel} {CLNAME:'${id}'})--(:${mscLabel}) return p`}
//             />
//           </Tab>
//         )}

//         {graphAvailable && (
//           <Tab eventKey="gcas" title={<GraphTabTitle title="GCAs" />}>
//             {id && (
//               <EmbeddedTable
//                 url={`/api/graph/msc/${mscId}/cell-lists/${id}/gcas`}
//                 columns={gcaColumns}
//                 initialSortKey={createMscSortKey("BTS", "NUMBER")}
//               />
//             )}
//           </Tab>
//         )}

//         {graphAvailable && (
//           <Tab eventKey="btss" title={<GraphTabTitle title="BTSs" />}>
//             {id && (
//               <EmbeddedTable
//                 url={`/api/graph/msc/${mscId}/cell-lists/${id}/btss`}
//                 columns={cellColumns}
//                 initialSortKey={createMscSortKey("BTS", "NUMBER")}
//               />
//             )}
//           </Tab>
//         )}

//         <Tab eventKey="settings" title={<>Settings</>}>
//           <MscObjView obj={cell!} context={context} />
//         </Tab>
//       </Tabs>
//     </Page>
//   );
// }
