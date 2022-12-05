package it.gov.pagopa.ranking.service.sign;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
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
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class P7mSignerServiceImpl implements P7mSignerService {

    private final CMSSignedDataGenerator cmsGenerator;
    private final OutputEncryptor encryptor;

    public P7mSignerServiceImpl(
            @Value("${app.ranking-build-file.p7m.cert}") String cert,
            @Value("${app.ranking-build-file.p7m.key}") String privateKeyPem) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            CertificateFactory certFactory= CertificateFactory
                    .getInstance("X.509", "BC");

            X509Certificate p7mCertificate = (X509Certificate) certFactory
//                    .generateCertificate(new ByteArrayInputStream(cert.getBytes(StandardCharsets.UTF_8)));
                    .generateCertificate(new FileInputStream("target/Baeldung.cer"));

            List<X509Certificate> certList = new ArrayList<>();
            certList.add(p7mCertificate);
            Store certs = new JcaCertStore(certList);

            char[] keystorePassword = "password".toCharArray();
            char[] keyPassword = "password".toCharArray();

            KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(new FileInputStream("target/Baeldung.p12"), keystorePassword);
            PrivateKey key = (PrivateKey) keystore.getKey("baeldung", keyPassword);

            cmsGenerator = new CMSSignedDataGenerator();

            ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256withRSA").build(key);
            cmsGenerator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(contentSigner, p7mCertificate));
            cmsGenerator.addCertificates(certs);

            encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC).setProvider("BC").build();
        } catch (CMSException | CertificateException | NoSuchProviderException e) {
            throw new IllegalStateException("Cannot build p7m encryptor", e);
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (OperatorCreationException e) {
            throw new RuntimeException(e);
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
