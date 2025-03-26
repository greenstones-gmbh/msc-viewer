import { PickerField, PickerModal } from "@clickapp/qui-bootstrap";
import { getMscPropValue } from "../../msc/MsgObj";
import { LteConfigModalFields } from "../ltes/LteConfigModal";
import { useLocationAreaColumns, useMscCellList } from "./LocationAreas";

function LocationAreaPicker({
  handleClose,
  onSelect,
}: {
  onSelect: (v: any) => void;
  handleClose: () => void;
}) {
  const listdata = useMscCellList();

  const plainLocationAreaColumns = useLocationAreaColumns(true);

  return (
    <PickerModal
      size="lg"
      title="Select Location Area"
      listData={listdata}
      handleClose={handleClose}
      columns={plainLocationAreaColumns}
      onSelect={onSelect}
    />
  );
}

export function LocationAreaPickerField({
  name,
}: {
  name: keyof LteConfigModalFields;
}) {
  return (
    <PickerField
      name={name}
      label="Cell / BTS"
      format={(v) => getMscPropValue(v, "LA", "NAME")}
      picker={({ close, onSelect }) => (
        <LocationAreaPicker handleClose={close} onSelect={onSelect} />
      )}
    />
  );
}
