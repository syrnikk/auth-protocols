import { useAuth } from "react-oidc-context";
import { isTokenExpired } from "../utils/jwt";
import Protocol from "../enums/Protocol";
import { useGlobalState } from "../components/GlobalProvider";

const useGlobalAuth = () => {
  const oidc = useAuth();
  const globalState = useGlobalState();

  const isAuthenticated = () => {
    if (oidc.isAuthenticated) {
      return true;
    }
    const refreshToken = localStorage.getItem("refreshToken");
    return refreshToken != null && !isTokenExpired(refreshToken);
  };

  const getProtocol = () => {
    if(oidc.isAuthenticated) {
        return Protocol.OIDC
    }
    return globalState.protocol;
  }

  return {
    isAuthenticated: isAuthenticated,
    getProtocol: getProtocol,
  };
};

export default useGlobalAuth;
