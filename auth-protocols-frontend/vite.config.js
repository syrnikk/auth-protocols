import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: 'frontend.auth-protocols.com',
    port: 80,
    proxy: {
      "/api": {
        target: "http://backend.auth-protocols.com:3000",
        changeOrigin: true,
        secure: false,
        ws: true,
      },
    },
  },
});
