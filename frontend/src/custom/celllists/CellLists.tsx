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

export function createCellListLink(mscId: string, name: string) {
  return `/${mscId}/cell-lists/${name}`;
}

export function useCellListColumns(useCLID = true) {
  const col = useCLID ? "CELL LIST ID" : "CLID";
  const mscId = useMscInstance();
  return useMscObjColumnBuilder((builder) => {
    builder
      .value(col, {
        linkTo: (v) =>
          `/${mscId}/cell-lists/${getMscPropValue(v, "CELL LIST NAME")}`,
        width: "12em",
      })
      .value("CELL LIST NAME");
  });
}

export function CellLists() {
  const listData = useOutletContext<MscObjListResult>();
  const cols = useCellListColumns();

  return (
    <MscListPage
      title="Cell Lists"
      cols={cols}
      csvFileName="CellList"
      listData={listData}
    />
  );
}

export function CellListsContainer() {
  const mscId = useMscInstance();
  const url = mscUrl(mscId, "cell-lists");

  const list = useMscObjList(url, {
    initialSort: { id: createMscSortKey("GCREF"), direction: "asc" },
    paging: true,
  });

  return <Outlet context={list} />;
}
