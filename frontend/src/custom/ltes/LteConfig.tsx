import { Tab } from "react-bootstrap";
import { useParams } from "react-router-dom";

import {
  getMscPropValue,
  MscObj,
  MscPropValue,
  mscUrl,
  prop,
  useMscInstance,
  useMscObj,
} from "../../msc/MsgObj";

import { useDetaiModelBuilder } from "@clickapp/qui-core";
import { GraphView } from "../../clickapp-bootstrap/graph/GraphView";
import { GraphTabTitle } from "../../msc/GraphTabTitle";
import { useGraphContext } from "../../msc/MscGraphContext";
import { MscGraphStyles } from "../CustomGraphStyles";
import { encodeAsLabel } from "../../msc/graphUtils";
import MscObjPage from "../../msc/MscObjPage";
import { createLacLink } from "../lacs/LocationAreas";
import { createBtsLink } from "../cells/Cells";

function useDetailModel() {
  const mscId = useMscInstance();
  return useDetaiModelBuilder((b) => {
    b.addLine(
      prop("BTS NUMBER", {
        linkTo: (e: MscObj) =>
          createBtsLink(mscId, getMscPropValue(e, "BTS NUMBER")),
      })
    );
    b.addLine(prop("BTS NAME"));
    b.block();
    b.addLine(prop("CI"));
    b.addLine(prop("MCC"));
    b.addLine(prop("MNC"));
    b.addLine(
      prop("LAC", {
        linkTo(entity) {
          return createLacLink(
            "context.mscId",
            getMscPropValue(entity, "LAC"),
            getMscPropValue(entity, "MCC"),
            getMscPropValue(entity, "MNC")
          );
        },
      })
    );
    b.block();

    b.addLine(prop("ECI"));
    b.addLine(prop("EMCC"));
    b.addLine(prop("EMNC"));
  });
}

export function LteConfig() {
  const { mscId, id } = useParams();
  const url = mscUrl(mscId!, `ltes/${id}`);

  const objData = useMscObj(url);
  const detailModel = useDetailModel();

  const { graphAvailable } = useGraphContext();
  const defaultTab = graphAvailable ? "graph" : "settings";

  return (
    <MscObjPage
      objData={objData}
      detailModel={detailModel}
      defaultTab={defaultTab}
      parentLabel="LTE Configs"
      parentPath="/ltes"
      title={(v) => (
        <>
          ECI
          <MscPropValue obj={v} name="ECI" />
        </>
      )}
      tabs={(cell) =>
        graphAvailable ? graphTabs(mscId!, id!, cell) : undefined
      }
    />
  );
}

function graphTabs(mscId: string, id: string, v?: MscObj) {
  const mscLabel = encodeAsLabel(mscId);
  if (!v) return undefined;
  return [
    <Tab eventKey="graph" title={<GraphTabTitle title="Graph" />}>
      <GraphView
        styles={MscGraphStyles}
        queries={[
          `match p=(gca:LTEConfig:${mscLabel} {ECI:'${getMscPropValue(
            v,
            "ECI"
          )}',EMCC:'${getMscPropValue(v, "EMCC")}',EMNC:'${getMscPropValue(
            v,
            "EMNC"
          )}'})--(:${mscLabel}) return p`,
        ]}
      />
    </Tab>,
  ];
}

// export function LteConfig() {
//   const { id, mscId } = useParams();
//   const url = mscUrl(mscId!, `ltes/${id}`);

//   const { graphAvailable } = useGraphContext();
//   const defaultTab = graphAvailable ? "graph" : "settings";

//   const { data, isPending, error, reload } = useMscObj(url);

//   const { data: cell, ...cmd } = data || {};

//   if (!cell && !error) return null;

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
//           <Breadcrumb.Item linkAs={Link} linkProps={{ to: `/${mscId}/ltes` }}>
//             LTE Configs
//           </Breadcrumb.Item>
//           <Breadcrumb.Item active>
//             ECI {getMscPropValue(cell!, "ECI")}
//           </Breadcrumb.Item>
//         </Breadcrumb>
//       }
//       // header={"ECI " + getMscPropValue(cell!, "ECI")}
//       header={
//         <MscPageHeader cmd={cmd} reload={reload}>
//           ECI
//           <MscPropValue obj={cell!} name="ECI" />
//         </MscPageHeader>
//       }
//     >
//       <DetailModelView model={detailModel} value={cell} context={context} />

//       <Tabs defaultActiveKey={defaultTab} className="mt-4">
//         {graphAvailable && (
//           <Tab eventKey="graph" title={<GraphTabTitle title="Graph" />}>
//             <GraphView
//               queries={[
//                 `match p=(gca:LTEConfig:${mscLabel} {ECI:'${getMscPropValue(
//                   cell!,
//                   "ECI"
//                 )}',EMCC:'${getMscPropValue(
//                   cell!,
//                   "EMCC"
//                 )}',EMNC:'${getMscPropValue(
//                   cell!,
//                   "EMNC"
//                 )}'})--(:${mscLabel}) return p`,
//               ]}
//             />
//           </Tab>
//         )}
//         <Tab eventKey="settings" title={<>Settings</>}>
//           <MscObjView obj={cell!} context={context} />
//         </Tab>
//       </Tabs>
//     </Page>
//   );
// }
