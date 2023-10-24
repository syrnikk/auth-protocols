import { AuthProvider } from "react-oidc-context";

const onSigninCallback = () => {
  window.history.replaceState({}, document.title, window.location.pathname);
};

const url = window.location.origin;

const oidcConfig = {
  authority: "http://auth-protocols-keycloak:8080/realms/auth-protocols",
  client_id: "auth-protocols-client-oidc",
  redirect_uri: url,
  onSigninCallback: onSigninCallback
};

const OidcProvider = (props) => {
  return <AuthProvider {...oidcConfig}>{props.children}</AuthProvider>;
};

export default OidcProvider;
