import { Outlet, useOutletContext } from "react-router-dom";

import { MscListPage } from "../../msc/MscListPage";
import {
  createMscSortKey,
  getMscPropValue,
  MscObj,
  MscObjListResult,
  mscUrl,
  useMscInstance,
  useMscObjColumnBuilder,
  useMscObjList,
} from "../../msc/MsgObj";
import { createLacLink } from "../lacs/LocationAreas";
import { ConfigTypeContainer } from "../../msc/types/ConfigTypeContainer";

export function createBtsLink(mscId: string, btsNumber: string) {
  return `/${mscId}/cells/${btsNumber}`;
}

export function useBtsColumns(mscId: string, plain = false) {
  return useMscObjColumnBuilder((builder) => {
    const linkToLac = plain
      ? undefined
      : (e: MscObj) =>
          createLacLink(
            mscId,
            getMscPropValue(e, "LA", "LAC"),
            getMscPropValue(e, "MCC"),
            getMscPropValue(e, "MNC")
          );

    const linkToBts = plain
      ? undefined
      : (e: MscObj) =>
          createBtsLink(mscId, getMscPropValue(e, "BTS", "NUMBER"));

    builder
      .multiValue("BTS", "NUMBER", { width: "5em", header: "BTS" })
      .multiValue("BTS", "NAME", { linkTo: linkToBts })
      .multiValue("BSC", "NUMBER", { width: "5em", header: "BSC" })
      .multiValue("BSC", "NAME", { width: "5em" })
      .multiValue("LA", "LAC", { width: "5em", header: "LAC" })
      .multiValue("LA", "NAME", { linkTo: linkToLac, width: "5em" })

      .value("MCC", {
        width: "5em",
      })

      .value("MNC", {
        width: "5em",
      })

      .value("CI", {
        width: "5em",
      })

      .value("BTS ADMINISTRATIVE STATE", {
        width: "5em",
        header: "State",
      });
  });
}

export function Cells() {
  const listData = useOutletContext<MscObjListResult>();
  const mscId = useMscInstance();
  const btsColumns = useBtsColumns(mscId);
  return (
    <MscListPage
      title="Cells"
      cols={btsColumns}
      csvFileName="BTS"
      listData={listData}
    />
  );
}

export function useCellList(mscId: string) {
  const url = mscUrl(mscId, "cells");
  const list = useMscObjList(url, {
    initialSort: { id: createMscSortKey("BTS", "NUMBER"), direction: "asc" },
    paging: true,
  });
  return list;
}

export function CellsContainer() {
  return <ConfigTypeContainer type="cells" initialSort="BTS|NUMBER" />;
}
