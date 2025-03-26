import { DependencyList } from "react";
import { useMscInstance, useMscObjColumnBuilder } from "../MsgObj";
import { ColumnProp, formatLink } from "./ConfigType";

export function useColumns(columns: ColumnProp[], deps?: DependencyList) {
  const mscId = useMscInstance();
  return useMscObjColumnBuilder((builder) => {
    columns.forEach((col) => {
      if (typeof col === "string") {
        builder.value(col, { width: "5em" });
      } else {
        builder.value(col.prop, {
          header: col.header,
          width: col.width,
          linkTo: col.linkTo
            ? (entity) => formatLink(mscId, col.linkTo!, entity)
            : undefined,
        });
      }
    });
  }, deps);
}
