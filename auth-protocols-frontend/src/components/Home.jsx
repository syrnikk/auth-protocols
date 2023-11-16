import { Typography } from "@mui/material";
import useGlobalAuth from "../hooks/useGlobalAuth";

const Home = () => {
  const globalAuth = useGlobalAuth();

  return (
    <Typography variant="h2" sx={{ paddingBottom: 4 }}>
      {globalAuth.isAuthenticated()
        ? `You are logged in with ${globalAuth.getProtocol()}.`
        : "Hello anonymous"}
      
    </Typography>
  );
};

export default Home;
