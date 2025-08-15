import { createDetails, DetailModelBuilder } from "@clickapp/qui-core";
import { DependencyList, useMemo } from "react";
import { Tab } from "react-bootstrap";
import { useParams } from "react-router-dom";
import { GraphView } from "../../clickapp-bootstrap/graph/GraphView";
import { VectorLayer } from "../../clickapp-ol/VectorLayer";
import { GraphTabTitle } from "../GraphTabTitle";
import { MscMap, useFeaturePresenter } from "../map/MscMap";
import { selectionStyle } from "../map/Styles";
import { useGraphContext } from "../MscGraphContext";
import MscObjPage from "../MscObjPage";
import {
  mscUrl,
  prop,
  urlWithBasePath,
  useMscInstance,
  useMscObj,
} from "../MsgObj";
import { formatLink, formatText, Prop, TableTab } from "./ConfigType";
import { useGraphStyles } from "./ConfigTypesGraph";
import { RelationTable } from "./RelationTable";
import { formatTemplate } from "./utils";

export function ConfigTypeDetailPage({
  type,
  parentLabel,
  parentPath,
  title,
  detailProps,
  graphQueries,
  relatedTables,
}: {
  type: string;
  parentLabel: string;
  parentPath: string;
  title: { template: string; mapping: string };
  detailProps?: Prop[][];
  graphQueries?: string[];
  relatedTables?: TableTab[];
}) {
  const { mscId, id } = useParams();
  const url = mscUrl(mscId!, `${type}/${id}`);
  const objData = useMscObj(url);
  const detailModel = useDetailModel(type, detailProps!);

  const { graphAvailable } = useGraphContext();
  const defaultTab =
    graphAvailable && graphQueries && graphQueries.length > 0
      ? "graph"
      : "settings";

  //defaultTab = "map";

  const graphStyles = useGraphStyles();

  const tabs =
    relatedTables?.map((t) => (
      <Tab
        eventKey={t.relation}
        key={t.relation}
        title={<GraphTabTitle title={t.title} />}
      >
        <RelationTable
          id={id!}
          type={type}
          relation={t.relation}
          columns={t.columns}
          initialSort={t.initialSort}
        />
      </Tab>
    )) || [];

  return (
    <MscObjPage
      objData={objData}
      detailModel={detailModel}
      parentLabel={parentLabel}
      parentPath={parentPath}
      defaultTab={defaultTab}
      title={(v) => (v ? formatText(title, v) : "")}
      // headerAddon={(v) => (
      //   <Badge bg="warning">
      //     <MscPropValue obj={v} name="BTS ADMINISTRATIVE STATE" />
      //   </Badge>
      // )}
      tabs={(v) =>
        graphAvailable
          ? [
              <Tab eventKey="map" title={<GraphTabTitle title="Map" />}>
                <MapView type={type} id={id} />
                {/* <div style={{ height: 2 }} /> */}
              </Tab>,

              ...(graphQueries && graphQueries.length > 0
                ? [
                    <Tab
                      eventKey="graph"
                      title={<GraphTabTitle title="Graph" />}
                    >
                      <GraphView
                        styles={graphStyles}
                        queries={createQueries(mscId!, id!, graphQueries)}
                      />
                    </Tab>,
                  ]
                : []),
              ...tabs,
            ]
          : undefined
      }
    />
  );
}

export function useDetailModel(type: string, props?: Prop[][]) {
  const mscId = useMscInstance();

  return useDetaiModelBuilder(
    (b) => {
      props?.forEach((block, index) => {
        block.forEach((p) => {
          if (!p || p === "") return b.separator();
          if (typeof p === "string") return b.addLine(prop(p));
          else {
            b.addLine(
              prop(p.prop, {
                linkTo: p.linkTo
                  ? (entity) => formatLink(mscId, p.linkTo!, entity)
                  : undefined,
                ...(p.label ? { label: p.label } : {}),
              })
            );
          }
        });
        if (index !== props.length - 1) b.block();
      });
    },
    [type]
  );
}

export function useDetaiModelBuilder<Type>(
  configurer?: (builder: DetailModelBuilder<Type>) => void,
  deps?: DependencyList
) {
  return useMemo(() => {
    return createDetails<Type>(configurer);
  }, deps || []);
}

export function createQueries(
  mscId: string,
  id: string,
  graphQueries?: string[]
): string[] {
  console.log("createQueries", mscId, id, graphQueries);

  if (!graphQueries) return [];
  const d = graphQueries.map((q) =>
    formatTemplate(q, { MSC: "`" + mscId + "`", ID: id })
  );
  console.log("createQueries", d);

  return d;
}

function MapView({ type, id }: any) {
  const mscId = useMscInstance();
  const { view, features } = useFeaturePresenter(
    urlWithBasePath(`/api/gis/msc/${mscId}/${type}/${id}`),
    selectionStyle(type)
  );
  return (
    <MscMap visibleLayers={[type]} view={view}>
      <VectorLayer features={features} />
    </MscMap>
  );
}
