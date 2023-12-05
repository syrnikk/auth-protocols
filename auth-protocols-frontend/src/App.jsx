import { BrowserRouter, Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar";
import Home from "./components/Home";
import LoginWith from "./components/LoginWith";
import { useAuth } from "react-oidc-context";
import { useEffect, useState } from "react";
import Loading from "./components/Loading";
import Error from "./components/Error";
import Login from "./components/Login";
import Saml2ArtifactHandler from "./components/Saml2ArtifactHandler";
import { ActionType, useGlobalDispatch } from "./components/GlobalProvider";
import Account from "./components/Account";

function App() {
  const [isLoading, setIsLoading] = useState(false);
  const oidc = useAuth();
  const globalDispatch = useGlobalDispatch();

  useEffect(() => {
    globalDispatch({
      type: ActionType.REFRESH,
    });
  }, []);

  const showLoading = () => {
    setIsLoading(true);
  };

  switch (oidc.activeNavigator) {
    case "signinSilent":
      return <Loading title="Signing you in..." />;
    case "signoutRedirect":
      return <Loading title="Signing you out..." />;
  }

  if (isLoading || oidc.isLoading) {
    return <Loading title="Loading..." />;
  }

  if (oidc.error) {
    return <Error errorMessage={`Oops... ${oidc.error.message}`} />;
  }

  return (
    <>
      <BrowserRouter>
        <Navbar />
        <Routes>
          <Route index element={<Home />} />
          <Route
            path="/login-with"
            element={<LoginWith onShowLoading={showLoading} />}
          />
          <Route path="/login" element={<Login />} />
          <Route path="/account" element={<Account />} />
          <Route path="/saml2" element={<Saml2ArtifactHandler />} />
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;
