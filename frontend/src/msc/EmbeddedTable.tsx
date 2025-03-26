import { QuickTable } from "@clickapp/qui-bootstrap";
import { Column } from "@clickapp/qui-core";
import { MscObj, urlWithBasePath, useMscObjList } from "./MsgObj";

export function EmbeddedTable({
  url,
  initialSortKey,
  columns,
}: {
  url: string;
  initialSortKey?: string;
  columns: Column<MscObj>[];
}) {
  const list = useMscObjList(urlWithBasePath(url), {
    initialSort: initialSortKey
      ? { id: initialSortKey, direction: "asc" }
      : undefined,
    paging: false,
  });

  const { items, sorting, isPending } = list;

  return (
    <div className="mt-0">
      {items && items.length === 0 && !isPending && <div>No rows found</div>}
      {items && items.length > 0 && (
        <QuickTable<MscObj> items={items} sorting={sorting} columns={columns} />
      )}
    </div>
  );
}
