import { Outlet, useOutletContext } from "react-router-dom";

import {
  createMscSortKey,
  getMscPropValue,
  MscObjListResult,
  mscUrl,
  useMscInstance,
  useMscObjColumnBuilder,
  useMscObjList,
} from "../../msc/MsgObj";

import { MscListPage } from "../../msc/MscListPage";

export function createLacLink(
  mscId: string,
  lac: string,
  mcc: string,
  mnc: string
) {
  return `/${mscId}/lacs/LAC=${lac.padStart(5, "0")},MCC=${mcc},MNC=${mnc}`;
}

export function useLocationAreaColumns(plain = false) {
  const mscId = useMscInstance();
  return useMscObjColumnBuilder((builder) => {
    builder.multiValue("LA", "LAC", { width: "5em", header: "LAC" });

    builder.multiValue("LA", "NAME", {
      header: "NAME",
      linkTo: plain
        ? undefined
        : (e) =>
            createLacLink(
              mscId,
              getMscPropValue(e, "LA", "LAC"),
              getMscPropValue(e, "MCC"),
              getMscPropValue(e, "MNC")
            ),
    });

    builder.value("MCC", {
      width: "5em",
    });

    builder.value("MNC", {
      width: "5em",
    });

    builder.value("AT", {
      width: "5em",
    });
  });
}

export function useMscCellList() {
  const mscId = useMscInstance();
  const list = useMscObjList(mscUrl(mscId, "lacs"), {
    initialSort: { id: createMscSortKey("LA", "LAC"), direction: "asc" },
    paging: true,
  });
  return list;
}

export function LocationAreas() {
  const listData = useOutletContext<MscObjListResult>();
  const locationAreaColumns = useLocationAreaColumns();
  return (
    <MscListPage
      title="Location Areas"
      cols={locationAreaColumns}
      csvFileName="LocationAreas"
      listData={listData}
    />
  );
}

export function LocationAreasContainer() {
  const list = useMscCellList();
  return <Outlet context={list} />;
}
