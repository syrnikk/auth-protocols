import { BrowserRouter, Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar";
import Home from "./components/Home";
import LoginWith from "./components/LoginWith";
import { useAuth } from "react-oidc-context";
import { Action, Protocol, useGlobalDispatch } from "./components/GlobalProvider";
import { useEffect, useState } from "react";
import Loading from "./components/Loading";
import Error from "./components/Error";
import Login from "./components/Login";
import { decodeToken } from "react-jwt";
import api from "./api/api";

function App() {
  const [isLoading, setIsLoading] = useState(false);
  const globalDispatch = useGlobalDispatch();
  const oidc = useAuth();

  useEffect(() => {
    if (oidc.isAuthenticated) {
      globalDispatch({ type: Action.LOGIN, protocol: Protocol.OIDC });
    }
  }, [oidc.isAuthenticated]);

  useEffect(() => {
    const checkAccessToken = () => {
      const accessToken = localStorage.getItem("accessToken");
      if (accessToken) {
        const decodedToken = decodeToken(accessToken);
        globalDispatch({
          type: Action.LOGIN,
          protocol: decodedToken.protocol,
          user: decodedToken.sub,
        });
      }
    };

    checkAccessToken();
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
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;
