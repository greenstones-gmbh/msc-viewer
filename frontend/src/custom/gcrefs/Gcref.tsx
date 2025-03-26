import { Tab } from "react-bootstrap";
import { useParams } from "react-router-dom";

import { GraphTabTitle } from "../../msc/GraphTabTitle";
import {
  getMscPropValue,
  MscPropValue,
  mscUrl,
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
import { createGcaLink } from "../gcas/Gcas";
import { parseIdParams } from "../../msc/types/utils";

function useDetailModel() {
  const mscId = useMscInstance();
  return useDetaiModelBuilder((b) => {
    b.addLine(prop("GCREF"));
    b.addLine(prop("SERVICE TYPE"));
    b.block();
    b.addLine(prop("GCAN"));
    b.addLine(
      prop("GCAC", {
        linkTo: (e) => createGcaLink(mscId, getMscPropValue(e, "GCAC")),
      })
    );
    b.block();
    b.addLine(prop("GROUP NAME"));
    b.addLine(prop("GROUP ID"));
  });
}

export function Gcref() {
  const { id, mscId } = useParams();
  const url = mscUrl(mscId!, `gcrefs/${id}`);

  const objData = useMscObj(url);

  const { graphAvailable } = useGraphContext();
  const detailModel = useDetailModel();
  const defaultTab = graphAvailable ? "graph" : "settings";

  return (
    <MscObjPage
      objData={objData}
      detailModel={detailModel}
      defaultTab={defaultTab}
      parentLabel="Group Call Refs"
      parentPath="/gcrefs"
      title={(v) => (
        <>
          GCREF <MscPropValue obj={v} name="GCREF" />
        </>
      )}
      tabs={(cell) => (graphAvailable ? graphTabs(mscId!, id!) : undefined)}
    />
  );
}

function graphTabs(mscId: string, id: string) {
  const idParams = parseIdParams(id || "");
  const mscLabel = encodeAsLabel(mscId);
  return [
    <Tab eventKey="graph" title={<GraphTabTitle title="Graph" />}>
      <GraphView
        styles={MscGraphStyles}
        query={`match  p=(:GCREF:${mscLabel} {GCREF:'${idParams.GCREF}',STYPE:'${idParams.STYPE}'})--(:${mscLabel}) return p`}
      />
    </Tab>,
  ];
}

// export function Gcref1() {
//   const { id, mscId, stype } = useParams();
//   const url = mscUrl(mscId!, `gcrefs/${id}/${stype}`);

//   const { graphAvailable } = useGraphContext();
//   const defaultTab = graphAvailable ? "graph" : "settings";

//   const context: MscContext = { mscId: mscId || "-" };

//   const { data, isPending, error, reload } = useMscObj(url);

//   const { data: cell, ...cmd } = data || {};
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
//           <Breadcrumb.Item linkAs={Link} linkProps={{ to: `/${mscId}/gcrefs` }}>
//             Group Call Refs
//           </Breadcrumb.Item>
//           <Breadcrumb.Item active>
//             GCREF <MscPropValue obj={cell!} name="GCREF" />
//           </Breadcrumb.Item>
//         </Breadcrumb>
//       }
//       header={
//         <MscPageHeader cmd={cmd} reload={reload}>
//           GCREF <MscPropValue obj={cell!} name="GCREF" />
//         </MscPageHeader>
//       }
//     >
//       <DetailModelView model={detailModel} value={cell} context={context} />

//       <Tabs defaultActiveKey={defaultTab} className="mt-4">
//         {graphAvailable && (
//           <Tab eventKey="graph" title={<GraphTabTitle title="Graph" />}>
//             <GraphView
//               query={`match  p=(:GCREF:${mscLabel} {GCREF:'${id}',STYPE:'${stype}'})--(:${mscLabel}) return p`}
//             />
//           </Tab>
//         )}

//         <Tab eventKey="settings" title={<>All Settings</>}>
//           <MscObjView obj={cell!} context={context} />
//         </Tab>
//       </Tabs>
//     </Page>
//   );
// }
