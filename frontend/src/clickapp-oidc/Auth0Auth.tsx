import { Auth0Provider, useAuth0 } from "@auth0/auth0-react";
import { AuthContext, BaseAuthProps, useRoles } from "@clickapp/qui-core";
import { useAsyncMemo } from "@clickapp/qui-core";
import { PropsWithChildren } from "react";

export interface Auth0AuthProps extends BaseAuthProps {
  domain: string;
  clientId: string;
}

export function Auth0Auth({
  domain,
  clientId,
  children,
  afterLoginPath,
  afterLogoutPath,
}: PropsWithChildren<Auth0AuthProps>) {
  return (
    <Auth0Provider
      domain={domain}
      clientId={clientId}
      authorizationParams={{
        redirect_uri: window.location.origin,
        audience: "https://dev-og2yprza28iiv7iu.us.auth0.com/api/v2/",
        scope: "openid profile email roles",
      }}
    >
      <Auth0Connector
        afterLogoutPath={afterLogoutPath}
        afterLoginPath={afterLoginPath}
      >
        {children}
      </Auth0Connector>
    </Auth0Provider>
  );
}
function Auth0Connector({
  children,
  afterLoginPath,
  afterLogoutPath,
  roleMapper,
}: PropsWithChildren<BaseAuthProps>) {
  const auth = useAuth0();

  const { hasRole } = useRoles(auth.user, roleMapper);

  const { data: token } = useAsyncMemo(async () => {
    if (auth.isAuthenticated) {
      return await auth.getAccessTokenSilently({
        authorizationParams: {
          audience: "https://dev-og2yprza28iiv7iu.us.auth0.com/api/v2/",
          scope: "read:current_user",
        },
      });
    }
    return undefined;
  }, [auth.isAuthenticated]);

  if (auth.isLoading) {
    return null;
  }

  if (auth.error) {
    return <div>Oops... {auth.error.message}</div>;
  }

  const login = async (returnTo?: string) => {
    const path = returnTo ?? afterLoginPath;
    const redirect_uri = path ? `${window.location.origin}${path}` : undefined;
    const params = redirect_uri ? { redirect_uri } : undefined;
    return auth.loginWithRedirect();
  };

  const logout = async (returnTo?: string) => {
    const path = returnTo ?? afterLogoutPath;
    const uri = path ? `${window.location.origin}${path}` : undefined;
    const params = uri ? { returnTo: uri } : undefined;
    return auth.logout(/*{ logoutParams: params }*/);
  };

  console.log("user", auth.user);

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated: auth.isAuthenticated,
        user: auth.user,
        userDisplayName: auth.user?.email,
        token,
        login,
        logout,

        afterLoginPath,
        afterLogoutPath,

        hasRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
