import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import Link from "@mui/material/Link";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import api from "../api/api";
import { useNavigate } from "react-router-dom";
import { decodeToken } from "react-jwt";
import Protocol from "../enums/Protocol";
import { ActionType, useGlobalDispatch } from "./GlobalProvider";

const Login = () => {
  const navigate = useNavigate();
  const globalDispatch = useGlobalDispatch();

  const handleSubmit = async (event) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    try {
      const response = await api.post("/api/login", data);
      const { accessToken, refreshToken } = response.data;
      const decodedToken = decodeToken(accessToken);
      globalDispatch({
        type: ActionType.LOGIN,
        accessToken: accessToken,
        refreshToken: refreshToken,
        protocol: Protocol.LDAP,
        user:{
          username: decodedToken.sub,
          authorities: decodedToken.authorities
        }
      })
    } catch (error) {
      console.log(error);
    }
    navigate("/");
  };

  return (
    <Container component="main" maxWidth="xs">
      <Box
        sx={{
          marginTop: 8,
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
        }}
      >
        <Typography component="h1" variant="h5">
          Sign in
        </Typography>
        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
          <TextField
            margin="normal"
            required
            fullWidth
            id="username"
            label="Username"
            name="username"
            autoComplete="username"
            autoFocus
          />
          <TextField
            margin="normal"
            required
            fullWidth
            name="password"
            label="Password"
            type="password"
            id="password"
            autoComplete="current-password"
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
          >
            Sign In
          </Button>
          <Grid container>
            <Grid item xs>
              <Link href="#" variant="body2">
                Forgot password?
              </Link>
            </Grid>
            <Grid item>
              <Link href="#" variant="body2">
                {"Don't have an account? Sign Up"}
              </Link>
            </Grid>
          </Grid>
        </Box>
      </Box>
    </Container>
  );
};

export default Login;
