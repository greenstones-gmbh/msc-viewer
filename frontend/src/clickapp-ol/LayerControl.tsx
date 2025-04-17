import { useEffect, useState } from "react";
import { useMap } from "./Map";

export function LayerControl() {
  const map = useMap();

  const [layers, setLayers] = useState<any[]>([]);

  useEffect(() => {
    if (!map) return;

    const findLayer = () => {
      const l = map.getAllLayers().filter((l) => (l as any).name);
      l.reverse();
      return l;
    };

    map.getLayers().on("add", (e) => {
      setLayers(findLayer());
    });

    setLayers(findLayer());
  }, [map]);

  return (
    <div
      className="bg-white border-sm shadow-sm p-2"
      style={{
        position: "absolute",
        top: 10,
        right: 10,
        zIndex: 100000,
        userSelect: "none",
      }}
    >
      <b className="mb-3">Layers</b>
      <br />
      {layers.map((l) => (
        <div className="">
          <input
            type="checkbox"
            className="me-2"
            style={{ paddingTop: 2 }}
            checked={l.getVisible()}
            onClick={(e) => {
              l.setVisible(!l.getVisible());
              const layers =
                map?.getAllLayers().filter((l) => (l as any).name) || [];
              layers.reverse();
              setLayers(layers);
            }}
          />
          {(l as any).title || (l as any).name}
        </div>
      ))}
    </div>
  );
}
