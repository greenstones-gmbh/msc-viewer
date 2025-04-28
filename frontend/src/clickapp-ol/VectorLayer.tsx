import OlVectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import { StyleLike } from "ol/style/Style";
import { FlatStyleLike } from "ol/style/flat";
import { useEffect, useRef } from "react";
import { useMap } from "./Map";
import Feature from "ol/Feature";

export interface VectorLayerProps {
  name?: string;
  title?: string;
  features?: Feature[] | null;
  style?: StyleLike | FlatStyleLike | null;
  maxResolution?: number;
  visible?: boolean;
}

export function VectorLayer({
  name,
  title,
  features,
  style,
  maxResolution,
  visible,
}: VectorLayerProps) {
  const map = useMap();
  const layerRef = useRef(
    new OlVectorLayer({
      source: new VectorSource({}),
      style,
      maxResolution,
      visible,
    })
  );

  useEffect(() => {
    if (!map) return;

    const layer = layerRef.current;
    (layer as any).name = name;
    (layer as any).title = title;
    map.addLayer(layer);

    return () => {
      if (map) {
        map.removeLayer(layer);
      }
    };
  }, [map]);

  useEffect(() => {
    const layer = layerRef.current;
    if (!map) return;
    if (!layer) return;
    if (!features) return;

    layer.getSource()?.clear();
    layer.getSource()?.addFeatures(features);
  }, [map, features]);

  return null;
}
