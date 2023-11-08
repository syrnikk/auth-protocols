import { createContext, useContext, useReducer } from "react";
import OidcProvider from "./oidc";

const AuthProtocol = {
  OIDC: "OIDC",
  SAML2: "SAML2",
  LDAP: "LDAP",
  KERBEROS: "KERBEROS",
};

const AuthAction = {
  LOGIN: "LOGIN",
  LOGOUT: "LOGOUT"
}

const initialState = {
  protocol: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,
  user: null,
};

const AuthStateContext = createContext();
const AuthDispatchContext = createContext();

function useAuthState() {
  const context = useContext(AuthStateContext);
  if (!context) throw new Error("useAuthState must be used in AuthProvider");
  return context;
}

function useAuthDispatch() {
  const context = useContext(AuthDispatchContext);
  if (!context) throw new Error("useAuthDispatch must be used in AuthProvider");
  return context;
}

function GlobalAuthProvider(props) {
  const [state, dispatch] = useReducer(reducer, initialState);

  return (
    <AuthStateContext.Provider value={state}>
      <AuthDispatchContext.Provider value={dispatch}>
        <OidcProvider>{props.children}</OidcProvider>
      </AuthDispatchContext.Provider>
    </AuthStateContext.Provider>
  );
}

function reducer(state, action) {
  if(action.type === AuthAction.LOGIN) {
    if(action.protocol === AuthProtocol.OIDC) {
      return {...state, protocol: AuthProtocol.OIDC, isAuthenticated: true}
    }
    if(action.protocol === AuthProtocol.SAML2) {
      return {...state, protocol: AuthProtocol.SAML2, isAuthenticated: true}
    }
    if(action.protocol === AuthProtocol.LDAP) {
      return {...state, protocol: AuthProtocol.LDAP, isAuthenticated: true}
    }
  }
  if(action.type === AuthAction.LOGOUT) {
    return {...initialState};
  }
  return {...state}
}

export { GlobalAuthProvider, useAuthState, useAuthDispatch, AuthAction, AuthProtocol };