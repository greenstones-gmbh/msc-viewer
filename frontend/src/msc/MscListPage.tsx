import { ButtonToolbar } from "react-bootstrap";

import {
  ActionButton,
  Page,
  Paging,
  QueryInput,
  QuickTable,
} from "@greenstones/qui-bootstrap";
import { Column, exportToCsv } from "@greenstones/qui-core";
import { ReactNode } from "react";
import { MscPageHeader } from "./MscPageHeader";
import { MscObj, MscObjListResult, useMscContext } from "./MsgObj";
import { AssistantButton } from "@greenstones/qui-ai";
import { ApplicationAgentButton } from "../agent/MscViewerChatButton";

export function MscListPage({
  listData,
  title,
  cols,
  csvFileName = "file",
  toolbarAddons,
}: {
  listData: MscObjListResult;
  title: ReactNode;
  cols: Column<MscObj>[];
  csvFileName?: string;
  toolbarAddons?: ReactNode;
}) {
  const {
    items,
    sorting,
    paging,
    query,
    cmd,
    reload,
    isPending,
    error,
    rawData,
  } = listData;

  const context = useMscContext();

  return (
    <Page
      loading={isPending}
      error={error}
      header={
        <MscPageHeader cmd={cmd} reload={reload}>
          {title}
        </MscPageHeader>
      }
      subheader={
        query && (
          <ButtonToolbar className="">
            <QueryInput query={query} className="me-4" placeholder="Filter" />

            {toolbarAddons}

            <ActionButton
              className="ms-2"
              size="sm"
              variant="outline-primary"
              onClick={async (e) => {
                if (rawData) {
                  exportToCsv(cols, rawData, `${csvFileName}_${context.mscId}`);
                }
              }}
            >
              Export as csv
            </ActionButton>
          </ButtonToolbar>
        )
      }
      footer={<Paging paging={paging} />}
    >
      <QuickTable<MscObj> items={items} sorting={sorting} columns={cols} />
    </Page>
  );
}
