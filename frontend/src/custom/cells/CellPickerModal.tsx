import { PickerField, PickerModal } from "@clickapp/qui-bootstrap";
import { RegisterOptions } from "react-hook-form";
import { getMscPropValue } from "../../msc/MsgObj";
import { LteConfigModalFields } from "../ltes/LteConfigModal";
import { useBtsColumns, useCellList } from "./Cells";

function CellPickerModal({
  handleClose,
  onSelect,
  mscId,
}: {
  onSelect: (v: any) => void;
  handleClose: () => void;
  mscId: string;
}) {
  const listdata = useCellList(mscId);
  const btsColumns = useBtsColumns(mscId, true);

  return (
    <PickerModal
      size="xl"
      title="Select Cell / BTS"
      listData={listdata}
      handleClose={handleClose}
      columns={btsColumns}
      onSelect={onSelect}
    />
  );
}

export function CellPickerField({
  name,
  ops,
  mscId,
}: {
  name: keyof LteConfigModalFields;
  ops?: RegisterOptions;
  mscId: string;
}) {
  return (
    <PickerField
      ops={ops}
      name={name}
      label="CELL / BTS"
      format={(v) =>
        getMscPropValue(v, "BTS", "NAME") +
        " / " +
        getMscPropValue(v, "BTS", "NUMBER")
      }
      picker={({ close, onSelect }) => (
        <CellPickerModal
          handleClose={close}
          onSelect={onSelect}
          mscId={mscId}
        />
      )}
    />
  );
}
