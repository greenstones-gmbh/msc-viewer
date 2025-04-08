import { Circle, Point } from "ol/geom";
import Fill from "ol/style/Fill";
import RegularShape from "ol/style/RegularShape";
import Stroke from "ol/style/Stroke";
import Style, { StyleLike } from "ol/style/Style";
import Text from "ol/style/Text";

export const cellsSelectionStyle = function (feature: any, resolution: any) {
  return [
    cellsAsSelectedCircleStyle(feature),
    ...cellStyle(feature, resolution),
  ];
};

const cellsAsSelectedCircleStyle = function (feature: any) {
  return new Style({
    fill: new Fill({
      color: "rgba(255, 255, 255, 0.3)",
    }),
    stroke: new Stroke({
      color: "red",
      lineDash: [8],
      width: 2,
    }),
    geometry: function (feature) {
      return new Circle(
        (feature.getGeometry() as Point).getCoordinates(),
        feature.get("radius")
      );
    },
  });
};

export const cellsAsCircleStyle = function (feature: any) {
  return new Style({
    fill: new Fill({
      color: "rgba(255, 255, 255, 0.3)",
    }),
    stroke: new Stroke({
      color: "rgba(255, 255, 255, 1)",
      width: 2,
    }),
    geometry: function (feature) {
      return new Circle(
        (feature.getGeometry() as Point).getCoordinates(),
        feature.get("radius")
      );
    },
  });
};

export const cellStyle = function (feature: any, resolution: any) {
  const shadow = new Style({
    image: new RegularShape({
      points: 3,
      radius: 10, // Etwas größer als das eigentliche Dreieck
      fill: new Fill({ color: "rgba(255,255,255,0.2)" }), // Transparenter Schatten
      stroke: new Stroke({ color: "rgba(255,255,255,0.2)", width: 1 }),
      displacement: [0, 0], // Leichte Verschiebung nach unten rechts
    }),
  });

  const triagle = new Style({
    image: new RegularShape({
      points: 3, // 3 Punkte für ein Dreieck
      radius: 6, // Größe des Dreiecks
      fill: new Fill({ color: "rgba(255, 0, 0, 1)" }), // Füllfarbe
      stroke: new Stroke({ color: "red", width: 2 }), // Randfarbe
    }),
  });

  const triagleWithText = new Style({
    image: new RegularShape({
      points: 3, // 3 Punkte für ein Dreieck
      radius: 6, // Größe des Dreiecks
      fill: new Fill({ color: "rgba(255, 0, 0, 1)" }), // Füllfarbe
      stroke: new Stroke({ color: "red", width: 2 }), // Randfarbe
    }),
    text: new Text({
      text: feature.get("NAME"), // 🔤 Dein Label-Text
      font: "10px Arial", // Schriftgröße und Font
      fill: new Fill({ color: "black" }), // Textfarbe
      stroke: new Stroke({ color: "rgba(255, 255, 255, 0.7)", width: 10 }), // Kontrast für bessere Sichtbarkeit
      offsetY: 15, // Verschiebt das Label über das Dreieck
    }),
  });

  if (resolution < 30) {
    return [shadow, triagleWithText];
  }

  return [shadow, triagle];
};

export const lacSelectionStyle = function (feature: any) {
  const c = stringToRGBA(feature.get("LAC"));
  return new Style({
    stroke: new Stroke({
      color: "blue",
      lineDash: [4],
      width: 2,
    }),
    fill: new Fill({
      color: c,
    }),
  });
};

export const lacStyle = function (feature: any) {
  const c = stringToRGBA(feature.get("LAC"));
  return new Style({
    stroke: new Stroke({
      color: "white",
      lineDash: [4],
      width: 2,
    }),
    fill: new Fill({
      color: c,
    }),
  });
};

export const gcaStyle = function (feature: any) {
  //const c = stringToRGBA(feature.get("NAME"), 0.02);
  const c = `rgba(100, 100, 100, 0.01)`;
  return new Style({
    stroke: new Stroke({
      color: "white",
      lineDash: [4],
      width: 1,
    }),
    fill: new Fill({
      color: c,
    }),
  });
};

export const gcaSelectionStyle = function (feature: any) {
  //const c = stringToRGBA(feature.get("NAME"), 0.02);
  const c = `rgba(0, 0, 255, 0.1)`;
  return new Style({
    stroke: new Stroke({
      color: "blue",
      lineDash: [4],
      width: 2,
    }),
    fill: new Fill({
      color: c,
    }),
  });
};

export const gcrefSelectionStyle = function (feature: any) {
  //const c = stringToRGBA(feature.get("NAME"), 0.02);
  const c = `rgba(255, 255, 0, 0.1)`;
  return new Style({
    stroke: new Stroke({
      color: "yellow",
      lineDash: [4],
      width: 2,
    }),
    fill: new Fill({
      color: c,
    }),
  });
};

function stringToRGBA(str: string, alpha: number = 0.7): string {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash);
  }

  // Extract RGB values
  const r = (hash & 0xff0000) >> 16;
  const g = (hash & 0x00ff00) >> 8;
  const b = hash & 0x0000ff;

  return `rgba(${r}, ${g}, ${b}, ${alpha})`; // RGBA format with transparency
}

// export const Selection: Record<string, StyleLike> = {
//   cells: cellsSelectionStyle,
//   lacs: lacSelectionStyle,
//   gca: gcaSelectionStyle,
// };

export const SelectionStyles: Record<string, Record<string, StyleLike>> = {
  gcas: {
    cells: cellStyle,
    gcas: gcaSelectionStyle,
  },
  cells: {
    cells: cellsSelectionStyle,
  },

  lacs: {
    cells: cellStyle,
    lacs: lacSelectionStyle,
  },

  gcrefs: {
    cells: cellStyle,
    gcas: gcaSelectionStyle,
    gcrefs: gcrefSelectionStyle,
  },

  "cell-lists": {
    cells: cellStyle,
    "cell-lists": gcaSelectionStyle,
  },
};

export const selectionStyle =
  (type: string) => (feature: any, resolution: any) => {
    const typeStyles = SelectionStyles[type];

    const typeName = feature.get("_type");
    const style = typeStyles[typeName];

    return (style as any)?.(feature, resolution);
  };

export const TypeStyles: Record<string, StyleLike> = {
  lacs: lacStyle,
  cells: cellStyle,
  "cell-coverages": cellsAsCircleStyle,
  ltes: cellStyle,
  "cell-lists": gcaStyle,
  gcas: gcaStyle,
  gcrefs: gcaStyle,
};

export const getNamedStyle = (type: string) => {
  const typestyle = TypeStyles[type];
  return typestyle;
};
