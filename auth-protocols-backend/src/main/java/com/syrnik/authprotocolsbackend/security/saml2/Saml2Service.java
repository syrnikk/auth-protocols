package com.syrnik.authprotocolsbackend.security.saml2;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Base64;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.saml2.core.ArtifactResolve;
import org.opensaml.saml.saml2.core.ArtifactResponse;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.soap.client.http.HttpSOAPClient;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.syrnik.authprotocolsbackend.dto.AssertionData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Saml2Service {

    @Value("${app.saml2.metadata-url}")
    private String saml2MetadataUrl;

    @Value("${app.saml2.acs-url}")
    private String assertionConsumerServiceUrl;

    @Value("${app.saml2.ars-url}")
    private String artifactResolutionServiceUrl;

    @Value("${app.saml2.slo-url}")
    private String singleLogoutServiceUrl;

    @Value("${app.saml2.destination}")
    private String destination;

    @Value("${app.saml2.issuer-id}")
    private String issuerId;

    @Value("${app.saml2.idp-issuer}")
    private String idpIssuer;

    public String createAuthnRequestEncoded() throws Exception {
        AuthnRequest authnRequest = Saml2Util.buildAuthnRequest(assertionConsumerServiceUrl, destination, issuerId);
        String stringAuthnRequest = Saml2Util.xmlObjectToString(authnRequest);
        return Base64.getEncoder().encodeToString(stringAuthnRequest.getBytes(StandardCharsets.UTF_8));
    }

    public LogoutRequest createLogoutRequest(String sessionIndex, String nameID) throws Exception {
        return Saml2Util.buildLogoutRequest(sessionIndex, nameID, destination, issuerId);
    }

    public AssertionData getAssertionData(String samlArt) throws Exception {
        ArtifactResolve artifactResolve = Saml2Util.buildArtifactResolve(samlArt, issuerId);
        ArtifactResponse artifactResponse = sendArtifactResolve(artifactResolve);

        if(!StatusCode.SUCCESS.equals(artifactResponse.getStatus().getStatusCode().getValue())) {
            throw new RuntimeException("SAML2 Artifact Response Error");
        }

        Response response = (Response) artifactResponse.getMessage();
        if(StatusCode.SUCCESS.equals(response.getStatus().getStatusCode().getValue())) {
            if(response.getAssertions() != null && !response.getAssertions().isEmpty()) {
                Assertion assertion = response.getAssertions().get(0);
                validateAssertion(assertion);
                return Saml2Util.mapAssertionData(assertion);
            }
        }

        throw new RuntimeException("SAML2 Assertion Error");
    }

    private void validateAssertion(Assertion assertion) throws Exception {
        String samlMetadata = getSamlMetadata(saml2MetadataUrl);
        X509Certificate idpCertificate = Saml2Util.getCertificateFromSamlMetadata(samlMetadata);

        if(assertion.isSigned()) {
            Saml2Util.validateSignature(assertion, idpCertificate);
        } else {
            throw new SecurityException("Assertion is not signed");
        }

        if(!assertion.getIssuer().getValue().equals(idpIssuer)) {
            throw new SecurityException("Invalid Issuer");
        }
    }

    public void sendLogoutRequest(LogoutRequest logoutRequest) throws Exception {
        LogoutResponse logoutResponse = (LogoutResponse) sendRequest(singleLogoutServiceUrl, logoutRequest);
        if(!StatusCode.SUCCESS.equals(logoutResponse.getStatus().getStatusCode().getValue())) {
            throw new RuntimeException("SAML2 Logout Response Error");
        }
    }

    private ArtifactResponse sendArtifactResolve(ArtifactResolve artifactResolve) throws Exception {
        return (ArtifactResponse) sendRequest(artifactResolutionServiceUrl, artifactResolve);
    }

    private XMLObject sendRequest(String endpoint, RequestAbstractType requestAbstractType) throws Exception {
        HttpSOAPClient soapClient = Saml2Util.buildSoapClient();

        Body body = (Body) XMLObjectSupport.buildXMLObject(Body.DEFAULT_ELEMENT_NAME);
        body.getUnknownXMLObjects().add(requestAbstractType);
        Envelope envelope = (Envelope) XMLObjectSupport.buildXMLObject(Envelope.DEFAULT_ELEMENT_NAME);
        envelope.setBody(body);

        SOAP11Context soap11Context = new SOAP11Context();
        soap11Context.setEnvelope(envelope);

        MessageContext outboundMessageContext = new MessageContext();
        outboundMessageContext.setMessage(requestAbstractType);
        outboundMessageContext.addSubcontext(soap11Context);
        InOutOperationContext context = new InOutOperationContext(null, outboundMessageContext);

        SecurityParametersContext securityParametersContext = outboundMessageContext.getSubcontext(
              SecurityParametersContext.class, true);
        securityParametersContext.setSignatureSigningParameters(Saml2Util.buildSignatureSigningParameters());

        soapClient.send(endpoint, context);

        return context
              .getInboundMessageContext()
              .getSubcontext(SOAP11Context.class)
              .getEnvelope()
              .getBody()
              .getUnknownXMLObjects()
              .getFirst();
    }

    private String getSamlMetadata(String saml2MetadataUrl) {
        try(HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(saml2MetadataUrl)).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch(Exception e) {
            log.error("Problem getting auth server certificate: {}", e.getMessage());
        }
        return null;
    }
}
