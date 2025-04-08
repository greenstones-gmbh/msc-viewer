import { Overlay } from "ol";
import { FeatureLike } from "ol/Feature";
import { Point } from "ol/geom";
import { PropsWithChildren, useEffect, useRef, useState } from "react";
import { useMap } from "./Map";

export function InfoPopup({
  children,
  popup,
  layerNames = [],
}: PropsWithChildren<{
  layerNames?: string[];
  popup?: (feature: FeatureLike) => JSX.Element;
}>) {
  const map = useMap();

  const popupRef = useRef<any>();

  const [selection, setSelection] = useState<FeatureLike | undefined>();

  const layerFilter = (l: any) => {
    if (layerNames?.length > 0) {
      return layerNames.includes(l.name);
    }
    return true;
  };

  useEffect(() => {
    if (!map) return;

    const overlay = new Overlay({
      element: popupRef.current,
      positioning: "bottom-center",
      stopEvent: false,
      offset: [0, -10],
    });
    map?.addOverlay(overlay);

    map?.on("pointermove", function (event: any) {
      map.getTargetElement().style.cursor = map.hasFeatureAtPixel(event.pixel, {
        layerFilter,
      })
        ? "pointer"
        : "";
    });

    map?.on("singleclick", function (event) {
      var feature = map.forEachFeatureAtPixel(
        event.pixel,
        function (feature) {
          return feature;
        },
        {
          layerFilter,
        }
      );

      if (feature) {
        const coordinates = (feature.getGeometry() as Point).getCoordinates();
        overlay.setPosition(coordinates);
        setSelection(feature);
      } else {
        overlay.setPosition(undefined);
        setSelection(undefined);
      }
    });
  }, [map]);

  return (
    <>
      {children}
      <div ref={popupRef}>
        {selection &&
          (popup ? (
            popup(selection)
          ) : (
            <Popup>
              <PopupContent feature={selection} />
            </Popup>
          ))}
      </div>
    </>
  );
}

export function Popup({ children }: PropsWithChildren) {
  return (
    <div
      className="p-2 bg-white shadow-sm rounded mb-1"
      style={{ maxWidth: 400, fontSize: "86%" }}
    >
      {children}
    </div>
  );
}

export function PopupContent({ feature }: any) {
  return (
    <>
      {(feature as any)
        .getKeys()
        .filter((k: string) => k !== "geometry")
        .map((k: string) => (
          <div>
            <b>{k}</b>: {JSON.stringify(feature.get(k))}
          </div>
        ))}
    </>
  );
}
