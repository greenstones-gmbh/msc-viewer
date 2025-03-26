import { jwtDecode } from "jwt-decode";
import { useEffect, useState } from "react";
import {
  Button,
  ButtonToolbar,
  Form,
  InputGroup,
  Nav,
  Navbar,
  Table,
} from "react-bootstrap";
import Container from "react-bootstrap/Container";
import { Outlet, Route, Routes } from "react-router-dom";
import {
  Paging,
  QueryCheckbox,
  QueryDropdown,
  QueryInput,
  SortingColumnHeader,
} from "@clickapp/qui-bootstrap";

import {
  Filters,
  Guard,
  ListSorting,
  Sorters,
  useArray,
  useAuth,
} from "@clickapp/qui-core";
import { NavLink } from "@clickapp/qui-bootstrap";

export function AppRoutes(props: any) {
  const { children } = props;
  return (
    <Routes>
      <Route path="/">
        <Route
          element={
            <>
              <BaseNavbar />
              <Container className="mt-4">
                <Outlet />
              </Container>
            </>
          }
        >
          <Route index element={<div>Home</div>} />
          <Route
            path="page1"
            element={
              <Guard redirectToLogin={false}>
                <Page1 />
              </Guard>
            }
          />
          <Route
            path="page2"
            element={
              <Guard role="role1">
                <Page2 />
              </Guard>
            }
          />

          <Route path="page3" element={<Page3 />} />

          <Route path="test1" element={<TableExample1 />} />
          <Route path="test2" element={<TableExample2 />} />
        </Route>
      </Route>
      {children}
    </Routes>
  );
}

function Page1() {
  const { user, token, roles } = useAuth();

  var decoded = null;
  try {
    decoded = token ? jwtDecode(token) : null;
  } catch (error) {}
  return (
    <>
      <h1>Page1 (Private)</h1>
      {JSON.stringify(roles)}
      <hr />
      <pre>
        <code>{JSON.stringify(user, null, 2)}</code>
      </pre>
      <hr />
      {token}
      <hr />
      <pre>
        <code>{JSON.stringify(decoded, null, 2)}</code>
      </pre>
    </>
  );
}

function Page3() {
  return (
    <>
      <h1>Page3 (Public)</h1>
    </>
  );
}

function Page2() {
  const [data, setData] = useState<any>(null);
  const { token } = useAuth();

  useEffect(() => {
    fetch("http://localhost:8090/test", {
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => res.json())
      .then((json) => {
        console.log(json);
        setData(json);
      });
  }, []);

  return (
    <>
      <h1>Page2 (Private)</h1>
      <p>{JSON.stringify(data)}</p>
    </>
  );
}

export function BaseNavbar() {
  const { login, logout, isAuthenticated, user, userDisplayName } = useAuth();

  return (
    <>
      <Navbar expand="md" className="bg-body-tertiary">
        <Container>
          <Navbar.Brand>Project</Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse id="basic-navbar-nav">
            <Nav className="me-auto">
              <Nav.Link as={NavLink} to="/">
                Home
              </Nav.Link>

              <Nav.Link as={NavLink} to="/page1">
                Page 1
              </Nav.Link>

              <Nav.Link as={NavLink} to="/page1" authenticated>
                Page 1 (auth)
              </Nav.Link>

              <Nav.Link as={NavLink} to="/page1" allowedRoles="role4">
                Page 1 (Role2)
              </Nav.Link>

              <Nav.Link as={NavLink} to="/page2">
                Page 2
              </Nav.Link>

              <Nav.Link as={NavLink} to="/page3">
                Page 3
              </Nav.Link>

              <Nav.Link as={NavLink} to="/test1">
                Test1
              </Nav.Link>

              <Nav.Link as={NavLink} to="/test2">
                Test2
              </Nav.Link>
            </Nav>

            {!isAuthenticated && (
              <Nav className="ms-auto">
                {/* <Nav.Link href={loginUrl || ""}>SignIn</Nav.Link> */}
                <Button
                  variant="outline-primary"
                  size="sm"
                  className="ms-2"
                  onClick={(e) => {
                    login();
                  }}
                >
                  Login
                </Button>
              </Nav>
            )}

            {isAuthenticated && (
              <Navbar.Text className="ms-auto">{userDisplayName}</Navbar.Text>
            )}
            {isAuthenticated && (
              <Nav>
                <Button
                  variant="outline-primary"
                  size="sm"
                  className="ms-2"
                  onClick={(e) => {
                    logout();
                  }}
                >
                  Logout
                </Button>
              </Nav>
            )}
          </Navbar.Collapse>
        </Container>
      </Navbar>
    </>
  );
}

interface TestRow {
  a?: string;
  b?: string;
  c?: string;
  d?: string;
  e?: string;
}

interface TestRowQuery {
  a?: string;
  b?: boolean;
  c?: number;
  d?: string;
}

const createData = (rows: number = 20): TestRow[] => {
  var data: TestRow[] = [];

  for (let index = 1; index <= rows; index++) {
    data.push({
      a: `a${index}`,
      b: `${index % 2 === 0 ? "odd" : "even"}`,
      c: `${index % 3 === 0 ? "deleted" : "active"}`,
      d: `d${index}`,
      e: `d${index}`,
    });
  }
  return data;
};

export const TEST_DATA = createData();

function TableExample1() {
  // const source = useMemo<
  //   ListSource<TestRow, { firstName?: string; lastName?: string }>
  // >(
  //   () =>
  //     createArraySource(createData(), {
  //       //filter: and(objectContains(), isEq("b", "lastName")),
  //       //filter: isEq("a", "lastName"),
  //       //filter: objectContains("firstName"),
  //       //filter: and(like("a", "firstName"), isEq("b", "lastName")),
  //       filter: Filters.and(
  //         Filters.objectContains("firstName"),
  //         Filters.isEq("b", "lastName")
  //       ),
  //     }),
  //   []
  // );

  type TestQuery = { query1?: string; type1?: string; deleted?: boolean };

  const { items, paging, query, sorting } = useArray(TEST_DATA, {
    paging: true,
    initialPageSize: 5,
    initialSort: { id: "c", direction: "desc" },

    filter: Filters.and(
      Filters.objectContains<TestQuery>("query1"),
      Filters.isEq<TestRow, TestQuery>("b", "type1"),
      Filters.isSatisfiedBy<TestRow, TestQuery>(
        (v, q) => !q?.deleted || v.c === "deleted"
      )
    ),
    sorter: Sorters.objectProps(),
  });

  return (
    <>
      {query && (
        <ButtonToolbar className="">
          <QueryInput query={query} field="query1" className="me-2" />
          <QueryDropdown
            query={query}
            field="type1"
            label="Type"
            values={["odd", "even"]}
            className="me-4"
          />
          <QueryCheckbox
            label="Only&nbsp;deleted"
            query={query}
            field="deleted"
            //{...query.field("deleted")}
          />
        </ButtonToolbar>
      )}

      <TestRowTable items={items} sorting={sorting} />
      <Paging paging={paging} />
    </>
  );
}

function TableExample2() {
  const { items, paging, query, sorting } = useArray(TEST_DATA, {
    paging: true,
    initialPageSize: 5,
    filter: Filters.objectContains(),
    sorter: Sorters.objectProps(),
  });

  return (
    <>
      <h1 className="mb-3">Projects</h1>
      {query && (
        <ButtonToolbar>
          <InputGroup>
            <Form.Control
              type="search"
              size="sm"
              className=""
              placeholder="Filter..."
              autoFocus
              {...query.bind()}
            />
          </InputGroup>

          {/* <Form className="">
            <Form.Check
              type="checkbox"
              checked={query?.[prop]}
              id={prop}
              label={"Editable"}
              onChange={(e) => {
                setQuery(
                  produce((draft) => {
                    draft[prop] = e.target.checked;
                  })
                );
              }}
            />
          </Form> */}
        </ButtonToolbar>
      )}
      <TestRowTable items={items} sorting={sorting} />
      <Paging paging={paging} />
    </>
  );
}

function TestRowTable({
  items,
  sorting,
}: {
  items?: TestRow[];
  sorting: ListSorting | undefined;
}) {
  return (
    <Table>
      <thead>
        <tr>
          <th>
            <SortingColumnHeader header="a" sorting={sorting} sortId={"a"} />
          </th>
          <th>
            <SortingColumnHeader header="b" sorting={sorting} sortId={"b"} />
          </th>
          <th>
            <SortingColumnHeader header="c" sorting={sorting} sortId={"c"} />
          </th>
          <th>
            <SortingColumnHeader header="d" sorting={sorting} sortId={"d"} />
          </th>
        </tr>
      </thead>
      <tbody>
        {items?.map((d, index) => (
          <tr key={index}>
            <td>{d.a}</td>
            <td>{d.b}</td>
            <td>{d.c}</td>
            <td>{d.d}</td>
          </tr>
        ))}
      </tbody>
    </Table>
  );
}

function SmartTable<Type>({ items }: { items?: Type[] }) {
  return (
    <Table>
      <thead>
        <th>a</th>
      </thead>
      <tbody>
        {items?.map((d, index) => (
          <tr key={index}>
            <td>{JSON.stringify(d)}</td>
          </tr>
        ))}
      </tbody>
    </Table>
  );
}
