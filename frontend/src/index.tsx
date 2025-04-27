import "bootstrap/dist/css/bootstrap.min.css";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
//import App from "./clickapp-test/AppAzure";
//import App from "./clickapp-test/AppOidcAuth";
//import App from "./clickapp-test/AppSupabaseAuth";
//import App from "./clickapp-test/AppAuth0";
//import App from "./clickapp-keycloak/AppKeycloak";

import "./index.css";
import { GenericApp } from "./msc/types/GenericApp";

//import { CustomApp } from "./custom/CustomApp";

const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement
);
root.render(
  // <React.StrictMode>
  <BrowserRouter basename="/msc-viewer">
    <GenericApp />
  </BrowserRouter>
  // </React.StrictMode>
);
