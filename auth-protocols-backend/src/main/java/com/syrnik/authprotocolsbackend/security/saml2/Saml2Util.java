package com.syrnik.authprotocolsbackend.security.saml2;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Artifact;
import org.opensaml.saml.saml2.core.ArtifactResolve;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.impl.ArtifactBuilder;
import org.opensaml.saml.saml2.core.impl.ArtifactResolveBuilder;
import org.opensaml.saml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml.saml2.core.impl.IssuerBuilder;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.soap.client.http.HttpSOAPClient;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.SignatureBuilder;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureValidator;
import org.opensaml.xmlsec.signature.support.Signer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.syrnik.authprotocolsbackend.dto.AssertionData;
import com.syrnik.authprotocolsbackend.util.KeyLoader;

import net.shibboleth.utilities.java.support.httpclient.HttpClientBuilder;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;

public class Saml2Util {
    private static final String ROLE_ATTRIBUTE_NAME = "Role";

    public static String xmlObjectToString(XMLObject xmlObject) throws TransformerException, MarshallingException {
        Element element = marshalToElement(xmlObject);
        return elementToString(element);
    }

    public static Element marshalToElement(XMLObject xmlObject) throws MarshallingException {
        MarshallerFactory marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
        Marshaller marshaller = marshallerFactory.getMarshaller(xmlObject);
        return marshaller.marshall(xmlObject);
    }

    public static String elementToString(Element element) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter stringWriter = new StringWriter();
        transformer.transform(new DOMSource(element), new StreamResult(stringWriter));
        return stringWriter.toString();
    }

    public static AuthnRequest buildAuthnRequest(String assertionConsumerServiceUrl, String destination,
          String issuerId) throws Exception {
        AuthnRequest authnRequest = new AuthnRequestBuilder().buildObject();
        authnRequest.setID("_" + UUID.randomUUID());
        authnRequest.setIssueInstant(Instant.now());
        authnRequest.setAssertionConsumerServiceURL(assertionConsumerServiceUrl);
        authnRequest.setDestination(destination);
        authnRequest.setIssuer(Saml2Util.buildIssuer(issuerId));
        authnRequest.setProtocolBinding(SAMLConstants.SAML2_ARTIFACT_BINDING_URI);

        Saml2Util.sign(authnRequest);

        return authnRequest;
    }

    public static void validateSignature(Assertion assertion, X509Certificate certificate) throws SignatureException {
        BasicX509Credential credential = new BasicX509Credential(certificate);
        SignatureValidator.validate(assertion.getSignature(), credential);
    }

    public static HttpSOAPClient buildSoapClient() throws Exception {
        BasicParserPool parserPool = new BasicParserPool();
        parserPool.initialize();
        HttpClientBuilder clientBuilder = new HttpClientBuilder();
        HttpSOAPClient soapClient = new HttpSOAPClient();
        soapClient.setHttpClient(clientBuilder.buildClient());
        soapClient.setParserPool(parserPool);
        return soapClient;
    }

    public static SignatureSigningParameters buildSignatureSigningParameters() throws Exception {
        SignatureSigningParameters signatureSigningParameters = new SignatureSigningParameters();
        signatureSigningParameters.setSigningCredential(getSigningCredential());
        signatureSigningParameters.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signatureSigningParameters.setSignatureCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        return signatureSigningParameters;
    }

    public static ArtifactResolve buildArtifactResolve(String samlArt, String issuerId) throws Exception {
        ArtifactResolve artifactResolve = new ArtifactResolveBuilder().buildObject();
        artifactResolve.setIssuer(buildIssuer(issuerId));
        artifactResolve.setArtifact(buildArtifact(samlArt));
        artifactResolve.setID("_" + UUID.randomUUID());
        artifactResolve.setIssueInstant(Instant.now());

        sign(artifactResolve);

        return artifactResolve;
    }

    public static AssertionData mapAssertionData(Assertion assertion) {
        String nameID = assertion.getSubject().getNameID().getValue();
        String sessionIndex = assertion.getAuthnStatements().getFirst().getSessionIndex();
        Set<String> authorities = assertion
              .getAttributeStatements()
              .getFirst()
              .getAttributes()
              .stream()
              .filter(attribute -> attribute.getName().equals(ROLE_ATTRIBUTE_NAME))
              .map(attribute -> {
                  if(attribute.getAttributeValues().getFirst() instanceof XSString stringAttribute) {
                      return stringAttribute.getValue();
                  }
                  return null;
              })
              .collect(Collectors.toSet());
        return new AssertionData(nameID, sessionIndex, authorities);
    }

    public static Issuer buildIssuer(String issuerId) {
        Issuer issuer = new IssuerBuilder().buildObject();
        issuer.setValue(issuerId);
        return issuer;
    }

    public static Artifact buildArtifact(String samlArt) {
        Artifact artifact = new ArtifactBuilder().buildObject();
        artifact.setValue(samlArt);
        return artifact;
    }

    public static Signature buildSignature() throws Exception {
        SignatureBuilder builder = new SignatureBuilder();
        Signature signature = builder.buildObject(Signature.DEFAULT_ELEMENT_NAME);
        signature.setSigningCredential(getSigningCredential());
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        return signature;
    }

    public static void sign(SignableSAMLObject signableSAMLObject) throws Exception {
        Signature signature = buildSignature();

        signableSAMLObject.setSignature(signature);

        MarshallerFactory marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
        Marshaller marshaller = marshallerFactory.getMarshaller(signableSAMLObject);
        marshaller.marshall(signableSAMLObject);

        Signer.signObject(signature);
    }

    public static Credential getSigningCredential() throws Exception {
        PrivateKey privateKey = KeyLoader.loadPrivateKey("saml2/local.key");
        X509Certificate x509Certificate = KeyLoader.loadCertificate("saml2/local.crt");
        return new BasicX509Credential(x509Certificate, privateKey);
    }

    public static X509Certificate getCertificateFromSamlMetadata(String samlMetadata) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream input = new ByteArrayInputStream(samlMetadata.getBytes(StandardCharsets.UTF_8));
        Document doc = builder.parse(input);

        NodeList nodeList = doc.getElementsByTagName("ds:X509Certificate");
        if(nodeList.getLength() == 0) {
            throw new Exception("No X509Certificate element found in SAML metadata");
        }

        String certStr = nodeList.item(0).getTextContent();

        byte[] decodedCert = Base64.getDecoder().decode(certStr);

        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(
              new ByteArrayInputStream(decodedCert));

        return certificate;
    }
}
