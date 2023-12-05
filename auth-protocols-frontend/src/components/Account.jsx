import { Container, Box, Typography, Avatar, Grid } from "@mui/material";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import useGlobalAuth from "../hooks/useGlobalAuth";

const Account = () => {
  const globalAuth = useGlobalAuth();
  const user = globalAuth.getUser();

  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          mt: 7,
        }}
      >
        <Avatar sx={{ m: 1, bgcolor: "primary.main", width: 200, height: 200 }}>
          <AccountCircleIcon sx={{ fontSize: 160 }} />
        </Avatar>
        <Typography component="h1" variant="h5" sx={{ mb: 2, mt: 3 }}>
          Account Information
        </Typography>
        <Grid container spacing={2} alignItems="center" sx={{paddingX: 10}}>
          <Grid item xs={5} sx={{ textAlign: "right" }}>
            <Typography variant="body1">Login:</Typography>
          </Grid>
          <Grid item xs={7}>
            <Typography variant="body1">{user.username}</Typography>
          </Grid>
          <Grid item xs={5} sx={{ textAlign: "right" }}>
            <Typography variant="body1">First Name:</Typography>
          </Grid>
          <Grid item xs={7}>
            <Typography variant="body1">{user.firstName}</Typography>
          </Grid>
          <Grid item xs={5} sx={{ textAlign: "right" }}>
            <Typography variant="body1">Last Name:</Typography>
          </Grid>
          <Grid item xs={7}>
            <Typography variant="body1">{user.lastName}</Typography>
          </Grid>
          <Grid item xs={5} sx={{ textAlign: "right" }}>
            <Typography variant="body1">Email:</Typography>
          </Grid>
          <Grid item xs={7}>
            <Typography variant="body1">{user.email}</Typography>
          </Grid>
          <Grid item xs={5} sx={{ textAlign: "right" }}>
            <Typography variant="body1">Authentication Protocol:</Typography>
          </Grid>
          <Grid item xs={7}>
            <Typography variant="body1">
              {globalAuth.getProtocol()}
            </Typography>
          </Grid>
        </Grid>
      </Box>
    </Container>
  );
};

export default Account;