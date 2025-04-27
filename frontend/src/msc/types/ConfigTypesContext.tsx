import { useFetch } from "@clickapp/qui-core";
import { createContext, PropsWithChildren, useContext, useMemo } from "react";
import { useParams } from "react-router-dom";
import { ConfigType } from "./ConfigType";

export interface ConfigTypeValues {
  types: ConfigType[] | undefined;
  mscId: string;
  reload?: (params?: any) => void;
  isPending: boolean;
}

export const ConfigTypesContext = createContext<ConfigTypeValues | undefined>(
  undefined
);

export function useConfigTypes(): ConfigType[] {
  const ctx = useContext(ConfigTypesContext);
  if (!ctx) new Error("ConfigTypesContext not found");
  return ctx!.types || [];
}

export function useMapLayers() {
  const types = useConfigTypes();
  const layers = useMemo(() => {
    return types
      .filter((t) => !!t.map)
      .flatMap((t) => t.map?.layers!)
      .sort((a, b) => a.prio - b.prio);
  }, [types]);
  return layers;
}

export function ConfigTypesContextProvider({
  children,
}: PropsWithChildren<{}>) {
  const { mscId } = useParams();
  const { data, reload, isPending } = useFetch<ConfigType[]>(
    `/msc-viewer/api/msc/${mscId}`
  );

  const c: ConfigTypeValues = {
    types: data,
    reload,
    isPending,
    mscId: mscId!,
  };

  return (
    <ConfigTypesContext.Provider value={c}>
      {children}
    </ConfigTypesContext.Provider>
  );
}
