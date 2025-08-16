import { Button, Nav, Navbar, Spinner } from "react-bootstrap";
import { useParams } from "react-router-dom";
import { useGraphContext } from "./MscGraphContext";

import {
  ActionButton,
  AppLayout,
  NavbarAuthButtons,
  NavbarBrand,
  NavLink,
} from "@clickapp/qui-bootstrap";
import { DateFormatter } from "@clickapp/qui-core";
import { BsHouse } from "react-icons/bs";
import { HiOutlineServer } from "react-icons/hi2";
import { PiGraphBold } from "react-icons/pi";

import { PropsWithChildren, ReactNode, useEffect, useRef } from "react";
import { useMapLayers } from "./types/ConfigTypesContext";
import { PiMapPinSimpleAreaFill } from "react-icons/pi";

function TopNav() {
  const { mscId } = useParams();
  const {
    graphAvailable,
    graphDatabaseAvailable,
    date,
    reload,
    isPending,
    status,
    reloadGraphStatus,
  } = useGraphContext();

  const layers = useMapLayers();

  useInterval(reloadGraphStatus ? reloadGraphStatus : () => {}, 5000);

  return (
    <>
      <Nav className="me-4">
        <NavLink to={`/${mscId}`}>Types</NavLink>

        {layers && layers.length > 0 && (
          <NavLink to={`/${mscId}/map`}>Map</NavLink>
        )}

        <NavLink to={`/${mscId}/graph`}>Graph</NavLink>
      </Nav>

      {!isPending && !graphAvailable && (
        <Navbar.Text className="me-2 text-danger">
          Graph is not available
        </Navbar.Text>
      )}

      {graphDatabaseAvailable && status !== "loading" && (
        <ActionButton
          variant="outline-primary"
          size="sm"
          onClick={async (e) =>
            reload?.().then(() => {
              reloadGraphStatus?.();
            })
          }
        >
          {graphAvailable ? "Update graph" : "Load data and create graph"}
        </ActionButton>
      )}

      {graphAvailable && date && status !== "loading" && (
        <Navbar.Text className="ms-2 text-muted">
          Created: <DateFormatter isoString={date} />{" "}
        </Navbar.Text>
      )}

      {graphAvailable && status === "loading" && (
        <>
          <Navbar.Text className="ms-2 text-muted">
            <Spinner animation="border" size="sm" /> Updating graph...
          </Navbar.Text>
        </>
      )}

      {/* <LayoutSwitcherDropdown className="ms-auto me-1" /> */}
      <NavbarAuthButtons />

      {/* <LayoutSwitcherDropdown /> */}
    </>
  );
}

export function MssAppLayout({ sidenav }: { sidenav: ReactNode }) {
  //brand={<NavbarBrand name="MSS Viewer" icon={BsBox} />}
  return (
    <AppLayout
      layout="brand-sidebar"
      topnavClassName="bg-white border-bottom"
      sidebarTheme="dark"
      sidebarClassName="bg-purple border-end"
      brand={<NavbarBrand name="MSC Viewer" icon={PiGraphBold} />}
      sidenav={sidenav}
      topnav={<TopNav />}
    />
  );
}

export function MscNoSidebarLayout() {
  //brand={<NavbarBrand name="MSS Viewer" icon={BsBox} />}
  return (
    <AppLayout
      layout="topnav-fluid"
      topnavClassName="bg-purple border-bottom"
      topnavTheme="dark"
      sidebarTheme="dark"
      width={0}
      sidebarClassName="bg-purple border-end"
      brand={<NavbarBrand name="MSC Viewer" icon={PiGraphBold} />}
      sidenav={<SidebarNav />}
      topnav={
        <>
          <Nav className="me-auto"></Nav>
          <NavbarAuthButtons variant="light" />
        </>
      }
    />
  );
}

export function SidebarNav({ children }: PropsWithChildren) {
  const { mscId } = useParams();
  const layers = useMapLayers();
  return (
    <>
      <Nav variant="pills" className="flex-column mt-3">
        <NavLink to="/" icon={BsHouse} text="Home" />
      </Nav>

      <Nav variant="pills" className="flex-column mt-3">
        <NavLink to={`/${mscId}`} end icon={HiOutlineServer}>
          <span className=" fw-bold" style={{ fontSize: "120%" }}>
            {mscId}
          </span>
        </NavLink>

        {children}
      </Nav>

      <Nav variant="pills" className="flex-column mt-3">
        {layers && layers.length > 0 && (
          <NavLink to={`/${mscId}/map`} icon={PiMapPinSimpleAreaFill}>
            Map
          </NavLink>
        )}
        <NavLink to={`/${mscId}/graph`} icon={PiGraphBold}>
          Graph
        </NavLink>
      </Nav>
    </>
  );
}

export function useInterval(callback: () => void, delay: number) {
  const savedCallback = useRef(callback);

  // Remember the latest callback if it changes.
  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  // Set up the interval.
  useEffect(() => {
    // Don't schedule if no delay is specified.
    if (delay === null) {
      return;
    }

    const id = setInterval(() => savedCallback.current(), delay);

    return () => clearInterval(id);
  }, [delay]);
}
