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
import { useGcrefColumns } from "../gcrefs/Gcrefs";

const detailModel = createDetails<MscObj>((b) => {
  b.addLine(prop("GCAC"));
  b.block();
  b.addLine(prop("GCAN"));
});

export function Gca() {
  const { id } = useParams();
  const mscId = useMscInstance();
  const url = mscUrl(mscId, `gcas/${id}`);

  const objData = useMscObj(url);

  const { graphAvailable } = useGraphContext();
  const defaultTab = graphAvailable ? "graph" : "settings";

  const btsColumns = useBtsColumns(mscId);
  const gcarefColumns = useGcrefColumns();

  return (
    <MscObjPage
      objData={objData}
      detailModel={detailModel}
      defaultTab={defaultTab}
      parentLabel="Group Call Areas"
      parentPath="/gcas"
      title={(v) => <MscPropValue obj={v} name="GCAN" />}
      tabs={(cell) =>
        graphAvailable
          ? graphTabs(mscId!, id!, btsColumns, gcarefColumns)
          : undefined
      }
    />
  );
}

function graphTabs(
  mscId: string,
  id: string,
  btsColumns: any,
  gcarefColumns: any
) {
  const mscLabel = encodeAsLabel(mscId);
  return [
    <Tab eventKey="graph" title={<GraphTabTitle title="Graph" />}>
      <GraphView
        styles={MscGraphStyles}
        //query={`match (gca:GCA{GCAC:'${id}'}), p=(gca)-[r]-(m:CELL_LIST)-[]-(b:BTS), v=(gca)-[]-(:GCREF)  return p,v`}
        //query={`match (gca:GCA:${mscLabel} {GCAC:'${id}'}), p=(gca)-[r]-(m:CELL_LIST:${mscLabel}), v=(gca)-[]-(:GCREF:${mscLabel})  return p,v`}
        query={`match (gca:GCA:${mscLabel} {GCAC:'${id}'}), p=(gca)-[r]-(m:${mscLabel}) return p`}
      />
    </Tab>,
    <Tab eventKey="cgrefs" title={<GraphTabTitle title="Group Call Refs" />}>
      {id && (
        <EmbeddedTable
          url={`/api/graph/msc/${mscId}/gcas/${id}/rels/gcrefs`}
          columns={gcarefColumns.filter(
            (v: any, index: number) => !(index === 1 || index === 2)
          )}
          initialSortKey={createMscSortKey("GCREF")}
        />
      )}
    </Tab>,
    <Tab eventKey="btss" title={<GraphTabTitle title="BTSs" />}>
      {id && (
        <EmbeddedTable
          url={`/api/graph/msc/${mscId}/gcas/${id}/rels/cell-lists,cells`}
          columns={btsColumns}
          initialSortKey={createMscSortKey("BTS", "NUMBER")}
        />
      )}
    </Tab>,
  ];
}

// export function Gca1() {
//   const { id, mscId } = useParams();
//   const url = mscUrl(mscId!, `gcas/${id}`);

//   const { graphAvailable } = useGraphContext();
//   const defaultTab = graphAvailable ? "graph" : "settings";

//   const { data, reload, error, isPending } = useMscObj(url);

//   const { data: cell, ...cmd } = data || {};

//   const context: MscContext = { mscId: mscId || "-" };
//   const mscLabel = encodeAsLabel(mscId);

//   return (
//     <Page
//       loading={isPending}
//       error={error}
//       breadcrumb={
//         <Breadcrumb>
//           <Breadcrumb.Item linkAs={Link} linkProps={{ to: `/${mscId}` }}>
//             {mscId}
//           </Breadcrumb.Item>
//           <Breadcrumb.Item linkAs={Link} linkProps={{ to: `/${mscId}/gcas` }}>
//             Group Call Areas
//           </Breadcrumb.Item>
//           <Breadcrumb.Item active>
//             <MscPropValue obj={cell!} name="GCAN" />
//           </Breadcrumb.Item>
//         </Breadcrumb>
//       }
//       header={
//         <MscPageHeader cmd={cmd} reload={reload}>
//           <PageHeader>
//             <MscPropValue obj={cell!} name="GCAN" />
//           </PageHeader>
//         </MscPageHeader>
//       }
//     >
//       <DetailModelView model={detailModel} value={cell} context={context} />

//       <Tabs
//         defaultActiveKey={defaultTab}
//         id="uncontrolled-tab-example"
//         className="mt-4"
//       >
//         {graphAvailable && (
//           <Tab eventKey="graph" title={<GraphTabTitle title="Graph" />}>
//             <GraphView
//               //query={`match (gca:GCA{GCAC:'${id}'}), p=(gca)-[r]-(m:CELL_LIST)-[]-(b:BTS), v=(gca)-[]-(:GCREF)  return p,v`}
//               //query={`match (gca:GCA:${mscLabel} {GCAC:'${id}'}), p=(gca)-[r]-(m:CELL_LIST:${mscLabel}), v=(gca)-[]-(:GCREF:${mscLabel})  return p,v`}

//               query={`match (gca:GCA:${mscLabel} {GCAC:'${id}'}), p=(gca)-[r]-(m:${mscLabel}) return p`}
//             />
//           </Tab>
//         )}

//         {graphAvailable && (
//           <Tab
//             eventKey="cgrefs"
//             title={<GraphTabTitle title="Group Call Refs" />}
//           >
//             {id && (
//               <EmbeddedTable
//                 url={`/api/graph/msc/${mscId}/gcas/${id}/gcrefs`}
//                 columns={cgrefColumns.filter(
//                   (v, index) => !(index === 1 || index === 2)
//                 )}
//                 initialSortKey={createMscSortKey("GCREF")}
//               />
//             )}
//           </Tab>
//         )}

//         {graphAvailable && (
//           <Tab eventKey="btss" title={<GraphTabTitle title="BTSs" />}>
//             {id && (
//               <EmbeddedTable
//                 url={`/api/graph/msc/${mscId}/gcas/${id}/btss`}
//                 columns={btsColumns}
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
