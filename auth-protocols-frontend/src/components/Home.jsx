import { Box, Container, Typography } from "@mui/material";

const Home = () => {
  return (
    <Container maxWidth="m">
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          height: "80vh",
        }}
      >
        <Typography variant="h2" component="h1" gutterBottom>
          Welcome to Auth Protocols
        </Typography>
      </Box>
    </Container>
  );
};

export default Home;
