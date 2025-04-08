import { Map as OlMap, View } from "ol";
import TileLayer from "ol/layer/Tile";
import "ol/ol.css";
import { fromLonLat, toLonLat } from "ol/proj";
import { OSM } from "ol/source";
import { ViewOptions } from "ol/View";
import {
  createContext,
  PropsWithChildren,
  useContext,
  useEffect,
  useRef,
  useState,
} from "react";

const MapContext = createContext<OlMap | undefined>(undefined);
export const useMap = () => {
  const map = useContext(MapContext);
  //if (!map) throw new Error("useMap must be used within a Map component");
  return map;
};

function getDefaultView(): ViewOptions {
  const saved = sessionStorage.getItem("map.view");

  if (saved) {
    try {
      const savedView = JSON.parse(saved);
      if (savedView.coords && savedView.zoom) {
        return {
          center: fromLonLat(savedView.coords),
          zoom: savedView.zoom,
        } as ViewOptions;
      }
    } catch (error) {}
  }

  return {
    center: fromLonLat([8.625286, 50.181481]),
    zoom: 14,
  };
}

export function Map({
  view,
  children,
}: PropsWithChildren<{
  view?: ViewOptions;
}>) {
  const mapNodeRef = useRef<any>(null);
  const [map, setMap] = useState<OlMap>();

  useEffect(() => {
    console.log("init.map", view);

    const osmLayer = new TileLayer({
      preload: Infinity,
      source: new OSM(),
      opacity: 1,
      className: "ol_bw",
    });

    const _map = new OlMap({
      target: mapNodeRef.current,
      layers: [osmLayer],
      view: new View(getDefaultView()),
    });

    _map.getView().on("change", () => {
      const view = _map.getView();
      const center = view.getCenter();
      const zoom = view.getZoom();
      if (center && zoom) {
        const c = toLonLat(center);

        sessionStorage.setItem(
          "map.view",
          JSON.stringify({ zoom: zoom, coords: c })
        );
      }
    });

    _map.on("click", (event) => {
      const coordinate = toLonLat(event.coordinate); // Convert to EPSG:4326 (Lon, Lat)
      const [lon, lat] = coordinate.map((c) => c.toFixed(6)); // Round to 6 decimals

      console.log(`Coords: ${lon},${lat}`, event.coordinate);
    });

    setMap(_map);

    console.log("map init");

    return () => {
      _map.setTarget(undefined);
    };
  }, []);

  useEffect(() => {
    console.log("update map view", view, map);

    if (!view) return;
    if (!map) return;

    if (view.center && view.zoom) {
      map.getView().animate({
        center: view.center,
        zoom: view.zoom,
        duration: 1000,
      });
    }

    if (view.extent && view.extent.length > 0 && view.extent[0] !== Infinity) {
      map.getView().fit(view.extent, {
        padding: [50, 50, 50, 50],
        maxZoom: 14,
      });
    }
  }, [view, map]);

  return (
    <MapContext.Provider value={map}>
      <div
        style={{
          height: "100%",
          width: "100%",
          position: "relative",
          overflow: "hidden",
        }}
        ref={mapNodeRef}
      >
        {children}
      </div>
    </MapContext.Provider>
  );
}
