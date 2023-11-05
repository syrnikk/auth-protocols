import { BrowserRouter, Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import Login from "./pages/Login";
import { useAuth } from "react-oidc-context";
import { AuthAction, AuthProtocol, useAuthDispatch } from "./auth/auth";
import { useEffect } from "react";
import axiosInstance from "./axios/axiosInstance";

function App() {
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

  switch (oidc.activeNavigator) {
    case "signinSilent":
      return <div>Signing you in...</div>;
    case "signoutRedirect":
      return <div>Signing you out...</div>;
  }

  if (oidc.isLoading) {
    return <div>Loading...</div>;
  }

  if (oidc.error) {
    return <div>Oops... {oidc.error.message}</div>;
  }

  return (
    <>
      <BrowserRouter>
        <Navbar />
        <Routes>
          <Route index element={<Home />} />
          <Route path="/login" element={<Login />} />
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;
