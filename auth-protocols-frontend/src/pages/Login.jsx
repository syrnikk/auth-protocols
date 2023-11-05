import AuthCard from "../components/AuthCard";
import oidcLogo from "/oidc-logo.png";
import samlLogo from "/saml-logo.png";
import ldapLogo from "/ldap-logo.png";
import kerberosLogo from "/kerberos-logo.png";
import { Grid, Box, Typography } from "@mui/material";
import { useAuth } from "react-oidc-context";
import config from "../config/config";
import { useRef } from "react";

const Login = () => {
  const oidc = useAuth();
  const samlLoginFormRef = useRef();

  const oidcLogin = () => {
    oidc.signinRedirect();
  };

  const samlLogin = () => {
    const form = samlLoginFormRef.current;
    if (form) {
      form.submit();
    }
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
          <form
            ref={samlLoginFormRef}
            action={config.SAML_AUTHENTICATION_REQUEST_URI}
            method="post"
          />
        </Grid>
        <Grid item>
          <AuthCard imageSrc={ldapLogo} text="LDAP" />
        </Grid>
        <Grid item>
          <AuthCard imageSrc={kerberosLogo} text="Kerberos" />
        </Grid>
      </Grid>
    </Box>
  );
};

export default Login;
