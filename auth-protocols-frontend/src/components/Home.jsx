import { Typography } from "@mui/material";
import { useGlobalState } from "./GlobalProvider";

const Home = () => {
  const globalState = useGlobalState();

  return (
    <Typography variant="h2" sx={{ paddingBottom: 4 }}>
      {globalState.isAuthenticated
        ? `You are logged in with ${globalState.protocol}.`
        : "Hello anonymous"}
    </Typography>
  );
};

export default Home;
