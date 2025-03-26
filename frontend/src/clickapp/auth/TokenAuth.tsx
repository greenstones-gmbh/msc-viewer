import {
  AuthContext,
  BaseAuthProps,
  fetchJson,
  useRoles,
} from "@clickapp/qui-core";
import { jwtDecode } from "jwt-decode";
import { PropsWithChildren, useState } from "react";
import { useNavigate } from "react-router-dom";

export interface TokenAuthProps extends BaseAuthProps {
  verifyTokenUri: string;
  createTokenUri: string;
  loginPath: string;
}

export function TokenAuth({
  verifyTokenUri,
  createTokenUri,
  children,
  loginPath = "/login",
  afterLoginPath = "/",
  afterLogoutPath,
  roleMapper,
}: PropsWithChildren<TokenAuthProps>) {
  const navigate = useNavigate();

  const t = sessionStorage.getItem("auth.access_token");
  const u = t ? createUser(t) : undefined;
  const [user, setUser] = useState<any>(u);

  const { hasRole } = useRoles(user, roleMapper);

  // console.log("user", user);

  // if (isPending || isError) {
  //   return null;
  // }

  const login = async (returnTo?: string) => {
    navigate(loginPath, {
      state: {
        redirectTo: returnTo ?? afterLoginPath,
      },
    });
  };

  const logout = async (returnTo?: string) => {
    //await supabaseClient.auth.signOut();
    sessionStorage.removeItem("auth.access_token");
    setUser(undefined);
    navigate(returnTo ?? afterLogoutPath ?? "/");
  };

  const loginWithPassword = async (
    username: string,
    password: string,
    returnTo?: string
  ) => {
    const data = await fetchJson(createTokenUri, {
      method: "POST",
      skipToken: true,
      body: JSON.stringify({ username, password }),
    });

    console.log("loginWithPassword.done", data);

    const u = createUser(data.token);

    sessionStorage.setItem("auth.access_token", u.access_token);
    setUser(u);

    navigate(returnTo ?? afterLoginPath ?? "/");
  };

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated: !!user,
        user: user,
        userDisplayName: user?.displayName,
        token: user?.access_token,
        login,
        logout,
        loginWithPassword,

        afterLoginPath,
        afterLogoutPath,

        hasRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

function createUser(token: string) {
  const claims = jwtDecode<any>(token);
  console.log("claims", claims);
  return {
    displayName: claims.user,
    access_token: token,
  };
}
