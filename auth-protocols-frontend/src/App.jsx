import { BrowserRouter, Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import LoginWith from "./pages/LoginWith";
import { useAuth } from "react-oidc-context";
import { AuthAction, AuthProtocol, useAuthDispatch } from "./auth/auth";
import { useEffect, useState } from "react";
import axiosInstance from "./axios/axiosInstance";
import Loading from "./pages/Loading";
import Error from "./pages/Error";
import Login from "./pages/Login";

function App() {
  const [isLoading, setIsLoading] = useState(false);
  const authDispatch = useAuthDispatch();
  const oidc = useAuth();

  useEffect(() => {
    if (oidc.isAuthenticated) {
      authDispatch({ type: AuthAction.LOGIN, protocol: AuthProtocol.OIDC });
    }
  }, [oidc.isAuthenticated]);

  useEffect(() => {
    async function fetchData() {
      try {
        const response = await axiosInstance.get("/api/auth/me");
        authDispatch({
          type: AuthAction.LOGIN,
          protocol: response.data.protocol,
        });
      } catch (error) {
        console.log(error);
      }
    }

    fetchData();
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
