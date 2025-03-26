import { User, UserManagerSettings } from "oidc-client-ts";
import { PropsWithChildren, useState } from "react";
import {
  AuthProvider,
  useAuth as useOidcContextAuth,
} from "react-oidc-context";
import { AuthContext, BaseAuthProps, useRoles } from "@clickapp/qui-core";
import { jwtDecode } from "jwt-decode";

export interface OidcAuthProps extends BaseAuthProps {
  clientSettings?: UserManagerSettings;
  authority?: string;
  clientId?: string;
}

export function OidcAuth({
  authority,
  clientId,
  clientSettings,
  ...props
}: PropsWithChildren<OidcAuthProps>) {
  const onSigninCallback = (_user: User | void): void => {
    window.history.replaceState({}, document.title, window.location.pathname);
  };

  const settings = clientSettings ?? {
    authority,
    client_id: clientId,
    redirect_uri: window.location.origin,
    post_logout_redirect_uri: window.location.origin,
    scope: "openid email roles",
    loadUserInfo: true,
  };

  return (
    <AuthProvider {...settings} onSigninCallback={onSigninCallback}>
      <OidcClientAuthProvider {...props} />
    </AuthProvider>
  );
}
function OidcClientAuthProvider({
  children,
  afterLoginPath,
  afterLogoutPath,
  roleMapper,
}: PropsWithChildren<BaseAuthProps>) {
  const auth = useOidcContextAuth();

  const { hasRole, roles } = useRoles(auth.user, roleMapper);

  const [hasTriedSignin, setHasTriedSignin] = useState<boolean>(false);

  if (auth.isLoading) {
    //return <div>Loading...</div>;
    return null;
  }

  if (auth.error) {
    return <div>Oops... {auth.error.message}</div>;
  }

  const login = async (returnTo?: string) => {
    const path = returnTo ?? afterLoginPath;
    const redirect_uri = path ? `${window.location.origin}${path}` : undefined;
    const params = redirect_uri ? { redirect_uri } : undefined;
    return auth.signinRedirect(params);
  };

  const logout = async (returnTo?: string) => {
    const path = returnTo ?? afterLogoutPath;
    const uri = path ? `${window.location.origin}${path}` : undefined;
    const params = uri ? { post_logout_redirect_uri: uri } : undefined;
    return auth.signoutRedirect(params);
  };

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated: auth.isAuthenticated,
        user: auth.user,
        userDisplayName: auth.user?.profile?.email,
        token: auth.user?.access_token,
        login,
        logout,

        afterLoginPath,
        afterLogoutPath,

        hasRole,
        roles,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const keycloacRoleMapper = (clientId: string) => {
  return async (user: any) => {
    if (!user) return [];
    const token = user.access_token;
    if (!token) return [];

    const claims = jwtDecode<any>(token);
    console.log("claims", claims);
    var roles: string[] = [
      ...claims.realm_access?.roles,
      ...claims.resource_access?.account?.roles,
      ...claims.resource_access?.[clientId].roles,
    ];

    return roles;
  };
};
