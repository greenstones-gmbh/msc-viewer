# GSMR Data Model

## lacs — Location Areas

- Node Label: `LAC`
- Properties: `NAME`, `LAC`, `MCC`, `MNC`

## cells — Cells and BTSs

- Node Label: `BTS`
- Properties: `NAME`, `NUMBER`, `LAC`, `LA_NAME`, `BSC`, `CI`, `MCC`, `MNC`
- Relations: [BELONGS_TO]->(lacs)

## cell-lists — Cell Lists

- Node Label: `CELL_LIST`
- Properties: `CLNAME`, `CLID`
- Relations: [CONTAINS]->(cells)

## ltes — LTE Configs

- Node Label: `LTEConfig`
- Properties: `ECI`, `EMCC`, `EMNC`, `BTS`, `NAME`, `MCC`, `MNC`, `CI`
- Relations: [CONFIG_FOR]->(cells)

## gcas — Group Call Areas

- Node Label: `GCA`
- Properties: `GCAC`, `GCAN`
- Relations: [USES]->(cell-lists)

## gcrefs — Group Call Refs

- Node Label: `GCREF`
- Properties: `GCREF`, `GCAC`, `GROUP`, `STYPE`
- Relations: [LINKED_TO]->(gcas), [INITIATOR_CELL]->(cell-lists)
