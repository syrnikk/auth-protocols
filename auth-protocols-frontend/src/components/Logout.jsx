import { useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "react-oidc-context";
import { MenuItem } from "@mui/material";
import config from "../config/config";
import Protocol from "../enums/Protocol";
import useGlobalAuth from "../hooks/useGlobalAuth";
import { ActionType, useGlobalDispatch } from "./GlobalProvider";

const Logout = () => {
  const samlLogoutFormRef = useRef();
  const navigate = useNavigate();
  const oidc = useAuth();
  const globalAuth = useGlobalAuth();
  const globalDispatch = useGlobalDispatch();

  const logout = () => {
    if (globalAuth.getProtocol() === Protocol.OIDC) {
      oidc.removeUser();
      oidc.signoutRedirect();
    }
    globalDispatch({
        type: ActionType.LOGOUT
    })

    navigate("/");
  };

  return (
    <>
      <MenuItem onClick={logout}>Log out</MenuItem>
      <form
        ref={samlLogoutFormRef}
        action={config.SAML_LOGOUT_URI}
        method="post"
      />
    </>
  );
};

export default Logout;
