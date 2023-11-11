import { useAuth } from "react-oidc-context";
import {
  Action,
  Protocol,
  useGlobalDispatch,
  useGlobalState,
} from "./GlobalProvider";
import { useRef, useState } from "react";
import { IconButton, Menu, MenuItem } from "@mui/material";
import { AccountCircle } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import config from "../config/config";

const UserProfileMenu = () => {
  const [anchorEl, setAnchorEl] = useState(null);
  const samlLogoutFormRef = useRef();

  const navigate = useNavigate();

  const globalState = useGlobalState();
  const globalDispatch = useGlobalDispatch();
  const oidc = useAuth();

  const handleMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const logout = () => {
    if (globalState.protocol === Protocol.OIDC) {
      oidc.removeUser();
      oidc.signoutRedirect();
    }

    if (globalState.protocol === Protocol.SAML2) {
      const form = samlLogoutFormRef.current;
      if (form) {
        form.submit();
      }
    }

    if (globalState.protocol === Protocol.LDAP) {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
    }

    globalDispatch({ type: Action.LOGOUT });
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
