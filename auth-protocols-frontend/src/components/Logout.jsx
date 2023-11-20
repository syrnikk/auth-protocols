import { useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "react-oidc-context";
import { MenuItem } from "@mui/material";
import config from "../config/config";
import Protocol from "../enums/Protocol";
import useGlobalAuth from "../hooks/useGlobalAuth";
import { ActionType, useGlobalDispatch } from "./GlobalProvider";
import api from "../api/api";

const Logout = () => {
  const samlLogoutFormRef = useRef();
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
      const response = await api.post("/api/saml2/logout");
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
