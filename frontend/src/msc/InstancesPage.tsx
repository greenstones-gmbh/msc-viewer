import { Page, PageHeader } from "@clickapp/qui-bootstrap";
import { useFetch } from "@clickapp/qui-core";
import { Badge, Card, Col, Row } from "react-bootstrap";

import { Link } from "react-router-dom";

export default function InstancesPage() {
  const { data, error, isPending } = useFetch("/msc-viewer/api/msc");

  return (
    <Page
      loading={isPending}
      fillBody={true}
      scrollBody={false}
      error={error}
      header={<PageHeader>Instances</PageHeader>}
      fluid={false}
    >
      <Row xs={1} md={2} lg={4} className="g-4">
        {data?.map((instance: any, idx: number) => (
          <Col key={idx}>
            <Card
            // style={{ cursor: "pointer" }}
            >
              <Card.Body>
                <Card.Title>{instance.id}</Card.Title>
                <Card.Text>
                  {instance.badges &&
                    instance.badges.map((badge: any, i: number) => (
                      <Badge
                        key={i}
                        bg={badge.style || "secondary"}
                        className="mb-2 me-2"
                      >
                        {badge.label}
                      </Badge>
                    ))}
                  {instance.badges && <br />}
                  Host: {instance.host}
                  <br />
                  Port: {instance.port}
                  <br />
                  User: {instance.user || "-"}
                  <p />
                  <Link to={`/${instance.id}`}>View</Link>
                </Card.Text>
              </Card.Body>
            </Card>
          </Col>
        ))}
      </Row>
    </Page>
  );
}
