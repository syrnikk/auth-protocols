import { Box, Container, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import useGlobalAuth from '../hooks/useGlobalAuth';

const Home = () => {
  const globalAuth = useGlobalAuth();

  const navigate = useNavigate();

  const handleSignIn = () => {
    navigate('/login-with');
  };

  const isAuthenticated = globalAuth.isAuthenticated();
  const user = globalAuth.getUser();

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
        {isAuthenticated ? (
          <Typography variant="h6">
            Hi <span style={{ fontWeight: 'bold' }}>{user.username}</span>! We're glad you are with us! :)
          </Typography>
        ) : (
          <>
            <Typography variant="h6">
              Please sign in
            </Typography>
            <Button 
              variant="contained" 
              color="primary" 
              onClick={handleSignIn}
              sx={{ mt: 2 }}
            >
              Sign In
            </Button>
          </>
        )}
      </Box>
    </Container>
  );
};

export default Home;
