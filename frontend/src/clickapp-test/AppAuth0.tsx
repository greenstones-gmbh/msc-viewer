import { AppRoutes } from "./AppRoutes";
import { Auth0Auth } from "../clickapp-oidc/Auth0Auth";

export function App() {
  return (
    <Auth0Auth
      domain={"dev-og2yprza28iiv7iu.us.auth0.com"}
      clientId="RpIReOwlUBgWcCY1OMNGPJky1rUW5Aso"
      afterLoginPath="/page1"
      afterLogoutPath="/page3"
    >
      <AppRoutes />
    </Auth0Auth>
  );
}

export default App;
