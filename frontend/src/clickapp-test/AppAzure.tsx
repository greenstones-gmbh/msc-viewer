import { AzureAuth } from "../clickapp-oidc/AzureAuth";
import { AppRoutes } from "./AppRoutes";

export function App() {
  return (
    <AzureAuth
      {...{
        clientId: "6e7b4d56-5b81-44e7-9d51-dfbe67df0617",
        //authority: "https://login.microsoftonline.com/common",
      }}
      afterLoginPath="/page1"
      afterLogoutPath="/page3"
    >
      <AppRoutes />
    </AzureAuth>
  );
}

export default App;
