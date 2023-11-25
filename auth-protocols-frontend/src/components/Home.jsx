import { Typography } from "@mui/material";
import useGlobalAuth from "../hooks/useGlobalAuth";
import { useEffect } from "react";
import api from "../api/api";

const Home = () => {
  const globalAuth = useGlobalAuth();

  useEffect(() => {
    const test = async () => {
      try {
        const response = await api.get("/api/private/example");
        console.log(response);
      } catch (error) {
        console.log(error);
      }
    };

    test();
  }, []);

  return (
    <Typography variant="h2" sx={{ paddingBottom: 4 }}>
      {globalAuth.isAuthenticated()
        ? `You are logged in with ${globalAuth.getProtocol()}.`
        : "Hello anonymous"}
    </Typography>
  );
};

export default Home;
