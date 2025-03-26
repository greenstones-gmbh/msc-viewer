import { Button } from "react-bootstrap";

import { PropsWithChildren, ReactNode } from "react";
import { PageHeader } from "@clickapp/qui-bootstrap";

export function MscPageHeader({
  cmd,
  children,
  reload,
  addon,
}: PropsWithChildren<{ addon?: ReactNode; cmd: any; reload?: () => void }>) {
  return (
    <PageHeader
      addon={
        <>
          {addon}
          <div className="ms-auto mt-2 d-flex align-items-center">
            <div className="bg-light p-2 rounded border font-monospace">
              <span className="text-secondary">
                {cmd.info}
                {">"}
              </span>{" "}
              {cmd.command}
            </div>
            <div className="ms-2">
              <small className="ms-1 text-secondary">
                executed on
                <span className="text-dark ms-1 ">{cmd.timestamp}</span>
              </small>
            </div>
            <div className="ms-4">
              <Button variant="primary" onClick={reload}>
                Reload
              </Button>
            </div>
          </div>
        </>
      }
    >
      {children}
    </PageHeader>
  );
}
