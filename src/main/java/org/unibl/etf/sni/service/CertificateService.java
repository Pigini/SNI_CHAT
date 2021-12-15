package org.unibl.etf.sni.service;


import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.servlet.http.Part;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.RSAKeyGenParameterSpec;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CertificateService {

    private static final String KEYSTORE_FILE = "keystore.p12";
    private static final String KEYSTORE_PASS = "sigurnost";
    private static final String CA_ALIAS = "root";

    private final PrivateKey privateKey;
    private final X509Certificate caCertificate;

    private static final String PUBLIC_KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256WithRSA";
    private static final String KEYSTORE_TYPE_PKCS12 = "PKCS12";
    private static final int CERT_DURATION = 365;
    private static final int KEY_SIZE = 2048;

    public CertificateService() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE_PKCS12);
        char[] pwd = KEYSTORE_PASS.toCharArray();
        keyStore.load(getClass().getResourceAsStream(KEYSTORE_FILE), pwd);
        caCertificate = (X509Certificate) keyStore.getCertificate(CA_ALIAS);
        privateKey = (PrivateKey) keyStore.getKey(CA_ALIAS, pwd);
    }

    public Certificate createCertificate(String commonName) throws Exception {
        KeyPair keyPair = generateKeyPair();
        return generateCertificateParts(commonName,keyPair);
    }


    public Certificate createTomcatCertificate() throws Exception {

        String keystoreFilename = "tomcat.p12";
        String alias = "tomcat";
        String commonName = "tomcat";
        X509Certificate certificate = null;

        try {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE_PKCS12);
            keyStore.load(null, null);
            try(FileOutputStream fos = new FileOutputStream(keystoreFilename)){
                char[] pwd = KEYSTORE_PASS.toCharArray();
                keyStore.store(fos, pwd);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE_PKCS12);
            keyStore.load(new FileInputStream(keystoreFilename), KEYSTORE_PASS.toCharArray());

            KeyPair keyPair = generateKeyPair();

            certificate = (X509Certificate) generateCertificateParts(commonName,keyPair);


            PrivateKey privateKey = keyPair.getPrivate();
            X509Certificate[] chain = new X509Certificate[1];
            chain[0] = certificate;
            keyStore.setKeyEntry(alias, privateKey, KEYSTORE_PASS.toCharArray(), chain);
            keyStore.store(new FileOutputStream(keystoreFilename), KEYSTORE_PASS.toCharArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return certificate;

    }

    private KeyPair generateKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(PUBLIC_KEY_ALGORITHM);
        keyGen.initialize(new RSAKeyGenParameterSpec(KEY_SIZE, RSAKeyGenParameterSpec.F4));
        return keyGen.genKeyPair();

    }

    private Certificate generateCertificateParts(String commonName,KeyPair keyPair) throws OperatorCreationException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, CertificateException {

        Date validFrom = new Date();
        Date validTo = Date.from(LocalDate.now().plus(CERT_DURATION, ChronoUnit.DAYS).atStartOfDay().toInstant(ZoneOffset.UTC));

        X500Name name = new X500NameBuilder(BCStyle.INSTANCE)
                .addRDN(BCStyle.C, "BA")
                .addRDN(BCStyle.ST, "RS")
                .addRDN(BCStyle.CN, commonName).build();

        X500Name issuerCA = new X500Name(caCertificate.getSubjectX500Principal().getName());

        JcaX509v3CertificateBuilder jcaX509v3CertificateBuilder = new JcaX509v3CertificateBuilder(
                issuerCA,
                new BigInteger(10,new SecureRandom()),
                validFrom,
                validTo,
                name,
                keyPair.getPublic()
        );

        ContentSigner signer = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).build(privateKey);
        return new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).
                getCertificate(jcaX509v3CertificateBuilder.build(signer));
    }

    public boolean verifyCertificate(Part part) {
        try {
            X509Certificate certificate = createCertFromPart(part);
            return verifyCertificate(certificate);
        } catch (Exception e) {
            Logger.getLogger(CertificateService.class.getName()).log(Level.SEVERE, e.toString());
            return false;
        }
    }

    public boolean verifyCertificate(X509Certificate certificate) {
        try {
            certificate.checkValidity();
            certificate.verify(caCertificate.getPublicKey());
            return true;
        } catch (Exception e) {
            Logger.getLogger(CertificateService.class.getName()).log(Level.SEVERE, e.toString());
            return false;
        }
    }

    private X509Certificate createCertFromPart(Part part) throws CertificateException, IOException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certificateFactory.generateCertificate(part.getInputStream());
    }

}
