import { FeatureLike } from "ol/Feature";
import { PropsWithChildren, useEffect, useRef, useState } from "react";
import { useMap } from "./Map";

export function Tooltip({
  children,
  tooltip,
  layerNames = [],
}: PropsWithChildren<{
  layerNames?: string[];
  tooltip?: (features: FeatureLike[]) => JSX.Element;
}>) {
  const map = useMap();

  const popupRef = useRef<any>();
  const [selection, setSelection] = useState<FeatureLike[] | undefined>();

  const [point, setPoint] = useState<any | undefined>();
  const debounced = useDebounce(point, 600);

  useEffect(() => {
    if (!map) return;
    if (!debounced) return;
    console.log("debounced", debounced);

    const foundFeatures: FeatureLike[] = [];

    map.forEachFeatureAtPixel(
      debounced,
      function (feature) {
        foundFeatures.push(feature);
      },
      {
        layerFilter: (layer) => true,
      }
    );

    if (foundFeatures && foundFeatures.length > 0) {
      popupRef.current.style.top = debounced[1] + "px";
      popupRef.current.style.left = debounced[0] + "px";

      setSelection(foundFeatures);
    } else {
      setSelection(undefined);
    }
  }, [debounced, map]);

  useEffect(() => {
    if (!map) return;

    map?.on("pointermove", function (event) {
      setPoint(event.pixel);
      setSelection(undefined);
    });
  }, [map]);

  return (
    <>
      {children}

      <div
        ref={popupRef}
        style={{
          position: "absolute",
          top: 10,
          left: 100,
          zIndex: 90000,
          whiteSpace: "normal",
          wordBreak: "break-word",
          overflowWrap: "break-word",
        }}
      >
        {selection &&
          (tooltip ? (
            tooltip(selection)
          ) : (
            <TooltipOverlay>
              <TooltipContent features={selection} />
            </TooltipOverlay>
          ))}
      </div>
    </>
  );
}

export function TooltipOverlay({ children }: PropsWithChildren) {
  return (
    <div
      className="p-2 bg-white shadow-sm rounded mb-1 mt-2 ms-2"
      style={{
        maxWidth: 360,
        fontSize: "86%",
        zIndex: 1000,
      }}
    >
      {children}
    </div>
  );
}

export function TooltipContent({ features }: { features?: FeatureLike[] }) {
  return (
    <>
      {features?.map((feature) => (
        <>{feature.get("id").toUpperCase()}</>
      ))}
    </>
  );
}

function useDebounce(cb: any, delay: number) {
  const [debounceValue, setDebounceValue] = useState(cb);
  useEffect(() => {
    const handler = setTimeout(() => {
      setDebounceValue(cb);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [cb, delay]);
  return debounceValue;
}
