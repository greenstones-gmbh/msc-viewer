import { useOutletContext } from "react-router-dom";
import { MscListPage } from "../MscListPage";
import { MscObjListResult } from "../MsgObj";
import { ColumnProp } from "./ConfigType";
import { useColumns } from "./useColumns";

export function ConfigTypeListPage({
  type,
  columns,
  title,
}: {
  type: string;
  title: string;
  columns: ColumnProp[];
}) {
  const listData = useOutletContext<MscObjListResult>();
  const cols = useColumns(columns, [type]);
  return (
    <MscListPage
      title={title}
      cols={cols}
      csvFileName={type}
      listData={listData}
    />
  );
}
