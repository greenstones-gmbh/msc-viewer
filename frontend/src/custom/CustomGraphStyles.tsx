import { IGraphStyles } from "../clickapp-bootstrap/graph/Graph";
import { createCellListLink } from "./celllists/CellLists";
import { createBtsLink } from "./cells/Cells";
import { createGcaLink } from "./gcas/Gcas";
import { createGcrefLink } from "./gcrefs/Gcrefs";
import { createLacLink } from "./lacs/LocationAreas";
import { createLteConfigLink } from "./ltes/LteConfigs";

export const MscGraphStyles: IGraphStyles = {
  nodes: [
    {
      selector: "GCA",
      color: "#ffc107",
      useLabelAsTitle: false,
      label: "GCA",
      titleProp: "GCAC",
      styles: ["bold"],
      navPath: (n, ctx) => createGcaLink(ctx?.mscId, n.properties.GCAC),
    },
    {
      selector: "GCREF",
      label: "GCREF",
      color: "#cff4fc",
      useLabelAsTitle: false,
      title: (n) => n.properties.GROUP + "-" + n.properties.STYPE,
      styles: ["bold"],
      navPath: (n, ctx) =>
        createGcrefLink(ctx?.mscId, n.properties.GCREF, n.properties.STYPE),
    },

    {
      selector: "CELL_LIST",
      color: "#aaa",
      captions: (n) => [
        { value: "CELL LIST" },
        { value: n.properties.CLID, styles: ["bold"] },
      ],
      navPath: (n, ctx) => createCellListLink(ctx?.mscId, n.properties.CLNAME),
    },

    {
      selector: "BTS",
      color: "blue",
      useLabelAsTitle: false,
      label: "BTS",
      titleProp: "NUMBER",
      navPath: (n, ctx) => createBtsLink(ctx?.mscId, n.properties.NUMBER),
      styles: ["bold"],
    },

    {
      selector: "LAC",
      color: "orange",
      useLabelAsTitle: false,
      label: "LAC",
      titleProp: "LAC",
      styles: ["bold"],
      navPath: (n, ctx) =>
        createLacLink(
          ctx?.mscId,
          n.properties.LAC,
          n.properties.MCC,
          n.properties.MNC
        ),
    },

    {
      selector: "LTEConfig",
      color: "green",
      styles: ["bold"],
      captions: (n) => [
        { value: "LTE Conf" },
        { value: n.properties.ECI, styles: ["bold"] },
      ],
      navPath: (n, ctx) =>
        createLteConfigLink(
          ctx?.mscId,
          n.properties.ECI,
          n.properties.EMCC,
          n.properties.EMNC
        ),
    },

    {
      selector: "*",
      captions: (n) => [{ value: n.labels?.join(",") || "-" }, { value: n.id }],
    },
  ],
};

export const MscTypeGraphStyles: IGraphStyles = {
  nodes: [
    {
      selector: "GCA",
      color: "#ffc107",
      title: "GCA",
      navPath: (n) => `/${n.properties[0].mscId}/gcas`,
    },
    {
      selector: "GCREF",
      color: "#cff4fc",
      title: "GCREF",
      navPath: (n) => `/${n.properties[0].mscId}/gcrefs`,
    },

    {
      selector: "CELL_LIST",
      color: "#aaa",
      title: "CELL LIST",
      navPath: (n) => `/${n.properties[0].mscId}/cell-lists`,
    },

    {
      selector: "BTS",
      color: "blue",
      title: "BTS",
      navPath: (n) => `/${n.properties[0].mscId}/cells`,
    },

    {
      selector: "LAC",
      color: "orange",
      title: "LAC",
      navPath: (n) => `/${n.properties[0].mscId}/lacs`,
    },

    {
      selector: "LTEConfig",
      color: "green",
      title: "LTE Config",
      navPath: (n) => `/${n.properties[0].mscId}/ltes`,
    },
  ],
};
