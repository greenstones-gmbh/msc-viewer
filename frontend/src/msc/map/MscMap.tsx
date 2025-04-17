import { useFetch } from "@clickapp/qui-core";
import { Feature } from "ol";
import { FeatureLike } from "ol/Feature";
import { ViewOptions } from "ol/View";
import { createEmpty, extend } from "ol/extent";
import GeoJSON from "ol/format/GeoJSON";
import { StyleLike } from "ol/style/Style";
import { PropsWithChildren, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { GeoJSONLayer } from "../../clickapp-ol/GeoJSONLayer";
import { LayerControl } from "../../clickapp-ol/LayerControl";
import { Map } from "../../clickapp-ol/Map";
import { Tooltip, TooltipOverlay } from "../../clickapp-ol/Tooltip";
import { urlWithBasePath, useMscInstance } from "../MsgObj";
import { useMapLayers } from "../types/ConfigTypesContext";
import { getNamedStyle } from "./Styles";

export function MscMap({
  visibleLayers,
  children,
  view,
}: PropsWithChildren<{ visibleLayers?: string[]; view?: ViewOptions }>) {
  const mscId = useMscInstance();

  const url = (type: string) =>
    urlWithBasePath(`/api/gis/msc/${mscId}/${type}`);

  const isLayerVisible = (name: string) =>
    !visibleLayers || visibleLayers.indexOf(name) !== -1;

  const layers = useMapLayers();
  console.log("layers", layers);
  if (!layers) return null;

  return (
    <Map view={view}>
      {layers?.map((l) => (
        <GeoJSONLayer
          key={l.layer}
          url={url(l.path)}
          style={getNamedStyle(l.style || l.layer)}
          name={l.layer}
          title={l.title}
          visible={isLayerVisible(l.layer)}
          //visible={l.enabled}
          maxResolution={l.maxResolution}
        />
      ))}

      {/*  <GeoJSONLayer
        url={url("gcas")}
        style={gcaStyle}
        name="gcas"
        title="GCAs"
        maxResolution={150}
        visible={isLayerVisible("gcas")}
      />

      <GeoJSONLayer
        url={url("gcrefs")}
        style={gcaStyle}
        name="gcrefs"
        title="GCREFs"
        maxResolution={150}
        visible={isLayerVisible("gcrefs")}
      />

      <GeoJSONLayer
        url={url("cell-lists")}
        style={gcaStyle}
        name="cell-lists"
        title="Cell Lists"
        maxResolution={150}
        visible={isLayerVisible("cell-lists")}
      />

      <GeoJSONLayer
        url={url("cells")}
        style={cellsAsCircleStyle}
        name="cells-coverage"
        title="Cells (Coverage)"
        maxResolution={150}
        visible={isLayerVisible("cells-coverage")}
      />

      <GeoJSONLayer
        url={url("cells")}
        style={cellStyle}
        name="cells"
        title="Cells"
        maxResolution={450}
        visible={isLayerVisible("cells")}
      />

      <GeoJSONLayer
        url={url("ltes")}
        style={cellStyle}
        name="ltes"
        title="LTE Confs"
        maxResolution={450}
        visible={isLayerVisible("ltes")}
      /> */}

      {children}

      {/* <InfoPopup
        layerNames={["cells"]}
        popup={(f) => (
          <Popup>
            <PopupContent feature={f} />
          </Popup>
        )}
      /> */}
      <Tooltip
        tooltip={(fs) => (
          <TooltipOverlay>
            <TooltipContent features={fs} />
          </TooltipOverlay>
        )}
      />
      <LayerControl />
    </Map>
  );
}

function PopupContent({ feature }: any) {
  const mscId = useMscInstance();
  return (
    <>
      <div className="mb-1">
        <span className="text-muted me-1">
          {feature.get("_type").toUpperCase()}
        </span>
        <Link to={`/${mscId}/${feature.get("LINK")}`}>
          <b>{feature.get("NAME")}</b>
        </Link>
      </div>
      <div>
        <b>NUMBER</b>: {feature.get("NUMBER")}
      </div>
      <div>
        <b>LAC</b>: {feature.get("LAC")}
      </div>
      <div>
        <b>MCC</b>: {feature.get("MCC")}
      </div>
      <div>
        <b>MNC</b>: {feature.get("MNC")}
      </div>
      <div>
        <b>CI</b>: {feature.get("CI")}
      </div>
    </>
  );
}

function TooltipContent({ features }: { features?: FeatureLike[] }) {
  const mscId = useMscInstance();

  const groups = groupBy(features || [], (f) => f.get("_type"));

  return (
    <>
      {Object.keys(groups).map((group) => {
        const features = groupBy(
          groups[group],
          (f) => `${f.get("NAME")}` as string
        );

        return (
          <div className="mb-1" style={{}}>
            <small className="text-muted">{group.toUpperCase()}</small>
            <br />
            <div style={{}}>
              {Object.keys(features)
                .sort()
                .map((featureName) => (
                  <Link
                    className="me-1 text-wrap"
                    to={`/${mscId}/${features[featureName][0].get("LINK")}`}
                  >
                    {features[featureName][0].get("NAME")}
                  </Link>
                ))}
            </div>
          </div>
        );
      })}
    </>
  );
}

const groupBy = <T, K extends keyof any>(arr: T[], key: (i: T) => K) =>
  arr.reduce((groups, item) => {
    (groups[key(item)] ||= []).push(item);
    return groups;
  }, {} as Record<K, T[]>);

export function useFeaturePresenter(url: string, style: StyleLike) {
  const { data } = useFetch(url);
  const [view, setView] = useState<ViewOptions | undefined>();
  const [selection, setSelection] = useState<Feature[] | null>();

  useEffect(() => {
    if (!data) return;
    const features = new GeoJSON().readFeatures(data, {});
    features.forEach((f) => f.setStyle(style));
    setSelection(features);
    console.log("feature", features[0]);
    if (features.length > 0) {
      // features.getFeaturesExtent;
      // const geometry = features[0].getGeometry();
      // if (!geometry) return;
      // const extent = geometry.getExtent();

      const extent = getFeaturesExtent(features);
      setView({ extent });
    }
  }, [data]);

  return { view, features: selection };
}

function getFeaturesExtent(features: Feature[]): number[] {
  let extent = createEmpty(); // Start with an empty extent
  features
    .filter((f) => f.getGeometry())
    .forEach((feature) => {
      if (feature.getGeometry()) {
        extend(extent, feature!.getGeometry()!.getExtent());
      }
    });
  return extent;
}
