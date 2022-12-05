package it.gov.pagopa.ranking.service.sign;

import java.nio.file.Path;

public interface P7mSignerService {
    Path sign(Path file);
}
