import axios from "axios";
import config from "../config/config";
import { decodeToken } from "react-jwt";

const getAccessToken = () => {
  const key = `oidc.user:${config.OIDC_AUTHORITY}:${config.OIDC_CLIENT_ID}`;
  const tokenData = JSON.parse(sessionStorage.getItem(key));
  if (tokenData !== null && tokenData !== undefined) {
    return tokenData.access_token;
  } else {
    return localStorage.getItem("accessToken");
  }
};

const getRefreshToken = () => {
  return localStorage.getItem("refreshToken");
};

const setAccessToken = (accessToken) => {
  localStorage.setItem("accessToken", accessToken);
};

const setRefreshToken = (refreshToken) => {
  localStorage.setItem("refreshToken", refreshToken);
};

const isTokenExpired = (token) => {
  try {
    const decodedToken = decodeToken(token);
    return decodedToken.exp * 1000 <= Date.now();
  } catch (error) {
    console.error("Error decoding access token:", error);
    return true;
  }
};

const refreshToken = async () => {
  try {
    const response = await axios.post(
      `${config.API_URL}/api/auth/refresh-token`,
      {
        refreshToken: getRefreshToken(),
      }
    );
    const { accessToken, refreshToken } = response.data;
    setAccessToken(accessToken);
    setRefreshToken(refreshToken);
    return accessToken;
  } catch (error) {
    console.error("Error refreshing access token:", error);
    throw error;
  }
};

export {
  getAccessToken,
  getRefreshToken,
  setAccessToken,
  setRefreshToken,
  refreshToken,
  isTokenExpired,
};
