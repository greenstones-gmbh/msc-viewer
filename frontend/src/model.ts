export interface IAppModel {
  modules: ConfigType[];
}

interface ConfigType {
  key: string;
  name: string;
  idFields: string[];

  relations?: {
    target: string;
    idMapping: Record<string, string | { block: string; name: string }>;
  }[];

  list: {
    cmd: string;
    parser: string;
    idMapping: Record<string, string | { block: string; name: string }>;
  };
  detail: {
    slug: string;
    cmd: string;
    parser: string;
    idMapping: Record<string, string>;
  };
}

const model: IAppModel = {
  modules: [
    {
      key: "lacs",
      name: "Location Areas",

      idFields: ["LAC", "MCC", "MNC"],

      list: {
        cmd: "ZELO",
        parser: "lac_list",
        idMapping: {
          LAC: { block: "LA", name: "LAC" },
          MCC: "MCC",
          MNC: "MNC",
        },
      },

      detail: {
        slug: "LAC=${LAC},MCC=${MCC},MNC=${MNC}",
        cmd: "ZELO:LAC=${LAC},MCC=${MCC},MNC=${MNC}",
        idMapping: {
          LAC: "LAC",
          MCC: "MCC",
          MNC: "MNC",
        },
        parser: "lac_detail",
      },
    },

    {
      key: "cells",
      name: "Cells and BTSs",

      idFields: ["BTSNO"],

      relations: [
        {
          target: "lacs",
          idMapping: {
            LAC: { block: "LA", name: "LAC" },
            MCC: "MCC",
            MNC: "MNC",
          },
        },
      ],

      list: {
        cmd: "ZEPO::IDE",
        parser: "cell_list",
        idMapping: {
          BTSNO: { block: "BTS", name: "NUMBER" },
        },
      },

      detail: {
        slug: "${BTSNO}",
        cmd: "ZEPO:NO=${NO}",
        idMapping: {
          NO: "BTSNO",
        },
        parser: "cell_detail",
      },
    },

    {
      key: "ltes",
      name: "LTE Configs",

      idFields: ["ECI", "EMCC", "EMNC"],

      relations: [
        {
          target: "lacs",
          idMapping: {
            ECI: "ECI",
            EMCC: "EMCC",
            EMNC: "EMNC",
          },
        },

        {
          target: "cells",
          idMapping: {
            BTS: "BTS NUMBER",
          },
        },
      ],

      list: {
        cmd: "ZEPO::IDE",
        parser: "cell_list",
        idMapping: {
          BTSNO: { block: "BTS", name: "NUMBER" },
        },
      },

      detail: {
        slug: "ECI=1111,EMCC=998,EMNC=06",
        cmd: "ZEPO:NO=${NO}",
        idMapping: {
          NO: "BTSNO",
        },
        parser: "cell_detail",
      },
    },
  ],
};

const a = `

BTS("LA|LAC","MCC","MNC") -> LAC("LAC", "MCC", "MNC")
LTE("BTS NUMBER") -> BTS("BTS")
CELL_LIST (MCC,MNC,LAC,CI) -> BTS(MCC,MNC,LAC,CI)
GCA -> CELL_LIST
GCREF -> GCA

LAC: { block: "LA", name: "LAC" },
MCC: "MCC",
MNC: "MNC",

`;

const types = {
  lac: {
    many: {
      cmd: "BTS",
      params: [],
    },
    one: {
      cmd: "ZELO:LAC=${LAC},MCC=${MCC},MNC=${MNC}",
      params: ["LAC", "MCC", "MNC"],
    },
  },

  bts: {
    key: "bts",
    id: {
      format: "NO=${NO}",
      params: ["NO"],
    },

    many: {
      cmd: "BTS",
      params: [],

      links: {
        lac: {
          target: "lacs",
          params: {
            LAC: "",
            MNC: "",
            MCC: "",
          },
        },
        self: {
          target: "bts",
          params: {
            NO: "BTSNO",
          },
        },
      },
    },
    one: {
      cmd: "ZEPO:NO=${NO}",
      params: ["NO"],
    },
  },
};
