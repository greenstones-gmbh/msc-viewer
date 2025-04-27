import "bootstrap/dist/css/bootstrap.min.css";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import "./index.css";
import { GenericApp } from "./msc/types/GenericApp.tsx";

createRoot(document.getElementById("root")!).render(
  <BrowserRouter basename="/msc-viewer">
    <GenericApp />
  </BrowserRouter>
);
