import { Route, Routes } from "react-router-dom";
import { ConfigType } from "./ConfigType";
import { ConfigTypeContainer } from "./ConfigTypeContainer";
import { ConfigTypeDetailPage } from "./ConfigTypeDetailPage";
import { ConfigTypeListPage } from "./ConfigTypeListPage";

export function ConfigTypeRoute({ configType }: { configType: ConfigType }) {
  const type = configType.type;
  return (
    <Routes>
      <Route
        key={type}
        path={type}
        element={
          <ConfigTypeContainer
            type={type}
            initialSort={configType.list.initialSort}
          />
        }
      >
        <Route
          index
          element={
            <ConfigTypeListPage
              title={configType.list.title}
              type={type}
              columns={configType.list.columns}
            />
          }
        />

        {configType.detail && (
          <Route
            path=":id"
            element={
              <ConfigTypeDetailPage
                type={type}
                title={configType.detail!.title}
                parentPath={`/${type}`}
                parentLabel={configType.list?.title}
                detailProps={configType.detail?.props}
                graphQueries={configType.detail?.graphQueries}
                relatedTables={configType.detail?.relatedTables}
              />
            }
          />
        )}
      </Route>
    </Routes>
  );
}
