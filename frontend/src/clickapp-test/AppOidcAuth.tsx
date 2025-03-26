import { OidcAuth, keycloacRoleMapper } from "../clickapp-oidc/OidcAuth";
import { AppRoutes } from "./AppRoutes";

export function App() {
  return (
    <OidcAuth
      {...keycloakSettings}
      // clientSettings={{
      //   authority: "http://localhost:8080/realms/test1",
      //   client_id: "test-client-id",
      //   redirect_uri: "http://localhost:3000",
      //   post_logout_redirect_uri: "http://localhost:3000",
      // }}
      afterLoginPath="/page1"
      afterLogoutPath="/page3"
    >
      <AppRoutes />
    </OidcAuth>
  );
}

export default App;

const azureSettings = {
  clientId: "6e7b4d56-5b81-44e7-9d51-dfbe67df0617",
  authority: "https://login.microsoftonline.com/common",
};

const keycloakSettings = {
  authority: "http://localhost:8080/realms/test1",
  clientId: "test-client-id",
  roleMapper: keycloacRoleMapper("test-client-id"),
};
