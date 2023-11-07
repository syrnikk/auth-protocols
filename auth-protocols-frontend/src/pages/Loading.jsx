import CircularProgress from "@mui/material/CircularProgress";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";

const Loading = ({ title }) => {
  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      minHeight="100vh"
    >
      <Typography variant="h5" gutterBottom>
        {title}
      </Typography>
      <Box marginTop={3}>
        <CircularProgress color="primary" size={60} />
      </Box>
    </Box>
  );
};

export default Loading;
