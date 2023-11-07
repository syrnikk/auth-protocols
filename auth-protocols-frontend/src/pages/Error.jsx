import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

const Error = ({ errorMessage }) => {
  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      minHeight="100vh"
    >
      <Typography variant="h5" color="error" gutterBottom>
        {errorMessage}
      </Typography>
    </Box>
  );
};

export default Error;