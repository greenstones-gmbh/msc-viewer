import { Nav, Navbar } from "react-bootstrap";
import { useParams } from "react-router-dom";
import { useGraphContext } from "./MscGraphContext";

import {
  ActionButton,
  AppLayout,
  LayoutSwitcherDropdown,
  NavbarAuthButtons,
  NavbarBrand,
  NavLink,
} from "@clickapp/qui-bootstrap";
import { DateFormatter } from "@clickapp/qui-core";
import { BsHouse } from "react-icons/bs";
import { HiOutlineServer } from "react-icons/hi2";
import { PiGraphBold } from "react-icons/pi";

import { PropsWithChildren, ReactNode } from "react";

function TopNav() {
  const { mscId } = useParams();
  const { graphAvailable, graphDatabaseAvailable, date, reload, isPending } =
    useGraphContext();
  return (
    <>
      <Nav className="me-4">
        <NavLink to={`/${mscId}`}>Types</NavLink>
        <NavLink to={`/${mscId}/graph`}>Graph</NavLink>
      </Nav>

      {!isPending && !graphAvailable && (
        <Navbar.Text className="me-2 text-danger">
          Graph is not available
        </Navbar.Text>
      )}

      {!isPending && graphDatabaseAvailable && (
        <ActionButton
          variant="outline-primary"
          size="sm"
          onClick={async (e) => await reload?.()}
        >
          {graphAvailable ? "Update graph" : "Load data and create graph"}
        </ActionButton>
      )}

      {!isPending && graphAvailable && date && (
        <Navbar.Text className="ms-2 text-muted">
          Created: <DateFormatter isoString={date} />{" "}
        </Navbar.Text>
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
        <NavLink to={`/${mscId}/graph`} icon={PiGraphBold}>
          Graph
        </NavLink>
      </Nav>
    </>
  );
}
