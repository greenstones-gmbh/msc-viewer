import { fetchJson, useFetch } from "@clickapp/qui-core";
import { createContext, PropsWithChildren, useContext } from "react";
import { useParams } from "react-router-dom";

export interface IGraphContext {
  graphAvailable: boolean;
  graphDatabaseAvailable: boolean;
  reload?: () => Promise<void>;
  date?: string;
  isPending: boolean;
}

export const GraphContext = createContext<IGraphContext>({
  graphAvailable: false,
  graphDatabaseAvailable: false,
  isPending: false,
});

export function useGraphContext(): IGraphContext {
  return useContext(GraphContext);
}

export function GraphContextProvider({ children }: PropsWithChildren<{}>) {
  const { mscId } = useParams();
  const { data, isSuccess, reload, isPending } = useFetch<IGraphContext>(
    `/msc-viewer/api/graph/msc/${mscId}`
  );

  const reloadGraph = async () => {
    try {
      await fetchJson(`/msc-viewer/api/graph/msc/${mscId}/update`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
      });
    } catch (error) {
      console.error(error);
    }
    reload();
  };

  const c = isSuccess
    ? { ...data!, reload: reloadGraph, isPending }
    : {
        isPending,
        graphAvailable: false,
        graphDatabaseAvailable: false,
      };

  return <GraphContext.Provider value={c}>{children}</GraphContext.Provider>;
}
