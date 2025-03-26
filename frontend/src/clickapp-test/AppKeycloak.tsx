import { ReactKeycloakProvider, useKeycloak } from "@react-keycloak/web";

import Keycloak from "keycloak-js";
import { useMemo } from "react";
import { AppRoutes } from "./AppRoutes";

const keycloak = new Keycloak({
  url: "http://localhost:8080",
  realm: "test1",
  clientId: "test-client-id",
});

export function App() {
  return (
    <ReactKeycloakProvider authClient={keycloak}>
      <KeycloakProvider>
        {(props: any) => <AppRoutes {...props} />}
      </KeycloakProvider>
    </ReactKeycloakProvider>
  );
}

export default App;

function KeycloakProvider({ children }: any) {
  const { keycloak, initialized } = useKeycloak();

  const isLoggedIn = keycloak.authenticated;

  const loginUrl = useMemo(() => {
    if (!initialized) return null;
    return keycloak.createLoginUrl({ redirectUri: "http://localhost:3000" });
  }, [initialized, keycloak]);

  if (!initialized) return null;

  const login = () => keycloak.login();
  const logout = () => keycloak.logout({ redirectUri: window.location.origin });
  const username = keycloak.tokenParsed?.preferred_username;
  const token = keycloak.token;

  const redirectToLogin = (targetPath?: string) => {
    window.location.replace(
      keycloak.createLoginUrl({
        redirectUri: `${window.location.origin}${targetPath}`,
      })
    );
  };

  return (
    <>
      {children({
        isLoggedIn,
        loginUrl,
        username,
        login,
        logout,
        token,
        redirectToLogin,
      })}
    </>
  );
}
