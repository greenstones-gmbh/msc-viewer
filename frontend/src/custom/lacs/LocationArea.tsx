import { Tab } from "react-bootstrap";
import { useParams } from "react-router-dom";

import { createDetails, filterColumns } from "@clickapp/qui-core";
import { GraphView } from "../../clickapp-bootstrap/graph/GraphView";
import { EmbeddedTable } from "../../msc/EmbeddedTable";
import { GraphTabTitle } from "../../msc/GraphTabTitle";
import { useGraphContext } from "../../msc/MscGraphContext";
import { MscGraphStyles } from "../CustomGraphStyles";
import { encodeAsLabel } from "../../msc/graphUtils";
import {
  createMscSortKey,
  MscObj,
  MscPropValue,
  mscUrl,
  multiValueProp,
  prop,
  useMscInstance,
  useMscObj,
} from "../../msc/MsgObj";

import MscObjPage from "../../msc/MscObjPage";
import { parseIdParams } from "../../msc/types/utils";
import { useBtsColumns } from "../cells/Cells";

const detailModel = createDetails<MscObj>((b) => {
  b.addLine(multiValueProp("LA", "NAME"));
  b.addLine(multiValueProp("LA", "LAC"));
  b.block();
  b.addLine(prop("MCC"));
  b.addLine(prop("MNC"));
});

export function LocationArea() {
  const { id } = useParams();
  const mscId = useMscInstance();
  const url = mscUrl(mscId!, `lacs/${id}`);

  console.log("LocationAreas", url);

  const objData = useMscObj(url);

  const { graphAvailable } = useGraphContext();
  const defaultTab = graphAvailable ? "graph" : "settings";

  const idParams = parseIdParams(id || "");

  const mscLabel = encodeAsLabel(mscId);

  const btsColumns = useBtsColumns(mscId);

  return (
    <MscObjPage
      objData={objData}
      detailModel={detailModel}
      defaultTab={defaultTab}
      parentLabel="Location Areas"
      parentPath="/lacs"
      title={(cell) => <MscPropValue obj={cell} name="LA" hint="NAME" />}
      tabs={(cell) =>
        graphAvailable
          ? [
              <Tab eventKey="graph" title={<GraphTabTitle title="Graph" />}>
                <GraphView
                  styles={MscGraphStyles}
                  queries={[
                    `match p=(gca:LAC:${mscLabel} {LAC:'${idParams.LAC}', MCC:'${idParams.MCC}', MNC:'${idParams.MNC}'})--(:${mscLabel}) return p`,
                    // `match p=(gca:BTS{NUMBER:'${id}'})-[]-(:CELL_LIST)--(:GCA) return p`,
                  ]}
                />
              </Tab>,
              <Tab
                eventKey="cells"
                title={<GraphTabTitle title="Cells and BTSs" />}
              >
                {id && (
                  <EmbeddedTable
                    //url={`/msc-viewer/api/graph/msc/${mscId}/lacs/${idParams.LAC}/${idParams.MCC}/${idParams.MNC}/btss`}
                    url={`/msc-viewer/api/graph/msc/${mscId}/lacs/${id}/rels/cells`}
                    columns={filterColumns(btsColumns, ["LAC", "LA NAME"])}
                    initialSortKey={createMscSortKey("BTS", "NUMBER")}
                  />
                )}
              </Tab>,
            ]
          : undefined
      }
    />
  );
}

// export function LocationArea1() {
//   const { mscId, id } = useParams();
//   const url = mscUrl(mscId!, `lacs/${id}`);

//   const { graphAvailable } = useGraphContext();
//   const defaultTab = graphAvailable ? "graph" : "settings";

//   const { data, reload, isError, error, isPending } = useMscObj(url);
//   const { data: cell, ...cmd } = data || {};

//   console.log("LocationArea", { id, isError, error });

//   const idParams = id?.split(",").reduce((p, c) => {
//     const parts = c.split("=");
//     p[parts[0]] = parts[1];
//     return p;
//   }, {} as any);

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
//           <Breadcrumb.Item linkAs={Link} linkProps={{ to: `/${mscId}/lacs` }}>
//             Location Areas
//           </Breadcrumb.Item>
//           <Breadcrumb.Item active>
//             <MscPropValue obj={cell!} name="LA" hint="NAME" />
//           </Breadcrumb.Item>
//         </Breadcrumb>
//       }
//       header={
//         <MscPageHeader cmd={cmd} reload={reload}>
//           <MscPropValue obj={cell!} name="LA" hint="NAME" />
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
//               queries={[
//                 `match p=(gca:LAC:${mscLabel} {LAC:'${idParams.LAC}', MCC:'${idParams.MCC}', MNC:'${idParams.MNC}'})--(:${mscLabel}) return p`,
//                 // `match p=(gca:BTS{NUMBER:'${id}'})-[]-(:CELL_LIST)--(:GCA) return p`,
//               ]}
//             />
//           </Tab>
//         )}

//         {graphAvailable && (
//           <Tab
//             eventKey="cells"
//             title={<GraphTabTitle title="Cells and BTSs" />}
//           >
//             {id && (
//               <EmbeddedTable
//                 url={`/api/graph/msc/${mscId}/lacs/${idParams.LAC}/${idParams.MCC}/${idParams.MNC}/btss`}
//                 columns={filterColumns(btsColumns, ["LAC", "LA NAME"])}
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
