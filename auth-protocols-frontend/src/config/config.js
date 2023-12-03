const config = {
  API_URL: import.meta.env.VITE_API_URL,
  OIDC_AUTHORITY: import.meta.env.VITE_OIDC_AUTHORITY,
  OIDC_CLIENT_ID: import.meta.env.VITE_OIDC_CLIENT_ID,
  OIDC_REDIRECT_URI: import.meta.env.VITE_OIDC_REDIRECT_URI,
  OIDC_POST_LOGOUT_REDIRECT_URI: import.meta.env.VITE_OIDC_POST_LOGOUT_REDIRECT_URI,
};

export default config;
