import { BrowserRouter, Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import Login from "./pages/Login";
import { useAuth } from "react-oidc-context";
import { AuthAction, AuthProtocol, useAuthDispatch } from "./auth/auth";
import { useEffect } from "react";

function App() {
  const authDispatch = useAuthDispatch();
  const oidc = useAuth();

  useEffect(() => {
    if (oidc.isAuthenticated) {
      authDispatch({ type: AuthAction.LOGIN, protocol: AuthProtocol.OIDC });
    }
  }, [oidc.isAuthenticated]);

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
