package it.gov.pagopa.ranking.utils.cert;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Base64;

public final class PrivateKeyUtils {

    public static final String PKCS8_HEADER = "-----BEGIN PRIVATE KEY-----";
    public static final String PKCS8_FOOTER = "-----END PRIVATE KEY-----";

    public static final String PKCS1_HEADER = "-----BEGIN RSA PRIVATE KEY-----";
    public static final String PKCS1_FOOTER = "-----END RSA PRIVATE KEY-----";

    private PrivateKeyUtils(){}

    public static PrivateKey readPrivateKeyPem(String privateKeyPem){
        try {
            final KeySpec spec;
            if (privateKeyPem.startsWith(PKCS8_HEADER)) {
                String privateKeyContent = extractPemBody(privateKeyPem, PKCS8_HEADER, PKCS8_FOOTER);
                spec = new PKCS8EncodedKeySpec(decodeBase64(privateKeyContent));
            } else if (privateKeyPem.startsWith(PKCS1_HEADER)) {
                String privateKeyContent = extractPemBody(privateKeyPem, PKCS1_HEADER, PKCS1_FOOTER);
                spec = getKeySpecFromAsn1StructuredData(decodeBase64(privateKeyContent));
            } else {
                throw new IllegalArgumentException("Invalid privateKey format: %s".formatted(privateKeyPem));
            }

            final KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePrivate(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e){
            throw new IllegalArgumentException("Cannot read private key from pem", e);
        }
    }

    private static String extractPemBody(String privateKeyPem, String header, String footer) {
        return privateKeyPem
                .substring(header.length())
                .replace("\n", "").trim()
                .replace(footer, "");
    }

    private static byte[] decodeBase64(String privateKeyContent) {
        return Base64.getDecoder().decode(privateKeyContent);
    }

    private static KeySpec getKeySpecFromAsn1StructuredData(byte[] decodedPrivateKeyContent) throws IOException {
        try(ByteArrayInputStream privateKeyAsInputStream = new ByteArrayInputStream(decodedPrivateKeyContent)) {
            ASN1InputStream stream = new ASN1InputStream(privateKeyAsInputStream);
            ASN1Sequence asn1Sequence = (ASN1Sequence)stream.readObject();

            BigInteger modulus = extractIntValueFrom(asn1Sequence.getObjectAt(1));
            BigInteger publicExponent = extractIntValueFrom(asn1Sequence.getObjectAt(2));
            BigInteger privateExponent = extractIntValueFrom(asn1Sequence.getObjectAt(3));
            BigInteger primeP = extractIntValueFrom(asn1Sequence.getObjectAt(4));
            BigInteger primeQ = extractIntValueFrom(asn1Sequence.getObjectAt(5));
            BigInteger primeExponentP = extractIntValueFrom(asn1Sequence.getObjectAt(6));
            BigInteger primeExponentQ = extractIntValueFrom(asn1Sequence.getObjectAt(7));
            BigInteger crtCoefficient = extractIntValueFrom(asn1Sequence.getObjectAt(8));

            return new RSAPrivateCrtKeySpec(
                    modulus, publicExponent, privateExponent,
                    primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient
            );
        }
    }

    private static BigInteger extractIntValueFrom(ASN1Encodable asn1Object) {
        if (asn1Object instanceof ASN1Integer asn1Integer) {
            return asn1Integer.getValue();
        } else {
            throw new IllegalStateException(String.format(
                    "Unable to parse the provided value of the object type [%s]. The type should be an instance of [%s]",
                    asn1Object.getClass().getName(), ASN1Integer.class.getName())
            );
        }
    }
}
