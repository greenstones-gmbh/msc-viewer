import { Sort } from "@clickapp/qui-core";
import { createMscSortKey, getMscPropValue, MscObj } from "../MsgObj";
import { formatTemplate } from "./utils";

export interface Template {
  template: string;
  mapping: string;
  paddings?: Record<string, number>;
  valueMapping?: Record<string, string>;
}

export type Prop =
  | string
  | {
      prop: string;
      label?: string;
      linkTo?: { type: string; id: Template };
    };

export type ColumnProp =
  | string
  | {
      prop: string;
      header?: string;
      width?: string;
      linkTo?: { type: string; id: Template };
    };

export interface TableTab {
  title: string;
  columns: ColumnProp[];
  initialSort?: string;
  relation: string;
}

export interface ConfigType {
  type: string;
  list: {
    columns: ColumnProp[];
    title: string;
    initialSort?: string;
  };
  detail?: {
    title: Template;
    props?: Prop[][];
    graphQueries?: string[];
    relatedTables?: TableTab[];
  };
  node?: {
    typeLabel: string;
    typeTitle?: string;
    valueTitle?: string;
    relations?: { targetType: string; name?: string }[];
    color: string;
  };
  map?: {
    layers: {
      path: string;
      layer: string;
      title: string;
      style?: string;
      maxResolution?: number;
      prio: number;
      enabled: boolean;
    };
  };
}

export function createSort(prop?: string): Sort | undefined {
  if (!prop) return undefined;
  return { id: createMscSortKey(prop), direction: "asc" };
}

export function formatText(template: Template, obj: MscObj) {
  const params = mapObj(obj, template.mapping);

  if (template.paddings) {
    Object.keys(template.paddings).forEach((k) => {
      params[k] = (params[k] || "").padStart(5, "0");
    });
  }
  if (template.valueMapping) {
    Object.keys(params)
      .filter((k) => !!template.valueMapping![params[k]])
      .forEach((k) => {
        params[k] = template.valueMapping![params[k]];
      });
  }

  return formatTemplate(template.template, params);
}

export function formatLink(
  mscId: string,
  link: { type: string; id: Template },
  obj: MscObj
) {
  const idString = formatText(link.id, obj);
  return `/${mscId}/${link.type}/${idString}`;
}

export function mapObj(obj: MscObj, mapping: string) {
  const result: Record<string, string> = {};
  mapping
    .split(",")
    .map((s) => s.trim())
    .map((s) => s.split("="))
    .forEach((s) => {
      const key = s[0];
      if (s.length === 2) {
        const names = s[1].split("|");
        if (names.length === 2) {
          result[key] = getMscPropValue(obj, names[0], names[1]);
        } else {
          result[key] = getMscPropValue(obj, s[1]);
        }
      } else {
        result[key] = getMscPropValue(obj, key);
      }
    });
  return result;
}
