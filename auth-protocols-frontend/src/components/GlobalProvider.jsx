import { createContext, useContext, useReducer } from "react";
import OidcProvider from "./OidcProvider";

const Protocol = {
  OIDC: "OIDC",
  SAML2: "SAML2",
  LDAP: "LDAP",
  KERBEROS: "KERBEROS",
};

const Action = {
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

const GlobalStateContext = createContext();
const GlobalDispatchContext = createContext();

function useGlobalState() {
  const context = useContext(GlobalStateContext);
  if (!context) throw new Error("useGlobalState must be used in GlobalProvider");
  return context;
}

function useGlobalDispatch() {
  const context = useContext(GlobalDispatchContext);
  if (!context) throw new Error("useGlobalDispatch must be used in GlobalProvider");
  return context;
}

function GlobalProvider(props) {
  const [state, dispatch] = useReducer(reducer, initialState);

  return (
    <GlobalStateContext.Provider value={state}>
      <GlobalDispatchContext.Provider value={dispatch}>
        <OidcProvider>{props.children}</OidcProvider>
      </GlobalDispatchContext.Provider>
    </GlobalStateContext.Provider>
  );
}

function reducer(state, action) {
  if(action.type === Action.LOGIN) {
    if(action.protocol === Protocol.OIDC) {
      return {...state, protocol: Protocol.OIDC, isAuthenticated: true}
    }
    if(action.protocol === Protocol.SAML2) {
      return {...state, protocol: Protocol.SAML2, isAuthenticated: true}
    }
    if(action.protocol === Protocol.LDAP) {
      return {...state, protocol: Protocol.LDAP, isAuthenticated: true, user: action.user }
    }
  }
  if(action.type === Action.LOGOUT) {
    return {...initialState};
  }
  return {...state}
}

export { GlobalProvider, useGlobalState, useGlobalDispatch, Action, Protocol };