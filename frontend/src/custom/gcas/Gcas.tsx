import { Outlet, useOutletContext } from "react-router-dom";

import { MscListPage } from "../../msc/MscListPage";
import {
  createMscSortKey,
  getMscPropValue,
  MscObjListResult,
  mscUrl,
  useMscInstance,
  useMscObjColumnBuilder,
  useMscObjList,
} from "../../msc/MsgObj";

export function createGcaLink(mscId: string, gcac: string) {
  return `/${mscId}/gcas/${gcac}`;
}

export function useGcaColumns(plain = false) {
  const mscId = useMscInstance();
  return useMscObjColumnBuilder((builder) => {
    builder
      .value("GCAC", {
        linkTo: (e) => createGcaLink(mscId, getMscPropValue(e, "GCAC")),
        width: "8em",
      })
      .value("GROUP CALL AREA NAME");
  });
}

export function Gcas() {
  const listData = useOutletContext<MscObjListResult>();
  const cols = useGcaColumns();
  return (
    <MscListPage
      title="Group Call Areas"
      cols={cols}
      csvFileName="GCA"
      listData={listData}
    />
  );
}

export function GcasContainer() {
  const mscId = useMscInstance();
  const url = mscUrl(mscId, "gcas");

  const list = useMscObjList(url, {
    initialSort: { id: createMscSortKey("GCAC"), direction: "asc" },
    paging: true,
  });

  return <Outlet context={list} />;
}
