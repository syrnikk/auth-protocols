import { useNavigate } from "react-router-dom";
import { useAuth } from "react-oidc-context";
import { MenuItem } from "@mui/material";
import Protocol from "../enums/Protocol";
import useGlobalAuth from "../hooks/useGlobalAuth";
import { ActionType, useGlobalDispatch } from "./GlobalProvider";
import api from "../api/api";

const Logout = () => {
  const navigate = useNavigate();
  const oidc = useAuth();
  const globalAuth = useGlobalAuth();
  const globalDispatch = useGlobalDispatch();

  const logout = async () => {
    if (globalAuth.getProtocol() === Protocol.OIDC) {
      oidc.removeUser();
      oidc.signoutRedirect();
    }
    if (globalAuth.getProtocol() === Protocol.SAML2) {
      try {
        await api.post("/api/saml2/logout");
      } catch (error) {
        console.log(error);
      }
    }
    globalDispatch({
      type: ActionType.LOGOUT,
    });

    navigate("/");
  };

  return (
    <>
      <MenuItem onClick={logout}>Log out</MenuItem>
    </>
  );
};

export default Logout;
