import { useFetch } from "@clickapp/qui-core";
import GeoJSON from "ol/format/GeoJSON";
import { PropsWithChildren, useMemo } from "react";

import { VectorLayer, VectorLayerProps } from "./VectorLayer";

export function GeoJSONLayer({
  url,
  ...props
}: PropsWithChildren<VectorLayerProps & { url: string }>) {
  const { data } = useFetch(url);
  const features = useMemo(() => {
    if (!data) return null;
    return new GeoJSON().readFeatures(data, {});
  }, [data]);

  return (
    <>
      <VectorLayer features={features} {...props} />
    </>
  );
}
