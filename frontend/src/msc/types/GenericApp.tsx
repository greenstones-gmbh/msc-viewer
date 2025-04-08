import { Route } from "react-router-dom";

import DashboardPage from "../DashboardPage";

import { Page, SignInPage } from "@clickapp/qui-bootstrap";
import { AppRoutes, ModalContextProvider } from "@clickapp/qui-core";
import { TokenAuth } from "../../clickapp/auth/TokenAuth";
import { GraphPage } from "../GraphPage";
import InstancesPage from "../InstancesPage";
import { MscNoSidebarLayout, MssAppLayout, SidebarNav } from "../MscAppLayouts";
import { GraphContextProvider } from "../MscGraphContext";

import "@clickapp/qui-bootstrap/dist/style.css";

import { ConfigTypesContextProvider } from "./ConfigTypesContext";

import { NavLink } from "@clickapp/qui-bootstrap";
import { IconType } from "react-icons";
import { MdFolderOpen, MdOutlineCellTower } from "react-icons/md";
import { TbChartArea, TbSignalLte } from "react-icons/tb";
import { TfiViewList } from "react-icons/tfi";
import { useMscInstance } from "../MsgObj";
import { ConfigType } from "./ConfigType";
import { ConfigTypeRoute } from "./ConfigTypeRoute";
import { useConfigTypes } from "./ConfigTypesContext";
import { useConfigTypesGraph, useGraphStyles } from "./ConfigTypesGraph";
import { MapPage } from "../MapPage";

export function GenericApp() {
  return (
    <TokenAuth
      loginPath="/login"
      createTokenUri="/msc-viewer/api/token"
      verifyTokenUri="/msc-viewer/api/validate"
    >
      <ModalContextProvider>
        <AppRoutes
          protected={
            <>
              <Route element={<MscNoSidebarLayout />}>
                <Route index element={<InstancesPage />} />
              </Route>
              <Route
                path=":mscId"
                element={
                  <ConfigTypesContextProvider>
                    <GraphContextProvider>
                      <AppLayout />
                    </GraphContextProvider>
                  </ConfigTypesContextProvider>
                }
              >
                <Route index element={<GenericDashboardPage />} />
                <Route path="graph" element={<GenericGraphPage />} />
                <Route path="map" element={<MapPage />} />
                <Route path="*" element={<ConfigTypeRoutes />} />
              </Route>
            </>
          }
          public={<></>}
          login={<SignInPage title="MSC Viewer" />}
        />
      </ModalContextProvider>
    </TokenAuth>
  );
}

function AppLayout() {
  return (
    <MssAppLayout
      sidenav={
        <SidebarNav>
          <ConfigTypeNavs />
        </SidebarNav>
      }
    />
  );
}

function ConfigTypeRoutes() {
  const types = useConfigTypes();
  if (!types) return <></>;
  return (
    <>
      {types.map((type: ConfigType) => (
        <ConfigTypeRoute key={type.type} configType={type} />
      ))}
    </>
  );
}

function ConfigTypeNavs() {
  const mscId = useMscInstance();
  const types = useConfigTypes();

  if (!types) return <></>;

  return (
    <>
      {types.map((configType: ConfigType) => {
        return (
          <NavLink
            key={configType.type}
            to={`/${mscId}/${configType.type}`}
            icon={getIcon(configType.type)}
            text={configType.list.title}
          />
        );
      })}
    </>
  );
}

const Icons: Record<string, IconType> = {
  lacs: TbChartArea,
  cells: MdOutlineCellTower,
  "cell-lists": TfiViewList,
  ltes: TbSignalLte,
};

export function getIcon(type: string) {
  return Icons[type] || MdFolderOpen;
}

function GenericDashboardPage() {
  const { graph, styles } = useConfigTypesGraph();
  return <DashboardPage graph={graph} styles={styles} />;
}

function GenericGraphPage() {
  const styles = useGraphStyles();
  return <GraphPage styles={styles} />;
}
