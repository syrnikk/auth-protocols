import { useAuth } from "react-oidc-context";
import {
  AuthAction,
  AuthProtocol,
  useAuthDispatch,
  useAuthState,
} from "../auth/auth";
import { useRef, useState } from "react";
import { IconButton, Menu, MenuItem } from "@mui/material";
import { AccountCircle } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import config from "../config/config";
import axiosInstance from "../axios/axiosInstance";

const UserProfileMenu = () => {
  const [anchorEl, setAnchorEl] = useState(null);
  const samlLogoutFormRef = useRef();

  const navigate = useNavigate();

  const authState = useAuthState();
  const authDispatch = useAuthDispatch();
  const oidc = useAuth();

  const handleMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const logout = () => {
    if (authState.protocol === AuthProtocol.OIDC) {
      oidc.removeUser();
      oidc.signoutRedirect();
    }

    if (authState.protocol === AuthProtocol.SAML2) {
      const form = samlLogoutFormRef.current;
      if (form) {
        form.submit();
      }
    }

    if (authState.protocol === AuthProtocol.LDAP) {
      axiosInstance.post(config.LDAP_LOGOUT_URI);
    }

    authDispatch({ type: AuthAction.LOGOUT });
    navigate("/");
  };

  return (
    <div>
      <IconButton
        size="large"
        aria-label="account of current user"
        aria-controls="menu-appbar"
        aria-haspopup="true"
        onClick={handleMenu}
        color="inherit"
      >
        <AccountCircle />
      </IconButton>
      <Menu
        id="menu-appbar"
        anchorEl={anchorEl}
        anchorOrigin={{
          vertical: "top",
          horizontal: "right",
        }}
        keepMounted
        transformOrigin={{
          vertical: "top",
          horizontal: "right",
        }}
        open={Boolean(anchorEl)}
        onClose={handleClose}
      >
        <MenuItem onClick={handleClose}>Profile</MenuItem>
        <MenuItem onClick={handleClose}>My account</MenuItem>
        <MenuItem onClick={logout}>Log out</MenuItem>
        <form
          ref={samlLogoutFormRef}
          action={config.SAML_LOGOUT_URI}
          method="post"
        />
      </Menu>
    </div>
  );
};

export default UserProfileMenu;
