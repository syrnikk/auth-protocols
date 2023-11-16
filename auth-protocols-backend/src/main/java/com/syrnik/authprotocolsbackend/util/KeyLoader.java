package com.syrnik.authprotocolsbackend.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;
import org.springframework.core.io.ClassPathResource;

public class KeyLoader {

    public static X509Certificate loadCertificate(String certResourcePath) throws Exception {
        try(InputStream is = new ClassPathResource(certResourcePath).getInputStream()) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(is);
        }
    }

    public static PrivateKey loadPrivateKey(String keyResourcePath)
          throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String keyPEM;
        try(InputStream is = new ClassPathResource(keyResourcePath).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            keyPEM = reader
                  .lines()
                  .filter(line -> !line.startsWith("-----BEGIN") && !line.startsWith("-----END"))
                  .collect(Collectors.joining());
        }
        byte[] decoded = Base64.getDecoder().decode(keyPEM);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
