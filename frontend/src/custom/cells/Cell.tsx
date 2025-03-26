import { Badge, Tab } from "react-bootstrap";
import { useParams } from "react-router-dom";

import { EmbeddedTable } from "../../msc/EmbeddedTable";
import { GraphTabTitle } from "../../msc/GraphTabTitle";
import {
  createMscSortKey,
  getMscPropValue,
  MscPropValue,
  mscUrl,
  multiValueProp,
  prop,
  useMscInstance,
  useMscObj,
} from "../../msc/MsgObj";

import { useDetaiModelBuilder } from "@clickapp/qui-core";
import { GraphView } from "../../clickapp-bootstrap/graph/GraphView";
import { useGraphContext } from "../../msc/MscGraphContext";
import { MscGraphStyles } from "../CustomGraphStyles";
import { encodeAsLabel } from "../../msc/graphUtils";
import MscObjPage from "../../msc/MscObjPage";
import { createLacLink } from "../lacs/LocationAreas";
import { useCellListColumns } from "../celllists/CellLists";
import { useLteColumns } from "../ltes/LteConfigs";
import { useGcaColumns } from "../gcas/Gcas";

function useDetailModel() {
  const mscId = useMscInstance();
  return useDetaiModelBuilder((b) => {
    b.addLine(multiValueProp("BTS", "NAME"));
    b.addLine(multiValueProp("BTS", "NUMBER"));
    b.separator();
    b.addLine(
      multiValueProp("LA", "NAME", {
        linkTo: (e) =>
          createLacLink(
            mscId,
            getMscPropValue(e, "LA", "LAC"),
            getMscPropValue(e, "MCC"),
            getMscPropValue(e, "MNC")
          ),
      })
    );
    b.addLine(multiValueProp("LA", "LAC"));

    b.block();
    b.addLine(prop("MCC"));
    b.addLine(prop("MNC"));
    b.addLine(prop("CI"));
    b.block();
    b.addLine(multiValueProp("BSC", "NAME"));
    b.addLine(multiValueProp("BSC", "NUMBER"));
  });
}

export function Cell() {
  const { mscId, id } = useParams();
  const url = mscUrl(mscId!, `cells/${id}`);

  const objData = useMscObj(url);

  const { graphAvailable } = useGraphContext();
  const defaultTab = graphAvailable ? "graph" : "settings";

  const detailModel = useDetailModel();
  const cellListColumns = useCellListColumns(false);
  const lteColumns = useLteColumns();
  const gcaColumns = useGcaColumns();

  return (
    <MscObjPage
      objData={objData}
      detailModel={detailModel}
      defaultTab={defaultTab}
      parentLabel="Cells"
      parentPath="/cells"
      title={(v) => <MscPropValue obj={v} name="BTS" hint="NAME" />}
      headerAddon={(v) => (
        <Badge bg="warning">
          <MscPropValue obj={v} name="BTS ADMINISTRATIVE STATE" />
        </Badge>
      )}
      tabs={(cell) =>
        graphAvailable
          ? graphTabs(mscId!, id!, cellListColumns, lteColumns, gcaColumns)
          : undefined
      }
    />
  );
}

function graphTabs(
  mscId: string,
  id: string,
  extendedColumns: any,
  lteColumns: any,
  gcaColumns: any
) {
  const mscLabel = encodeAsLabel(mscId);
  return [
    <Tab eventKey="graph" title={<GraphTabTitle title="Graph" />}>
      <GraphView
        styles={MscGraphStyles}
        queries={[
          `match p=(gca:BTS:${mscLabel} {NUMBER:'${id}'})--() return p`,
          `match p=(gca:BTS:${mscLabel} {NUMBER:'${id}'})--(:CELL_LIST:${mscLabel})--(:GCA:${mscLabel}) return p`,
          // `match p=(gca:BTS{NUMBER:'${id}'})-[]-(:CELL_LIST)--(:GCA) return p`,
        ]}
      />
    </Tab>,
    <Tab eventKey="clists" title={<GraphTabTitle title="Cell Lists" />}>
      {id && (
        <EmbeddedTable
          url={`/api/graph/msc/${mscId}/cells/${id}/rels/cell-lists`}
          columns={extendedColumns}
          initialSortKey={createMscSortKey("GCREF")}
        />
      )}
    </Tab>,
    <Tab eventKey="gcas" title={<GraphTabTitle title="GCAs" />}>
      {id && (
        <EmbeddedTable
          url={`/api/graph/msc/${mscId}/cells/${id}/rels/cell-lists,gcas`}
          columns={gcaColumns}
          initialSortKey={createMscSortKey("GCAC")}
        />
      )}
    </Tab>,
    <Tab eventKey="ltes" title={<GraphTabTitle title="LTE Configs" />}>
      {id && (
        <EmbeddedTable
          url={`/api/graph/msc/${mscId}/cells/${id}/rels/ltes`}
          columns={lteColumns}
          initialSortKey={createMscSortKey("GCAC")}
        />
      )}
    </Tab>,
  ];
}

// export function Cell1() {
//   const { mscId, id } = useParams();
//   const url = mscUrl(mscId!, `cells/${id}`);

//   const { graphAvailable } = useGraphContext();
//   const defaultTab = graphAvailable ? "graph" : "settings";

//   const { data, error, isPending, reload } = useMscObj(url);
//   const { data: obj, ...cmd } = data || {};

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
//           <Breadcrumb.Item linkAs={Link} linkProps={{ to: `/${mscId}/cells` }}>
//             Cells
//           </Breadcrumb.Item>
//           <Breadcrumb.Item active>
//             <MscPropValue obj={obj!} name="BTS" hint="NAME" />
//           </Breadcrumb.Item>
//         </Breadcrumb>
//       }
//       header={
//         <MscPageHeader
//           cmd={cmd}
//           reload={reload}
//           addon={
//             <Badge bg="warning">
//               <MscPropValue obj={obj!} name="BTS ADMINISTRATIVE STATE" />
//             </Badge>
//           }
//         >
//           <MscPropValue obj={obj!} name="BTS" hint="NAME" />
//         </MscPageHeader>
//       }
//     >
//       <DetailModelView model={detailModel} value={obj} context={context} />

//       <Tabs
//         defaultActiveKey={defaultTab}
//         id="uncontrolled-tab-example"
//         className="mt-4"
//       >
//         {graphAvailable && (
//           <Tab eventKey="graph" title={<GraphTabTitle title="Graph" />}>
//             <GraphView
//               queries={[
//                 `match p=(gca:BTS:${mscLabel} {NUMBER:'${id}'})--() return p`,
//                 `match p=(gca:BTS:${mscLabel} {NUMBER:'${id}'})--(:CELL_LIST:${mscLabel})--(:GCA:${mscLabel}) return p`,
//                 // `match p=(gca:BTS{NUMBER:'${id}'})-[]-(:CELL_LIST)--(:GCA) return p`,
//               ]}
//             />
//           </Tab>
//         )}

//         {graphAvailable && (
//           <Tab eventKey="clists" title={<GraphTabTitle title="Cell Lists" />}>
//             {id && (
//               <EmbeddedTable
//                 url={`/api/graph/msc/${mscId}/cells/${id}/cell-lists`}
//                 columns={extendedColumns}
//                 initialSortKey={createMscSortKey("GCREF")}
//               />
//             )}
//           </Tab>
//         )}

//         {graphAvailable && (
//           <Tab eventKey="gcas" title={<GraphTabTitle title="GCAs" />}>
//             {id && (
//               <EmbeddedTable
//                 url={`/api/graph/msc/${mscId}/cells/${id}/gcas`}
//                 columns={gcaColumns}
//                 initialSortKey={createMscSortKey("GCAC")}
//               />
//             )}
//           </Tab>
//         )}

//         {graphAvailable && (
//           <Tab eventKey="ltes" title={<GraphTabTitle title="LTE Configs" />}>
//             {id && (
//               <EmbeddedTable
//                 url={`/api/graph/msc/${mscId}/cells/${id}/lte`}
//                 columns={columns}
//                 initialSortKey={createMscSortKey("GCAC")}
//               />
//             )}
//           </Tab>
//         )}

//         <Tab eventKey="settings" title={<>Settings</>}>
//           <MscObjView
//             obj={obj!}
//             fields={[CellFields.lacName]}
//             context={context}
//           />
//         </Tab>

//         {/* <Tab eventKey="tra" title={<>Traffic</>}>
//               <MscObjSectionView
//                 obj={obj}
//                 section={obj.sections[3]}
//                 fields={[CellFields.lacName]}
//               />
//             </Tab>

//             <Tab eventKey="loc" title={<>Location</>}>
//               <MscObjSectionView
//                 obj={obj}
//                 section={obj.sections[2]}
//                 fields={[CellFields.lacName]}
//               />
//             </Tab>

//             <Tab eventKey="res" title={<>Resource</>}>
//               <MscObjSectionView
//                 obj={obj}
//                 section={obj.sections[4]}
//                 fields={[CellFields.lacName]}
//               />
//             </Tab>

//             <Tab eventKey="misc" title={<>Misc</>}>
//               <MscObjSectionView
//                 obj={obj}
//                 section={obj.sections[1]}
//                 fields={[CellFields.lacName]}
//               />
//             </Tab> */}
//       </Tabs>
//     </Page>
//   );
// }

// IDE ... IDENTIFICATION PARAMETERS
// SIDE .. SHORT IDENTIFICATION PARAMETERS
// RES ... RESOURCE INDICATION PARAMETERS
// TRA ... TRAFFIC REASON HANDOVER PARAMETERS
// NEI ... NEIGHBOUR BTS LIST
// LOC ... LOCATION INFORMATION PARAMETERS
// MIS ... MISCELLANEOUS PARAMETERS
// ALL ... ALL PARAMETERS
