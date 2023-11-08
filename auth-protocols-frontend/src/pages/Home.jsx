import { Typography } from "@mui/material";
import { useAuthState } from "../auth/auth";

const Home = () => {
  const authState = useAuthState();

  return (
    <Typography variant="h2" sx={{ paddingBottom: 4 }}>
      {authState.isAuthenticated
        ? `You are logged in with ${authState.protocol}.`
        : "Hello anonymous"}
    </Typography>
  );
};

export default Home;
