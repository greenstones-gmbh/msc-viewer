import { PropsWithChildren } from "react";
import { Tab } from "react-bootstrap";
import { VscLink } from "react-icons/vsc";
import { useGraphContext } from "./MscGraphContext";

export function GraphTabTitle({ title }: { title: string }) {
  return (
    <>
      <VscLink style={{ marginTop: -3 }} className="me-1" />
      {/* <Badge bg="secondary" className="me-1">
        Refs
      </Badge> */}
      {title}
    </>
  );
}

export function GraphTab({
  eventKey,
  title,
  children,
}: PropsWithChildren<{
  eventKey: string;
  title: string;
}>) {
  const { graphAvailable } = useGraphContext();
  if (!graphAvailable) return null;
  return (
    <>
      {graphAvailable && (
        <Tab eventKey={eventKey} title={<GraphTabTitle title={title} />}>
          {children}
        </Tab>
      )}
    </>
  );
}
