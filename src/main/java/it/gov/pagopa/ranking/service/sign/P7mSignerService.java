package it.gov.pagopa.ranking.service.sign;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public interface P7mSignerService {
    Path sign(Path file);
    OutputStream sign(InputStream is, OutputStream os);

    boolean verifySign(Path signedFile);
    boolean verifySign(InputStream is);
}
