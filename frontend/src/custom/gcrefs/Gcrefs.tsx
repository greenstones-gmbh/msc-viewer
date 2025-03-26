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
import { createGcaLink } from "../gcas/Gcas";

export function createGcrefLink(mscId: string, ref: string, stype: string) {
  return `/${mscId}/gcrefs/GCREF=${ref},STYPE=${stype}`;
}

export function useGcrefColumns() {
  const mscId = useMscInstance();
  return useMscObjColumnBuilder((builder) => {
    builder
      .value("GCREF", {
        width: "12em",
        linkTo: (e) =>
          createGcrefLink(
            mscId,
            getMscPropValue(e, "GCREF"),
            getMscPropValue(e, "STYPE")
          ),
      })

      .value("GCA CODE", {
        width: "5em",
      })

      .value("GCA NAME", {
        linkTo: (e) => createGcaLink(mscId, getMscPropValue(e, "GCA CODE")),
      })

      .value("GROUP ID", {
        width: "5em",
      })

      .value("GROUP NAME", {
        // width: "5em",
      })

      .value("STYPE", {
        // width: "5em",
      });
  });
}

export function Gcrefs() {
  const listData = useOutletContext<MscObjListResult>();
  const cols = useGcrefColumns();

  return (
    <MscListPage
      title="Group Call Refs"
      cols={cols}
      csvFileName="GCREF"
      listData={listData}
    />
  );
}

export function GcrefsContainer() {
  const mscId = useMscInstance();
  const url = mscUrl(mscId, "gcrefs");

  const partnerList = useMscObjList(url, {
    initialSort: { id: createMscSortKey("GCREF"), direction: "asc" },
    paging: true,
  });

  return <Outlet context={partnerList} />;
}
