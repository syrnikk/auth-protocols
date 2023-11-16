import { createContext, useContext, useReducer } from "react";
import OidcProvider from "./OidcProvider";

const ActionType = {
  STATE: "STATE",
  LOGIN: "LOGIN",
  LOGOUT: "LOGOUT",
  REFRESH: "REFRESH"
};

const initialState = {};

const GlobalStateContext = createContext();
const GlobalDispatchContext = createContext();

function useGlobalState() {
  const context = useContext(GlobalStateContext);
  if (!context)
    throw new Error("useGlobalState must be used in GlobalProvider");
  return context;
}

function useGlobalDispatch() {
  const context = useContext(GlobalDispatchContext);
  if (!context)
    throw new Error("useGlobalDispatch must be used in GlobalProvider");
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
  switch (action.type) {
    case ActionType.STATE: {
      return { ...state, ...action.state };
    }
    case ActionType.LOGIN: {
      localStorage.setItem("accessToken", action.accessToken);
      localStorage.setItem("refreshToken", action.refreshToken);
      localStorage.setItem("protocol", action.protocol);
      localStorage.setItem("user", JSON.stringify(action.user));
      return { ...state, user: action.user, protocol: action.protocol };
    }
    case ActionType.LOGOUT: {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      localStorage.removeItem("protocol");
      localStorage.removeItem("user");
      return { ...initialState };
    }
    case ActionType.REFRESH: {
      const protocol = localStorage.getItem("protocol");
      const user = JSON.parse(localStorage.getItem("user"));
      return { ...initialState, user, protocol };
    }
    default: {
      throw Error("Unknown action: " + action.type);
    }
  }
}

export { GlobalProvider, useGlobalState, useGlobalDispatch, ActionType };
