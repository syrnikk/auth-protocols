import { AuthProvider } from "react-oidc-context";
import config from "../config/config";

const onSigninCallback = () => {
  window.history.replaceState({}, document.title, window.location.pathname);
};

const url = window.location.origin;

const oidcConfig = {
  authority: config.OIDC_AUTHORITY,
  client_id: config.OIDC_CLIENT_ID,
  redirect_uri: config.OIDC_REDIRECT_URI || url,
  post_logout_redirect_uri: config.OIDC_POST_LOGOUT_REDIRECT_URI || url,
  onSigninCallback: onSigninCallback
};

const OidcProvider = (props) => {
  return <AuthProvider {...oidcConfig}>{props.children}</AuthProvider>;
};

export default OidcProvider;
