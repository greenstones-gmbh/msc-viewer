import { Outlet, useOutletContext } from "react-router-dom";

import { OpenModalButton } from "@clickapp/qui-bootstrap";
import { MscListPage } from "../../msc/MscListPage";
import {
  getMscPropValue,
  MscObj,
  MscObjListResult,
  mscUrl,
  useMscInstance,
  useMscObjColumnBuilder,
  useMscObjList,
} from "../../msc/MsgObj";
import { createBtsLink } from "../cells/Cells";
import { LteConfigModal } from "./LteConfigModal";

export function createLteConfigLink(
  mscId: string,
  eci: string,
  emcc: string,
  emnc: string
) {
  return `/${mscId}/ltes/ECI=${eci},EMCC=${emcc},EMNC=${emnc}`;
}

// export const LteConfigFields = {
//   eci: prop("ECI", {
//     linkTo: (e) =>
//       createLteConfigLink(
//         "context.mscId",
//         getMscPropValue(e, "ECI"),
//         getMscPropValue(e, "EMCC"),
//         getMscPropValue(e, "EMNC")
//       ),
//   }),

//   bts: prop("BTS NUMBER", {
//     linkTo: (v) =>
//       createBtsLink("context.mscId", getMscPropValue(v, "BTS NUMBER")),
//   }),
// };

// const columns = createColumns<MscObj>((builder) => {
//   builder.add(
//     column(LteConfigFields.eci, {
//       width: "5em",
//     })
//   );
//   builder.add(valueColumn("EMCC", { width: "5em" }));
//   builder.add(valueColumn("EMNC", { width: "5em" }));

//   builder.add(
//     column(LteConfigFields.bts, {
//       width: "5em",
//       header: "BTS",
//     })
//   );

//   builder.add(valueColumn("NAME", { header: "BTS Name" }));

//   builder.add(valueColumn("MCC", { width: "5em" }));
//   builder.add(valueColumn("MNC", { width: "5em" }));
//   builder.add(valueColumn("CI", { width: "5em" }));
// });

export function useLteColumns(plain = false) {
  const mscId = useMscInstance();
  return useMscObjColumnBuilder((builder) => {
    const linkToLte = plain
      ? undefined
      : (e: MscObj) =>
          createLteConfigLink(
            mscId,
            getMscPropValue(e, "ECI"),
            getMscPropValue(e, "EMCC"),
            getMscPropValue(e, "EMNC")
          );

    const linkToBts = plain
      ? undefined
      : (e: MscObj) => createBtsLink(mscId, getMscPropValue(e, "BTS NUMBER"));

    builder.value("ECI", {
      width: "5em",
      linkTo: linkToLte,
    });
    builder.value("EMCC", { width: "5em" });
    builder.value("EMNC", { width: "5em" });

    builder.value("BTS NUMBER", {
      width: "5em",
      header: "BTS",
      linkTo: linkToBts,
    });

    builder.value("NAME", { header: "BTS Name" });
    builder.value("MCC", { width: "5em" });
    builder.value("MNC", { width: "5em" });
    builder.value("CI", { width: "5em" });
  });
}

export function LteConfigs() {
  const listData = useOutletContext<MscObjListResult>();
  const columns = useLteColumns();
  const mscId = useMscInstance();

  return (
    <MscListPage
      title="LTE Configs"
      cols={columns}
      csvFileName="LTE"
      listData={listData}
      toolbarAddons={
        <OpenModalButton
          size={"sm"}
          label="Create LTE Config"
          modal={(close) => (
            <LteConfigModal handleClose={close} mscId={mscId} />
          )}
        />
      }
    />
  );
}

export function LteConfigsContainer() {
  const mscId = useMscInstance();
  const url = mscUrl(mscId, "ltes");

  const list = useMscObjList(url, {
    initialSort: { id: "MCC", direction: "asc" },
    paging: true,
  });
  return <Outlet context={list} />;
}
