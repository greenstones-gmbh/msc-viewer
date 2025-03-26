import { PublicClientApplication } from "@azure/msal-browser";
import { MsalProvider, useIsAuthenticated, useMsal } from "@azure/msal-react";
import { AuthContext, BaseAuthProps, useRoles } from "@clickapp/qui-core";
import { PropsWithChildren, useMemo } from "react";

// export const msalConfig = {
//   auth: {
//     clientId: "6e7b4d56-5b81-44e7-9d51-dfbe67df0617",
//     authority: "https://login.microsoftonline.com/common",
//     redirectUri: "http://localhost:3000/",
//   },
//   cache: {
//     cacheLocation: "sessionStorage", // This configures where your cache will be stored
//     storeAuthStateInCookie: false, // Set this to "true" if you are having issues on IE11 or Edge
//   },
//   system: {
//     loggerOptions: {
//       loggerCallback: (level: any, message: any, containsPii: any) => {
//         if (containsPii) {
//           return;
//         }
//         switch (level) {
//           case LogLevel.Error:
//             console.error(message);
//             return;
//           case LogLevel.Info:
//             console.info(message);
//             return;
//           case LogLevel.Verbose:
//             console.debug(message);
//             return;
//           case LogLevel.Warning:
//             console.warn(message);
//             return;
//           default:
//             return;
//         }
//       },
//     },
//   },
// };

//const msalInstance = new PublicClientApplication(msalConfig);

export interface AzureAuthProps extends BaseAuthProps {
  authority?: string;
  clientId: string;
}

export function AzureAuth({
  authority,
  clientId,
  children,
  afterLoginPath,
  afterLogoutPath,
}: PropsWithChildren<AzureAuthProps>) {
  const msalInstance = useMemo(
    () =>
      new PublicClientApplication({
        auth: {
          clientId,
          authority,
          redirectUri: window.location.origin,
        },
        cache: {
          cacheLocation: "sessionStorage", // This configures where your cache will be stored
          storeAuthStateInCookie: false, // Set this to "true" if you are having issues on IE11 or Edge
        },
      }),
    [authority, clientId]
  );

  // console.log("2!!!!!!", msalInstance);
  // if (!msalInstance) return null;

  return (
    <MsalProvider instance={msalInstance}>
      <MsalConnector
        afterLogoutPath={afterLogoutPath}
        afterLoginPath={afterLoginPath}
      >
        {children}
      </MsalConnector>
    </MsalProvider>
  );
}
function MsalConnector({
  children,
  afterLoginPath,
  afterLogoutPath,
  roleMapper,
}: PropsWithChildren<BaseAuthProps>) {
  const { instance, accounts, inProgress } = useMsal();

  const isAuthenticated = useIsAuthenticated();
  const account = accounts && accounts.length > 0 ? accounts[0] : undefined;

  const { hasRole } = useRoles(account, roleMapper);

  const login = async (returnTo?: string) => {
    const path = returnTo ?? afterLoginPath;
    const redirect_uri = path ? `${window.location.origin}${path}` : undefined;
    const params = redirect_uri ? { redirect_uri } : undefined;

    instance
      .loginRedirect({
        scopes: ["User.Read"],
      })
      .catch((e) => {
        console.log(e);
      });

    return;
  };

  const logout = async (returnTo?: string) => {
    const path = returnTo ?? afterLogoutPath;
    const uri = path ? `${window.location.origin}${path}` : undefined;
    const params = uri ? { returnTo: uri } : undefined;
    return await instance.logout(/*{ logoutParams: params }*/);
  };

  console.log("accounts", accounts);
  console.log("account", account);
  console.log("inProgress", inProgress);

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated,
        user: account,
        userDisplayName: account?.name,
        token: undefined,
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
