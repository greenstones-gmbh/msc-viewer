import { ActionButton, MessagePage, Page } from "@clickapp/qui-bootstrap";
import { useState } from "react";
import { Button, ButtonToolbar, Form, InputGroup } from "react-bootstrap";
import { SubmitHandler, useForm } from "react-hook-form";
import { useParams } from "react-router-dom";
import { IGraphStyles } from "../clickapp-bootstrap/graph/Graph";
import { GraphView } from "../clickapp-bootstrap/graph/GraphView";
import { useGraphContext } from "./MscGraphContext";

export function GraphPage({ styles }: { styles: IGraphStyles }) {
  const { mscId } = useParams();
  const { graphAvailable, reload, graphDatabaseAvailable, isPending } =
    useGraphContext();

  const q1 = `match p=(n:\`${mscId}\`)-[r]->(m:\`${mscId}\`) return p LIMIT 600`;

  const [query, setQuery] = useState<string>(q1);

  const { register, handleSubmit } = useForm<any>();
  const onSubmit: SubmitHandler<any> = async (data: any) => {
    console.log(data);
    var q = q1;

    if (!!data.query) {
      const where = (nn: string) =>
        data.query
          .trim()
          .toLowerCase()
          .split(" ")
          .map((s: string) => s.trim())
          .filter((s: string) => s)
          .map((s: string) => `${nn}.fulltext CONTAINS "${s}"`)
          .join(" and ");

      q = `MATCH (n:\`${mscId}\`) where ${where(
        "n"
      )} return n limit 300; MATCH p=(n:\`${mscId}\`  where ${where(
        "n"
      )})--(nn:\`${mscId}\`  where ${where("nn")}) return p limit 300;`;
    }

    setQuery(q);
  };

  if (!isPending && !graphAvailable)
    return (
      <MessagePage>
        The configuration graph is not available.
        <p />
        {graphDatabaseAvailable && (
          <ActionButton variant="primary" onClick={async (e) => reload?.()}>
            Load data and create graph
          </ActionButton>
        )}
      </MessagePage>
    );

  return (
    <Page header="Graph">
      <ButtonToolbar className="ps-1 pb-2">
        <Form onSubmit={handleSubmit(onSubmit)}>
          <InputGroup size="sm" className="me-2">
            <Form.Control
              type="search"
              placeholder="Search term..."
              {...register("query", {})}
            />
            <Button size="sm" variant="outline-secondary" type="submit">
              Run query
            </Button>
          </InputGroup>
        </Form>
      </ButtonToolbar>
      {!isPending && <GraphView styles={styles} query={query} />}
    </Page>
  );
}
