import { EmbeddedTable } from "../EmbeddedTable";
import { useMscInstance, createMscSortKey } from "../MsgObj";
import { ColumnProp } from "./ConfigType";
import { useColumns } from "./useColumns";

export function RelationTable({
  id,
  type,
  columns,
  initialSort,
  relation,
}: {
  id: string;
  type: string;
  columns: ColumnProp[];
  initialSort?: string;
  relation: string;
}) {
  const cols = useColumns(columns);
  const mscId = useMscInstance();
  return (
    <EmbeddedTable
      url={`/api/graph/msc/${mscId}/${type}/${id}/rels/${relation}`}
      columns={cols}
      initialSortKey={initialSort ? createMscSortKey(initialSort) : undefined}
    />
  );
}
