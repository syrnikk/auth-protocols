import axios from "axios";
import config from "../config/config";

const axiosInstance = axios.create({
  baseURL: config.API_URL,
  withCredentials: true,
});

axiosInstance.interceptors.request.use(
  (cfg) => {
    const key = `oidc.user:${config.OIDC_AUTHORITY}:${config.OIDC_CLIENT_ID}`
    const tokenData = JSON.parse(sessionStorage.getItem(key));
    if (tokenData !== null && tokenData !== undefined) {
        cfg.headers["Authorization"] = `Bearer ${tokenData.access_token}`;
    }
    return cfg;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default axiosInstance;