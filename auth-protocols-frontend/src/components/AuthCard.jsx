import { Box } from "@mui/material";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import CardMedia from "@mui/material/CardMedia";
import Typography from "@mui/material/Typography";

const AuthCard = ({ imageSrc, text, onClick }) => {
  return (
    <Card
      onClick={onClick}
      style={{ cursor: "pointer" }}
      sx={{
        maxWidth: 260,
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        padding: 3,
        margin: "auto",
      }}
    >
      <Box>
        <CardMedia component="img" alt={imageSrc} image={imageSrc} />
      </Box>
      <CardContent>
        <Typography variant="h6" color="textSecondary">
          {text}
        </Typography>
      </CardContent>
    </Card>
  );
};

export default AuthCard;
