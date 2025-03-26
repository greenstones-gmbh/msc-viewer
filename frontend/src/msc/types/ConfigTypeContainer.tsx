import { Outlet } from "react-router-dom";
import { useMscInstance, useMscObjList, mscUrl } from "../MsgObj";
import { createSort } from "./ConfigType";

export function ConfigTypeContainer({
  type,
  initialSort,
}: {
  type: string;
  initialSort?: string;
}) {
  const mscId = useMscInstance();

  const list = useMscObjList(mscUrl(mscId, type), {
    paging: true,
    initialSort: createSort(initialSort),
  });
  return <Outlet context={list} />;
}
