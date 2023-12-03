import AuthCard from "./AuthCard";
import oidcLogo from "/oidc-logo.png";
import samlLogo from "/saml-logo.png";
import ldapLogo from "/ldap-logo.png";
import kerberosLogo from "/kerberos-logo.png";
import { Grid, Box, Typography } from "@mui/material";
import { useAuth } from "react-oidc-context";
import { useRef } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import { decodeToken } from "react-jwt";
import { ActionType, useGlobalDispatch } from "./GlobalProvider";
import Protocol from "../enums/Protocol";

const LoginWith = ({ onShowLoading }) => {
  const oidc = useAuth();
  const samlLoginFormRef = useRef();
  const navigate = useNavigate();
  const globalDispatch = useGlobalDispatch();

  const oidcLogin = () => {
    oidc.signinRedirect();
  };

  const samlLogin = async () => {
    const response = await api.get("/api/saml2/create");
    const form = samlLoginFormRef.current;
    if (form) {
      form.action = response.data.samlIdpService;
      form.querySelector('input[name="SAMLRequest"]').value =
        response.data.authnRequestEncoded;
      form.submit();
      onShowLoading();
    }
  };

  const ldapLogin = () => {
    navigate("/login");
  };

  const kerberosLogin = async () => {
    try {
      const response = await api.post("/api/kerberos/authenticate");
      const { accessToken, refreshToken } = response.data;
      const decodedToken = decodeToken(accessToken);
      globalDispatch({
        type: ActionType.LOGIN,
        accessToken: accessToken,
        refreshToken: refreshToken,
        protocol: Protocol.KERBEROS,
        user: {
          username: decodedToken.sub,
          authorities: decodedToken.authorities,
        },
      });
    } catch (error) {
      console.log(error);
    }
    navigate("/");
  };

  return (
    <Box
      sx={{
        padding: 5,
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <Typography variant="h3" sx={{ paddingBottom: 4 }}>
        Login with:
      </Typography>
      <Grid
        container
        justifyContent="center"
        alignItems="center"
        spacing={2}
        maxWidth={720}
      >
        <Grid item>
          <AuthCard
            imageSrc={oidcLogo}
            text="Open ID Connect"
            onClick={oidcLogin}
          />
        </Grid>
        <Grid item>
          <AuthCard imageSrc={samlLogo} text="SAML 2.0" onClick={samlLogin} />
          <form ref={samlLoginFormRef} method="post">
            <input type="hidden" name="SAMLRequest" />
          </form>
        </Grid>
        <Grid item>
          <AuthCard imageSrc={ldapLogo} text="LDAP" onClick={ldapLogin} />
        </Grid>
        <Grid item>
          <AuthCard
            imageSrc={kerberosLogo}
            text="Kerberos"
            onClick={kerberosLogin}
          />
        </Grid>
      </Grid>
    </Box>
  );
};

export default LoginWith;
