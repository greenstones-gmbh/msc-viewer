import { Tab } from "react-bootstrap";
import Breadcrumb from "react-bootstrap/Breadcrumb";
import { Link } from "react-router-dom";
import { MscObjView } from "./MscObjView";

import { DetailModelView, Page, Tabs } from "@clickapp/qui-bootstrap";
import { DetailModel } from "@clickapp/qui-core";
import { PropsWithChildren, ReactNode } from "react";
import { MscPageHeader } from "./MscPageHeader";
import { MscObj, useMscContext } from "./MsgObj";

export default function MscObjPage({
  objData,
  detailModel,
  defaultTab = "settings",
  parentPath,
  parentLabel,
  title,
  shortTitle,
  children,
  tabs,
  headerAddon,
}: PropsWithChildren<{
  detailModel: DetailModel<MscObj>;
  defaultTab?: string;
  objData: any;
  parentPath: string;
  parentLabel: string;
  title: (v: MscObj) => ReactNode;
  shortTitle?: (v: MscObj) => ReactNode;
  tabs?: (v: MscObj) => JSX.Element[] | undefined;
  headerAddon?: (v: MscObj) => ReactNode;
}>) {
  const { data, reload, error, isPending } = objData;
  const { data: cell, ...cmd } = data || {};
  const context = useMscContext();

  return (
    <Page
      loading={isPending}
      error={error}
      breadcrumb={
        <Breadcrumb>
          <Breadcrumb.Item
            linkAs={Link}
            linkProps={{ to: `/${context.mscId}` }}
          >
            {context.mscId}
          </Breadcrumb.Item>
          <Breadcrumb.Item
            linkAs={Link}
            linkProps={{ to: `/${context.mscId}${parentPath}` }}
          >
            {parentLabel}
          </Breadcrumb.Item>
          <Breadcrumb.Item active>
            {(cell && shortTitle?.(cell)) || title?.(cell)}
          </Breadcrumb.Item>
        </Breadcrumb>
      }
      header={
        <MscPageHeader cmd={cmd} reload={reload} addon={headerAddon?.(cell!)}>
          {cell && title?.(cell)}
        </MscPageHeader>
      }
    >
      {detailModel &&
        detailModel.blocks &&
        detailModel.blocks.length > 0 &&
        detailModel.blocks[0].lines.length > 0 && (
          <DetailModelView model={detailModel} value={cell} className="mb-4" />
        )}

      <Tabs defaultActiveKey={defaultTab} className="mt-0">
        {tabs?.(cell)}
        <Tab eventKey="settings" title={<>Settings</>}>
          <MscObjView obj={cell!} context={context} />
        </Tab>
        {/* <Tab eventKey="settings1" title={<>Settings1</>}>
          <MscObjView obj={cell!} context={context} />
        </Tab> */}
      </Tabs>

      {children}
    </Page>
  );
}
