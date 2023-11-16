import axios from "axios";
import config from "../config/config";
import { getAccessToken, isTokenExpired, refreshToken } from "../utils/jwt";

const api = axios.create({
  baseURL: config.API_URL,
});

api.interceptors.request.use(
  async (cfg) => {
    let accessToken = getAccessToken();
    if(accessToken === null || accessToken === undefined) {
      return cfg;
    }

    if(isTokenExpired(accessToken)) {
      accessToken = await refreshToken()
    }

    cfg.headers.Authorization = `Bearer ${accessToken}`;
    
    return cfg;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api;
