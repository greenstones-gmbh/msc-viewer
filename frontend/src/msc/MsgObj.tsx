import { useParams } from "react-router-dom";
// import {
//   Column,
//   ColumnOptions,
//   createColumn,
//   Field,
//   FieldRenderOptions,
//   Fields,
// } from "../clickapp/Fields";
// import {
//   ArraySourceOptions,
//   Filters,
//   useArray,
// } from "../clickapp/hooks/useArray";
// import { useAsyncMemo } from "../clickapp/hooks/useAsyncMemo";
// import { fetchJson } from "../clickapp/hooks/useFetch";
// import { ListData, ListOptions, Sort } from "../clickapp/hooks/useList";
import {
  ArraySourceOptions,
  Column,
  ColumnBuilder,
  ColumnOptions,
  createColumn,
  fetchJson,
  Field,
  FieldRenderOptions,
  Fields,
  Filters,
  ListData,
  ListOptions,
  Sort,
  useArray,
  useAsyncMemo,
  useColumnBuilder,
} from "@clickapp/qui-core";
import { DependencyList } from "react";
import { MscResponse } from "./MscResponse";

const BASE_PATH = `/msc-viewer/api/msc`;
const ROOT_BASE_PATH = `/msc-viewer`;

export interface MscObj {
  sections: MscSection[];
}

export interface MscSection {
  name?: string;
  props: MscProp[];

  columns?: string[];
  objects?: MscObj[];
}

export interface MscTable {}

export interface MscProp {}
export interface MscValueProp extends MscProp {
  name: string;
  shortName?: string;
  value: any;
}
export interface MscMultiValueProp extends MscProp {
  name: string;
  props: MscValueProp[];
}
export function isValueProp(object: any): object is MscValueProp {
  return "value" in object;
}
export function isMultiValueProp(object: any): object is MscMultiValueProp {
  return "props" in object;
}
export function hasName(prop: MscProp, name: string) {
  if (isValueProp(prop)) {
    return prop.name === name || prop.shortName === name;
  }

  if (isMultiValueProp(prop)) {
    return prop.name === name;
  }

  return false;
}
export function getMscPropValue(v: MscObj, name: string, hint?: string) {
  const prop = v.sections
    .filter((s) => !!s.props)
    .flatMap((s) => s.props)
    .find((p) => hasName(p, name));

  if (prop) {
    if (isValueProp(prop)) return prop.value;
    if (isMultiValueProp(prop)) {
      return hint ? prop.props.find((p) => p.name === hint)?.value : prop.props;
    }
  }
  return undefined;
}
export function mscProp(name: string, hint?: string) {
  return (v: MscObj) => {
    return getMscPropValue(v, name, hint);
  };
}

export function sortMscProps() {
  return (sort: Sort) => {
    return (a: MscObj, b: MscObj) => {
      const parts = sort.id.split("@@@");
      const va = getMscPropValue(a, parts[0], parts[1]);
      const vb = getMscPropValue(b, parts[0], parts[1]);

      return (
        (`${va}` || "").localeCompare(`${vb}` || "") *
        (sort.direction === "asc" ? 1 : -1)
      );
    };
  };
}

export function createMscSortKey(name: string, hint?: string) {
  if (!hint) {
    return name.split("|").join("@@@");
  }
  return !hint ? name : `${name}@@@${hint}`;
}

export interface MscObjListResult extends ListData<MscObj, string> {
  error?: Error;
  isError: boolean;
  isSuccess: boolean;
  isPending: boolean;
  rawData: MscObj[] | undefined;
  reload: (params?: any) => void;
  cmd: { info?: string; version?: string; command?: string };
}

export function mscUrl(mscId: string, type: string) {
  return `${BASE_PATH}/${mscId}/${type}`;
}

export function urlWithBasePath(url: string) {
  return `${ROOT_BASE_PATH}${url}`;
}

export function useMscObjList(
  url: string,
  ops?: ArraySourceOptions<MscObj, string> & ListOptions<string>
): MscObjListResult {
  const { data, error, isError, isSuccess, isPending, reload } = useAsyncMemo<
    MscResponse<MscObj[]>
  >(
    async (params) => {
      const u = params && params.force ? `${url}?force=true` : url;
      return await fetchJson<MscResponse<MscObj[]>>(u);
    },
    [url]
  );

  const { data: items, ...cmd } = data || {};

  const partnerList = useArray<MscObj>(data?.data || [], {
    paging: false,
    sorting: true,
    // filtering: true,
    filter: Filters.objectContains(),
    sorter: sortMscProps(),
    ...ops,
  });

  return {
    ...partnerList,
    error,
    isError,
    isSuccess,
    isPending,
    rawData: items,
    cmd,
    reload: () => reload({ force: true }),
  };
}

export function useMscObj(url: string) {
  const { reload, ...props } = useAsyncMemo<MscResponse<MscObj>>(
    async (params) => {
      const u = params && params.force ? `${url}?force=true` : url;
      return await fetchJson<MscResponse<MscObj>>(u);
    },
    [url]
  );
  return { ...props, reload: () => reload({ force: true }) };
}

export function prop<PropType = string>(
  name: string,
  options: FieldRenderOptions<MscObj, PropType> = {}
): MscField {
  const parts = name.split("|");
  const o = {
    label:
      options.label || parts.length === 2 ? parts[0] + " " + parts[1] : name,
    getter: (v: MscObj) =>
      parts.length === 2
        ? getMscPropValue(v, parts[0], parts[1])
        : getMscPropValue(v, name),
    ...options,
  };

  return {
    name: parts.length === 2 ? parts[0] : name,
    hint: parts.length === 2 ? parts[1] : undefined,
    ...Fields.create(o),
  };
}

export interface MscField extends Field<MscObj> {
  name: string;
  hint?: string;
}

export function multiValueProp<PropType = string>(
  name: string,
  hint: string,
  options: FieldRenderOptions<MscObj, PropType> = {}
): MscField {
  const o = {
    label: !hint ? name : `${name} ${hint}`,
    getter: (v: MscObj) => getMscPropValue(v, name, hint),
    ...options,
  };
  return { name, hint, ...Fields.create(o) };
}

export function valueColumn<PropType = string>(
  name: string,
  options: ColumnOptions<MscObj> & FieldRenderOptions<MscObj, PropType> = {}
): Column<MscObj> {
  const field = prop(name, options);
  return column(field, options);
}

export function column(
  field: MscField,
  options: ColumnOptions<MscObj> = {}
): Column<MscObj> {
  return createColumn(field, {
    sortKey: createMscSortKey(field.name, field.hint),
    header: !field.hint ? field.name : `${field.name} ${field.hint}`,
    ...options,
  });
}

export function multiValueColumn<PropType = string>(
  name: string,
  hint: string,
  options: ColumnOptions<MscObj> & FieldRenderOptions<MscObj, PropType> = {}
): Column<MscObj> {
  const field = multiValueProp(name, hint, options);
  return createColumn(field, {
    sortKey: createMscSortKey(name, hint),
    ...options,
  });
}

export function MscPropValue({
  obj,
  name,
  hint,
}: {
  obj: MscObj;
  name: string;
  hint?: string;
}) {
  return getMscPropValue(obj, name, hint);
}

export interface MscContext {
  mscId: string;
}

export function useMscContext() {
  const { mscId } = useParams();
  const context: MscContext = { mscId: mscId || "-" };
  return context;
}

export function useMscInstance() {
  const { mscId } = useParams();
  if (!mscId) {
    throw new Error("MSC ID required");
  }
  return mscId;
}

export function useMscObjColumnBuilder(
  configurer: (builder: MscObjectColumnBuilder) => void,
  deps?: DependencyList
) {
  return useColumnBuilder<MscObj>((builder) => {
    const b = new MscObjectColumnBuilder(builder);
    configurer(b);
  }, deps);
}

export class MscObjectColumnBuilder {
  builder: ColumnBuilder<MscObj>;

  constructor(builder: ColumnBuilder<MscObj>) {
    this.builder = builder;
  }

  value(
    prop: string,
    options: ColumnOptions<MscObj> & FieldRenderOptions<MscObj, any> = {}
  ) {
    this.builder.add(valueColumn(prop, options));
    return this;
  }

  multiValue(
    name: string,
    hint: string,
    options: ColumnOptions<MscObj> & FieldRenderOptions<MscObj, any> = {}
  ) {
    this.builder.add(multiValueColumn(name, hint, options));
    return this;
  }
}
