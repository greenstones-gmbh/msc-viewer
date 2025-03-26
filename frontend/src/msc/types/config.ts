import { ConfigType } from "./ConfigType";

export const Types: ConfigType[] = [
  {
    type: "lacs",
    node: {
      typeLabel: "LAC",
      valueTitle: "${LAC}",
      color: "orange",
    },
    list: {
      title: "Location Areas",
      columns: [
        "LA|LAC",
        {
          prop: "LA|NAME",
          linkTo: {
            type: "lacs",
            id: {
              template: "LAC=${LAC},MCC=${MCC},MNC=${MNC}",
              mapping: "LAC=LA|LAC,MCC,MNC",
              paddings: { LAC: 5 },
            },
          },
        },
        "MCC",
        "MNC",
        "AT",
      ],
      initialSort: "LA|LAC",
    },
    detail: {
      title: {
        template: "${NAME}",
        mapping: "NAME=LA|NAME",
      },
      graphQueries: ["match p=(:LAC:${MSC} {id:'${ID}'})--(:${MSC}) return p"],
      relatedTables: [
        {
          relation: "cells",
          title: "Cells and BTSs",
          columns: [
            "BTS|NUMBER",
            {
              prop: "BTS|NAME",
              linkTo: {
                type: "cells",
                id: {
                  template: "${BTS}",
                  mapping: "BTS=BTS|NUMBER",
                },
              },
            },
            "BSC|NUMBER",
            "BSC|NAME",
            "MCC",
            "MNC",
            "CI",
            {
              prop: "BTS ADMINISTRATIVE STATE",
              header: "State",
              width: "5em",
            },
          ],
        },
      ],
      props: [
        ["LA|LAC", "LA|NAME"],
        ["MCC", "MNC"],
      ],
    },
  },

  {
    type: "cells",
    node: {
      typeLabel: "BTS",
      relations: [{ targetType: "lacs", name: "USES" }],
      color: "blue",
    },
    list: {
      columns: [
        "BTS|NUMBER",
        {
          prop: "BTS|NAME",
          linkTo: {
            type: "cells",
            id: {
              template: "${BTS}",
              mapping: "BTS=BTS|NUMBER",
            },
          },
        },
        "BSC|NUMBER",
        "BSC|NAME",
        "LA|LAC",
        {
          prop: "LA|NAME",
          linkTo: {
            type: "lacs",
            id: {
              template: "LAC=${LAC},MCC=${MCC},MNC=${MNC}",
              mapping: "LAC=LA|LAC,MCC,MNC",
              paddings: { LAC: 5 },
            },
          },
        },
        "MCC",
        "MNC",
        "CI",
        {
          prop: "BTS ADMINISTRATIVE STATE",
          header: "State",
          width: "5em",
        },
      ],
      title: "Cells and BTSs",
      initialSort: "BTS|NUMBER",
    },
    detail: {
      title: {
        template: "${NAME}",
        mapping: "NAME=BTS|NAME",
      },

      graphQueries: [
        "match p=(gca:BTS:${MSC} {id:'${ID}'})--() return p",
        "match p=(gca:BTS:${MSC} {id:'${ID}'})--(:CELL_LIST:${MSC})--(:GCA:${MSC}) return p",
      ],
      props: [
        [
          "BTS|NAME",
          "BTS|NUMBER",
          "",
          "LA|LAC",
          {
            prop: "LA|NAME",
            linkTo: {
              type: "lacs",
              id: {
                template: "LAC=${LAC},MCC=${MCC},MNC=${MNC}",
                mapping: "LAC=LA|LAC,MCC,MNC",
                paddings: { LAC: 5 },
              },
            },
          },
        ],
        ["MCC", "MNC", "CI"],
        ["BSC|NAME", "BSC|NUMBER"],
      ],

      relatedTables: [
        {
          relation: "cell-lists",
          title: "Cell Lists",
          columns: [
            {
              prop: "CLID",
              width: "5em",
              linkTo: {
                type: "cell-lists",
                id: {
                  template: "${CLID}",
                  mapping: "CLID=CELL LIST NAME",
                },
              },
            },
            { prop: "CELL LIST NAME", width: undefined },
          ],
        },

        {
          relation: "cell-lists,gcas",
          title: "GCAs",
          columns: [
            {
              prop: "GCAC",
              width: "5em",
              linkTo: {
                type: "gcas",
                id: {
                  template: "${GCAC}",
                  mapping: "GCAC",
                },
              },
            },

            { prop: "GROUP CALL AREA NAME", width: undefined },
          ],
        },

        {
          relation: "ltes",
          title: "LTE Configs",
          columns: [
            {
              prop: "ECI",
              width: "5em",
              linkTo: {
                type: "ltes",
                id: {
                  template: "ECI=${ECI},EMCC=${EMCC},EMNC=${EMNC}",
                  mapping: "ECI,EMCC,EMNC",
                },
              },
            },
            "EMCC",
            "EMNC",
            { prop: "BTS NUMBER", header: "BTS", width: "5em" },
            {
              prop: "NAME",
              width: undefined,
              header: "BTS Name",
              linkTo: {
                type: "cells",
                id: {
                  template: "${NAME}",
                  mapping: "NAME=BTS NUMBER",
                },
              },
            },
            "MCC",
            "MNC",
            "CI",
          ],
        },
      ],
    },
  },

  {
    type: "cell-lists",
    node: {
      typeLabel: "CELL_LIST",
      typeTitle: "CELL LIST",
      valueTitle: "${CLID}",
      relations: [{ targetType: "cells" }],
      color: "#aaa",
    },
    list: {
      title: "Cell Lists",
      columns: [
        {
          prop: "CELL LIST ID",
          width: "5em",
          linkTo: {
            type: "cell-lists",
            id: {
              template: "${NAME}",
              mapping: "NAME=CELL LIST NAME",
            },
          },
        },
        { prop: "CELL LIST NAME", width: undefined },
      ],
      initialSort: "CELL LIST ID",
    },
    detail: {
      title: {
        template: "${NAME}",
        mapping: "NAME=CELL LIST NAME",
      },
      graphQueries: [
        "match p=(:CELL_LIST:${MSC} {id:'${ID}'})--(:${MSC}) return p",
      ],
      relatedTables: [
        {
          relation: "gcas",
          title: "GCAs",
          columns: [
            {
              prop: "GCAC",
              width: "5em",
              linkTo: {
                type: "gcas",
                id: {
                  template: "${GCAC}",
                  mapping: "GCAC",
                },
              },
            },

            { prop: "GROUP CALL AREA NAME", width: undefined },
          ],
        },

        {
          relation: "cells",
          title: "Cells and BTSs",
          columns: [
            "BTS|NUMBER",
            {
              prop: "BTS|NAME",
              linkTo: {
                type: "cells",
                id: {
                  template: "${BTS}",
                  mapping: "BTS=BTS|NUMBER",
                },
              },
            },
            "BSC|NUMBER",
            "BSC|NAME",
            "LA|LAC",
            {
              prop: "LA|NAME",
              linkTo: {
                type: "lacs",
                id: {
                  template: "LAC=${LAC},MCC=${MCC},MNC=${MNC}",
                  mapping: "LAC=LA|LAC,MCC,MNC",
                  paddings: { LAC: 5 },
                },
              },
            },
            "MCC",
            "MNC",
            "CI",
            {
              prop: "BTS ADMINISTRATIVE STATE",
              header: "State",
              width: "5em",
            },
          ],
        },
      ],
      props: [["CLNAME"], ["CLID"]],
    },
  },

  {
    type: "ltes",
    node: {
      typeLabel: "LTEConfig",
      typeTitle: "LTE Conf",
      valueTitle: "${ECI}",
      relations: [{ targetType: "cells" }],
      color: "green",
    },
    list: {
      title: "LTE Configs",
      columns: [
        {
          prop: "ECI",
          width: "5em",
          linkTo: {
            type: "ltes",
            id: {
              template: "ECI=${ECI},EMCC=${EMCC},EMNC=${EMNC}",
              mapping: "ECI,EMCC,EMNC",
            },
          },
        },
        "EMCC",
        "EMNC",
        { prop: "BTS NUMBER", header: "BTS", width: "5em" },
        {
          prop: "NAME",
          width: undefined,
          header: "BTS Name",
          linkTo: {
            type: "cells",
            id: {
              template: "${NAME}",
              mapping: "NAME=BTS NUMBER",
            },
          },
        },
        "MCC",
        "MNC",
        "CI",
      ],
      initialSort: "ECI",
    },
    detail: {
      title: {
        template: "ECI ${ECI}",
        mapping: "ECI",
      },
      graphQueries: [
        "match p=(:LTEConfig:${MSC} {id:'${ID}'})--(:${MSC}) return p",
      ],
      props: [
        [
          {
            prop: "BTS NUMBER",
            linkTo: {
              type: "cells",
              id: {
                template: "${NAME}",
                mapping: "NAME=BTS NUMBER",
              },
            },
          },
          "BTS NAME",
        ],
        [
          "CI",
          "MCC",
          "MNC",
          "",
          {
            prop: "LAC",
            linkTo: {
              type: "lacs",
              id: {
                template: "LAC=${LAC},MCC=${MCC},MNC=${MNC}",
                mapping: "LAC,MCC,MNC",
                paddings: { LAC: 5 },
              },
            },
          },
        ],
        ["ECI", "EMCC", "EMNC"],
      ],
    },
  },

  {
    type: "gcas",
    node: {
      typeLabel: "GCA",
      relations: [{ targetType: "cell-lists", name: "CONTAINS" }],
      color: "#ffc107",
    },
    list: {
      title: "Group Call Areas",
      columns: [
        {
          prop: "GCAC",
          width: "5em",
          linkTo: {
            type: "gcas",
            id: {
              template: "${GCAC}",
              mapping: "GCAC",
            },
          },
        },

        { prop: "GROUP CALL AREA NAME", width: undefined },
      ],
      initialSort: "GCAC",
    },
    detail: {
      title: {
        template: "${GCAN}",
        mapping: "GCAN",
      },
      graphQueries: ["match p=(:GCA:${MSC} {id:'${ID}'})--(:${MSC}) return p"],
      props: [["GCAC"], ["GCAN"]],
      relatedTables: [
        {
          relation: "gcrefs",
          title: "Group Call Refs",
          initialSort: "GCREF",
          columns: [
            {
              prop: "GCREF",
              width: "12em",
              linkTo: {
                type: "gcrefs",
                id: {
                  template: "GCREF=${GCREF},STYPE=${STYPE}",
                  mapping: "GCREF,STYPE",
                },
              },
            },
            "GROUP ID",
            { prop: "GROUP NAME" },
            "STYPE",
          ],
        },
        {
          relation: "cell-lists,cells",
          title: "Cells and BTSs",
          initialSort: "BTS|NUMBER",
          columns: [
            "BTS|NUMBER",
            {
              prop: "BTS|NAME",
              linkTo: {
                type: "cells",
                id: {
                  template: "${BTS}",
                  mapping: "BTS=BTS|NUMBER",
                },
              },
            },
            "BSC|NUMBER",
            "BSC|NAME",
            "LA|LAC",
            {
              prop: "LA|NAME",
              linkTo: {
                type: "lacs",
                id: {
                  template: "LAC=${LAC},MCC=${MCC},MNC=${MNC}",
                  mapping: "LAC=LA|LAC,MCC,MNC",
                  paddings: { LAC: 5 },
                },
              },
            },
            "MCC",
            "MNC",
            "CI",
            {
              prop: "BTS ADMINISTRATIVE STATE",
              header: "State",
              width: "5em",
            },
          ],
        },
      ],
    },
  },

  {
    type: "gcrefs",
    node: {
      typeLabel: "GCREF",
      valueTitle: "${GROUP}-${STYPE}",
      relations: [{ targetType: "gcas" }],
      color: "#cff4fc",
    },
    list: {
      title: "Group Call Refs",
      columns: [
        {
          prop: "GCREF",
          width: "12em",
          linkTo: {
            type: "gcrefs",
            id: {
              template: "GCREF=${GCREF},STYPE=${STYPE}",
              mapping: "GCREF,STYPE",
            },
          },
        },
        "GCA CODE",
        {
          prop: "GCA NAME",
          linkTo: {
            type: "gcas",
            id: {
              template: "${GCA CODE}",
              mapping: "GCA CODE",
            },
          },
        },
        "GROUP ID",
        { prop: "GROUP NAME" },
        "STYPE",
      ],
      initialSort: "GCREF",
    },
    detail: {
      title: {
        template: "GCREF ${GCREF}",
        mapping: "GCREF",
      },
      graphQueries: [
        "match p=(:GCREF:${MSC} {id:'${ID}'})--(:${MSC}) return p",
      ],
      props: [
        ["GCREF", "SERVICE TYPE"],
        [
          "GCAN",
          {
            prop: "GCAC",
            linkTo: {
              type: "gcas",
              id: {
                template: "${GCAC}",
                mapping: "GCAC",
              },
            },
          },
        ],
        ["GROUP NAME", "GROUP ID"],
      ],
    },
  },
];
