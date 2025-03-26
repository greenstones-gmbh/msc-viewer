import { Route, useParams } from "react-router-dom";

import DashboardPage from "../msc/DashboardPage";

//import { SignInPage } from "./clickapp-bootstrap/SignInPage";

//import { AppRoutes } from "./clickapp/AppRoutes";
import { NavLink, SignInPage } from "@clickapp/qui-bootstrap";
import { AppRoutes, ModalContextProvider } from "@clickapp/qui-core";
import { TokenAuth } from "../clickapp/auth/TokenAuth";
import {
  MscNoSidebarLayout,
  MssAppLayout,
  SidebarNav,
} from "../msc/MscAppLayouts";
import { GraphContextProvider } from "../msc/MscGraphContext";
import { CellList } from "./celllists/CellList";
import { CellLists, CellListsContainer } from "./celllists/CellLists";
import { Cell } from "./cells/Cell";
import { Cells, CellsContainer } from "./cells/Cells";
import { Gca } from "./gcas/Gca";
import { Gcas, GcasContainer } from "./gcas/Gcas";
import { Gcref } from "./gcrefs/Gcref";
import { Gcrefs, GcrefsContainer } from "./gcrefs/Gcrefs";
import { GraphPage } from "../msc/GraphPage";
import InstancesPage from "../msc/InstancesPage";
import { LocationArea } from "./lacs/LocationArea";
import { LocationAreas, LocationAreasContainer } from "./lacs/LocationAreas";
import { LteConfig } from "./ltes/LteConfig";
import { LteConfigs, LteConfigsContainer } from "./ltes/LteConfigs";

import "@clickapp/qui-bootstrap/dist/style.css";

import { Bs1Circle, Bs2Circle, Bs3Circle } from "react-icons/bs";
import { MdOutlineCellTower } from "react-icons/md";
import { TbChartArea, TbSignalLte } from "react-icons/tb";
import { IGraph } from "../clickapp-bootstrap/graph/Graph";
import { MscGraphStyles, MscTypeGraphStyles } from "./CustomGraphStyles";

export function CustomApp() {
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
                  <GraphContextProvider>
                    <AppLayout />
                  </GraphContextProvider>
                }
              >
                <Route index element={<CustomDashboardPage />} />

                <Route path="lacs" element={<LocationAreasContainer />}>
                  <Route index element={<LocationAreas />} />
                  <Route path=":id" element={<LocationArea />} />
                </Route>

                <Route path="cells" element={<CellsContainer />}>
                  <Route index element={<Cells />} />
                  <Route path=":id" element={<Cell />} />
                </Route>

                <Route path="ltes" element={<LteConfigsContainer />}>
                  <Route index element={<LteConfigs />} />
                  <Route path=":id" element={<LteConfig />} />
                </Route>

                <Route path="gcas" element={<GcasContainer />}>
                  <Route index element={<Gcas />} />
                  <Route path=":id" element={<Gca />} />
                </Route>

                <Route path="gcrefs" element={<GcrefsContainer />}>
                  <Route index element={<Gcrefs />} />
                  <Route path=":id" element={<Gcref />} />
                </Route>

                <Route path="cell-lists" element={<CellListsContainer />}>
                  <Route index element={<CellLists />} />
                  <Route path=":id" element={<CellList />} />
                </Route>

                <Route
                  path="graph"
                  element={<GraphPage styles={MscGraphStyles} />}
                />
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
  const { mscId } = useParams();
  return (
    <MssAppLayout
      sidenav={
        <SidebarNav>
          <NavLink
            to={`/${mscId}/lacs`}
            icon={TbChartArea}
            text="Location Areas"
          />
          <NavLink to={`/${mscId}/cells`} icon={MdOutlineCellTower}>
            Cells and BTSs
          </NavLink>
          <NavLink to={`/${mscId}/cell-lists`} icon={Bs3Circle}>
            Cell Lists
          </NavLink>
          <NavLink to={`/${mscId}/ltes`} icon={TbSignalLte}>
            LTE Configs
          </NavLink>
          <NavLink to={`/${mscId}/gcas`} icon={Bs1Circle}>
            Group Call Areas
          </NavLink>
          <NavLink to={`/${mscId}/gcrefs`} icon={Bs2Circle}>
            Group Call Refs
          </NavLink>
        </SidebarNav>
      }
    />
  );
}

export default function CustomDashboardPage() {
  const { mscId } = useParams();
  const graph = createGraph(mscId || "-");
  return <DashboardPage graph={graph} styles={MscTypeGraphStyles} />;
}

function createGraph(mscId: string): IGraph {
  return {
    nodes: [
      { id: "gcref", labels: ["GCREF"], properties: [{ mscId }] },
      { id: "gca", labels: ["GCA"], properties: [{ mscId }] },
      { id: "cell", labels: ["BTS"], properties: [{ mscId }] },
      { id: "lte", labels: ["LTEConfig"], properties: [{ mscId }] },
      { id: "cell-list", labels: ["CELL_LIST"], properties: [{ mscId }] },
      { id: "lac", labels: ["LAC"], properties: [{ mscId }] },
    ],
    relationships: [
      {
        startId: "gcref",
        endId: "gca",
        id: "10",
      },

      {
        startId: "cell",
        endId: "lac",
        id: "11",
      },

      {
        startId: "lte",
        endId: "cell",
        id: "12",
      },

      {
        startId: "cell-list",
        endId: "cell",
        id: "13",
      },

      {
        startId: "gca",
        endId: "cell-list",
        id: "14",
      },
    ],
  };
}
