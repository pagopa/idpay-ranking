package it.gov.pagopa.ranking.service.sign;

import it.gov.pagopa.ranking.utils.cert.PrivateKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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

            final PrivateKey key = PrivateKeyUtils.readPrivateKeyPem(privateKeyPem);

            cmsGenerator = new CMSSignedDataStreamGenerator();

            ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256withRSA").build(key);
            cmsGenerator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(contentSigner, p7mCertificate));
            cmsGenerator.addCertificates(certs);
        } catch (CMSException | CertificateException | NoSuchProviderException | OperatorCreationException e) {
            throw new IllegalStateException("Cannot build p7m encryptor", e);
        }
    }

    @Override
    public Path sign(Path file) {
        CMSProcessableFile msg = new CMSProcessableFile(file.toFile());

        Path output = file.getParent().resolve("%s.p7m".formatted(file.getFileName().toString()));
        try (InputStream is = msg.getInputStream();
             OutputStream os = new FileOutputStream(output.toFile())) {
            sign(is, os).close();
        } catch (IOException | CMSException | IllegalStateException e) {
            throw new IllegalStateException("Cannot build p7m file for input file %s".formatted(file), e);
        }

        return output;
    }

    @Override
    public OutputStream sign(InputStream is, OutputStream os) {
        try {
            OutputStream cos = cmsGenerator.open(os, true);
            is.transferTo(cos);
            return cos;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write p7m signed data into output stream", e);
        }
    }

    @Override
    public boolean verifySign(Path signedFile) {
        try(InputStream signedFileStream = Files.newInputStream(signedFile)){
            return verifySign(signedFileStream);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read signed file: %s".formatted(signedFile), e);
        }
    }

    @Override
    public boolean verifySign(InputStream signedData) {
        try {
            CMSSignedData s = new CMSSignedData(signedData);
            Store<X509CertificateHolder> certs = s.getCertificates();
            SignerInformationStore signers = s.getSignerInfos();
            Collection<SignerInformation> c = signers.getSigners();

            if(c.isEmpty()){
                log.info("The inputStream has not sign");
                return false;
            }

            log.debug("Found {} signs in inputStream", c.size());
            for(SignerInformation signer : c) {
                @SuppressWarnings("unchecked")
                Collection<X509CertificateHolder> certCollection = certs.getMatches(signer.getSID());
                Iterator<X509CertificateHolder> certIt = certCollection.iterator();
                X509CertificateHolder certHolder = certIt.next();
                boolean result = signer.verify(new JcaSimpleSignerInfoVerifierBuilder().build(certHolder));
                if(!result){
                    log.info("The inputStream has an invalid sign: %s".formatted(signer.getSID()));
                    return false;
                }
            }

            return true;
        } catch (CMSException | OperatorCreationException | CertificateException e){
            return false;
        }
    }
}
