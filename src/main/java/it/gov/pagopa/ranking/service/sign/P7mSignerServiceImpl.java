package it.gov.pagopa.ranking.service.sign;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class P7mSignerServiceImpl implements P7mSignerService {

    private final CMSSignedDataGenerator cmsGenerator;

    public P7mSignerServiceImpl(
            @Value("${app.ranking-build-file.p7m.cert}") String cert,
            @Value("${app.ranking-build-file.p7m.key}") String privateKeyPem) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            CertificateFactory certFactory= CertificateFactory
                    .getInstance("X.509", "BC");

            X509Certificate p7mCertificate = (X509Certificate) certFactory
                    .generateCertificate(new ByteArrayInputStream(cert.getBytes(StandardCharsets.UTF_8)));

            List<X509Certificate> certList = new ArrayList<>();
            certList.add(p7mCertificate);
            Store<?> certs = new JcaCertStore(certList);

            String privateKeyContent = privateKeyPem.substring(28).replace("\n", "").trim().replace("-----END PRIVATE KEY-----", "");
            byte[] decodedPrivateKeyContent = Base64.getDecoder().decode(privateKeyContent);
            final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedPrivateKeyContent);
            final KeyFactory factory = KeyFactory.getInstance("RSA");
            final PrivateKey key = factory.generatePrivate(spec);

            cmsGenerator = new CMSSignedDataGenerator();

            ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256withRSA").build(key);
            cmsGenerator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(contentSigner, p7mCertificate));
            cmsGenerator.addCertificates(certs);
        } catch (CMSException | CertificateException | NoSuchProviderException | NoSuchAlgorithmException |
                 InvalidKeySpecException | OperatorCreationException e) {
            throw new IllegalStateException("Cannot build p7m encryptor", e);
        }
    }

    @Override
    public Path sign(Path file) {
        CMSProcessableFile msg = new CMSProcessableFile(file.toFile());

        Path output = file.getParent().resolve("%s.p7m".formatted(file.getFileName().toString()));
        try (OutputStream outStream = new BufferedOutputStream(new FileOutputStream(output.toFile()))) {
            CMSSignedData cmsSignedData = cmsGenerator.generate(msg, true);
            outStream.write(cmsSignedData.getEncoded());
        } catch (IOException | CMSException e) {
            throw new IllegalStateException("Cannot build p7m file for input file %s".formatted(file), e);
        }

        return output;
    }
}
