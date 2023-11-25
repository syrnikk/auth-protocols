const config = {
  API_URL: "http://localhost:5173",
  OIDC_AUTHORITY: "http://auth-protocols-keycloak:8080/realms/auth-protocols-oidc",
  OIDC_CLIENT_ID: "auth-protocols-client-oidc",
  OIDC_REDIRECT_URI: "http://localhost:5173",
  OIDC_POST_LOGOUT_REDIRECT_URI: "http://localhost:5173",
  SAML_AUTHENTICATION_REQUEST_URI:
    "http://auth-protocols-keycloak:8080/realms/auth-protocols-saml2/protocol/saml",
  SAML_LOGOUT_URI: "http://localhost:8081/api/logout",
  LDAP_LOGOUT_URI: "/api/logout",
};

export default config;
