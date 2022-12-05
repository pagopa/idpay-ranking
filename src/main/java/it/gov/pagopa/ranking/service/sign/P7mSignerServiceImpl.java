package it.gov.pagopa.ranking.service.sign;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OutputEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@Service
@Slf4j
public class P7mSignerServiceImpl implements P7mSignerService {

    private final CMSEnvelopedDataGenerator cmsEnvelopedDataGenerator;
    private final OutputEncryptor encryptor;

    public P7mSignerServiceImpl(
            @Value("${app.ranking-build-file.p7m.cert}") String cert,
            @Value("${app.ranking-build-file.p7m.key}") String privateKeyPem) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            CertificateFactory certFactory= CertificateFactory
                    .getInstance("X.509", "BC");

            X509Certificate p7mCertificate = (X509Certificate) certFactory
                    .generateCertificate(new ByteArrayInputStream(cert.getBytes(StandardCharsets.UTF_8)));

            cmsEnvelopedDataGenerator = new CMSEnvelopedDataGenerator();

            JceKeyTransRecipientInfoGenerator jceKey = new JceKeyTransRecipientInfoGenerator(p7mCertificate);
            cmsEnvelopedDataGenerator.addRecipientInfoGenerator(jceKey);

            encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC).setProvider("BC").build();
        } catch (CMSException | CertificateException | NoSuchProviderException e) {
            throw new IllegalStateException("Cannot build p7m encryptor", e);
        }
    }

    @Override
    public Path sign(Path file) {
        CMSProcessableFile msg = new CMSProcessableFile(file.toFile());

        Path output = file.getParent().resolve("%s.p7m".formatted(file.getFileName().toString()));
        try (OutputStream outStream = new BufferedOutputStream(new FileOutputStream(output.toFile()))) {
            CMSEnvelopedData cmsEnvelopedData = cmsEnvelopedDataGenerator.generate(msg,encryptor);
            outStream.write(cmsEnvelopedData.getEncoded());
        } catch (IOException | CMSException e) {
            throw new IllegalStateException("Cannot build p7m file for input file %s".formatted(file), e);
        }

        return output;
    }
}
