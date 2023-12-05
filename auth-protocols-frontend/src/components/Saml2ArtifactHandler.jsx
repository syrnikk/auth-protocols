import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Loading from "./Loading";
import api from "../api/api";
import useRawQueryParam from "../hooks/useRawQueryParam";
import { decodeToken } from "react-jwt";
import Protocol from "../enums/Protocol";
import { ActionType, useGlobalDispatch } from "./GlobalProvider";

const Saml2ArtifactHandler = () => {
  const navigate = useNavigate();
  const samlArt = useRawQueryParam("samlArt");
  const globalDispatch = useGlobalDispatch();

  useEffect(() => {
    const authenticateWithSaml2 = async () => {
      try {
        if (samlArt) {
          const response = await api.post("/api/saml2/authenticate", {
            samlArt: samlArt,
          });
          const { accessToken, refreshToken } = response.data;
          const decodedToken = decodeToken(accessToken);
          globalDispatch({
            type: ActionType.LOGIN,
            accessToken: accessToken,
            refreshToken: refreshToken,
            protocol: Protocol.SAML2,
            user:{
              username: decodedToken.sub,
              email: decodedToken.email,
              firstName: decodedToken.first_name,
              lastName: decodedToken.last_name,
              authorities: decodedToken.authorities
            }
          })
        }
      } catch (error) {
        console.log(error);
      }
      navigate("/");
    };

    authenticateWithSaml2();
  }, [samlArt]);

  return <Loading title="Loading..." />;
};

export default Saml2ArtifactHandler;
