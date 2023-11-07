const config = {
  API_URL: "http://localhost:5173",
  OIDC_AUTHORITY: "http://auth-protocols-keycloak:8080/realms/auth-protocols-oidc",
  OIDC_CLIENT_ID: "auth-protocols-client-oidc",
  OIDC_REDIRECT_URI: "http://localhost:5173",
  OIDC_POST_LOGOUT_REDIRECT_URI: "http://localhost:5173",
  SAML_AUTHENTICATION_REQUEST_URI:
    "http://localhost:8081/saml2/authenticate/keycloak",
  LOGOUT_URI: "http://localhost:8081/logout",
};

export default config;
