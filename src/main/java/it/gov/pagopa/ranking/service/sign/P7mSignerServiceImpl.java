package it.gov.pagopa.ranking.service.sign;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedDataStreamGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class P7mSignerServiceImpl implements P7mSignerService {

    private final CMSSignedDataStreamGenerator cmsGenerator;

    public P7mSignerServiceImpl(
            @Value("${app.ranking-build-file.p7m.cert}") String cert,
            @Value("${app.ranking-build-file.p7m.key}") String privateKeyPem) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            CertificateFactory certFactory = CertificateFactory
                    .getInstance("X.509", "BC");

            X509Certificate p7mCertificate = (X509Certificate) certFactory
                    .generateCertificate(new ByteArrayInputStream(cert.getBytes(StandardCharsets.UTF_8)));

            List<X509Certificate> certList = new ArrayList<>();
            certList.add(p7mCertificate);
            Store<?> certs = new JcaCertStore(certList);

            String privateKeyContent = privateKeyPem.substring(28).replace("\n", "").trim().replace("-----END PRIVATE KEY-----", "");
            byte[] decodedPrivateKeyContent = Base64.getDecoder().decode(privateKeyContent);
            final KeySpec spec = getKeySpecFromAsn1StructuredData(decodedPrivateKeyContent);// new PKCS8EncodedKeySpec(decodedPrivateKeyContent);
            final KeyFactory factory = KeyFactory.getInstance("RSA");
            final PrivateKey key = factory.generatePrivate(spec);

            cmsGenerator = new CMSSignedDataStreamGenerator();

            ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256withRSA").build(key);
            cmsGenerator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(contentSigner, p7mCertificate));
            cmsGenerator.addCertificates(certs);
        } catch (CMSException | CertificateException | NoSuchProviderException | NoSuchAlgorithmException |
                 InvalidKeySpecException | OperatorCreationException e) {
            throw new IllegalStateException("Cannot build p7m encryptor", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static KeySpec getKeySpecFromAsn1StructuredData(byte[] decodedPrivateKeyContent) throws IOException {
        try(ByteArrayInputStream privateKeyAsInputStream = new ByteArrayInputStream(decodedPrivateKeyContent)) {
            ASN1InputStream stream = new ASN1InputStream(privateKeyAsInputStream);
            ASN1Sequence asn1Sequence = (ASN1Sequence)stream.readObject();

//            if (asn1Sequence.getValue().size() < 9) {
//                throw new RuntimeException("Parsed key content doesn't have the minimum required sequence size of 9");
//            }

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
        if (asn1Object instanceof ASN1Integer) {
            return ((ASN1Integer) asn1Object).getValue();
        } else {
            throw new RuntimeException(String.format(
                    "Unable to parse the provided value of the object type [%s]. The type should be an instance of [%s]",
                    asn1Object.getClass().getName(), ASN1Integer.class.getName())
            );
        }
    }

    @Override
    public Path sign(Path file) {
        CMSProcessableFile msg = new CMSProcessableFile(file.toFile());

        Path output = file.getParent().resolve("%s.p7m".formatted(file.getFileName().toString()));
        try (InputStream is = msg.getInputStream();
             OutputStream os = cmsGenerator.open(new FileOutputStream(output.toFile()))) {
            is.transferTo(os);
        } catch (IOException | CMSException e) {
            throw new IllegalStateException("Cannot build p7m file for input file %s".formatted(file), e);
        }

        return output;
    }
}
