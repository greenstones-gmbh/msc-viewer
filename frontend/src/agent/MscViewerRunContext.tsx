import { ConfigType } from "../msc/types/ConfigType";

export interface MscViewerRunContext {
  navigate: (to: string) => void;
  types: ConfigType[];
}
