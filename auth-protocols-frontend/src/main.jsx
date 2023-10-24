import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.jsx";
import { GlobalAuthProvider } from "./auth/auth";

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <GlobalAuthProvider>
      <App />
    </GlobalAuthProvider>
  </React.StrictMode>
);
