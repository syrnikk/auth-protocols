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

  const getUser = () => {
    if(oidc.isAuthenticated) {
      const profile = oidc.user.profile;
      return {
        username: profile.preferred_username,
        firstName: profile.given_name,
        lastName: profile.family_name,
        email: profile.email
      }
    }
    return globalState.user;
  }

  return {
    isAuthenticated: isAuthenticated,
    getProtocol: getProtocol,
    getUser: getUser,
  };
};

export default useGlobalAuth;
