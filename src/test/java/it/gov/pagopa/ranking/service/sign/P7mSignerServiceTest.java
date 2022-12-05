package it.gov.pagopa.ranking.service.sign;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

class P7mSignerServiceTest {

    private final String cert = """
            -----BEGIN CERTIFICATE-----
            MIIDnzCCAoegAwIBAgIUJ8/0z+sR6Llr9FcIGoc5nvZQydgwDQYJKoZIhvcNAQEL
            BQAwXzELMAkGA1UEBhMCSVQxDTALBgNVBAgMBFJPTUUxDTALBgNVBAcMBFJPTUUx
            DjAMBgNVBAoMBUlEUEFZMQ4wDAYDVQQLDAVJRFBBWTESMBAGA1UEAwwJbG9jYWxo
            b3N0MB4XDTIyMTEwOTE1MTI0NFoXDTMyMDkxNzE1MTI0NFowXzELMAkGA1UEBhMC
            SVQxDTALBgNVBAgMBFJPTUUxDTALBgNVBAcMBFJPTUUxDjAMBgNVBAoMBUlEUEFZ
            MQ4wDAYDVQQLDAVJRFBBWTESMBAGA1UEAwwJbG9jYWxob3N0MIIBIjANBgkqhkiG
            9w0BAQEFAAOCAQ8AMIIBCgKCAQEArDOJKswwCaKdYJbaHZz3bgEIl7z1ArZpNI54
            ZGaXcRitiwjr/W9fenW69mG7IAlITuPtaIu4iggXTcSRuaulres2EvuP7KjL0tfo
            x/PstqaMZzLF8wOYfJE4iJ8ffcQL67LJ3/Wwn2FhYVV+4D2AYW8QPdRm406HJG7b
            NKLmdM9AFUQp6zoTvNegyWQyAfH40i72UopltDubcAykD6YgkRctCtKd8h/BRpIR
            tMn0AGLM/o5qwYu+eCAy8/7Ppj3HzCwHkDOJad/g2pRj4soJdvn5rP6TM4OVtZ7V
            ehxionkaccBPcyDGSrIo5837XYaGv3r7Rn0rCplfxnU4Gtmd5wIDAQABo1MwUTAd
            BgNVHQ4EFgQUPYfJeHRHwSLmcueB8jUQSHUReVIwHwYDVR0jBBgwFoAUPYfJeHRH
            wSLmcueB8jUQSHUReVIwDwYDVR0TAQH/BAUwAwEB/zANBgkqhkiG9w0BAQsFAAOC
            AQEAK34LEHSVM44Wwbs9nKDKeQTRGosdd+gQSrqGf3nI0vkhckuaoYPnuFKi+eo2
            r+J6xXgqhQfrvhXnYxNEJr9U+9ELBc3IjG6bTUS6HyWhu2PJCeckxQJqonVntl99
            jmEr4G7QJeDc9oJmC0NJqBmQS/D0tMxChNWpYe1AoGXwqc4S6NTd3x2Z8THzv8du
            MMn7+1f/VOWe7/Iuuvx5DHN2JFi0lvhMqwglIweGn/qLGB0+r9GM+QlfGuZvUey2
            x3C0DLQnNIkNKktGjaNjCmpZcd9SIVi6TOPpR+AxlIddYvUXu4GYVXyfDPgzPeha
            JDiI4WMkIMmYSzhMc/lfuDMGow==
            -----END CERTIFICATE-----
            """;

    private final P7mSignerService service = new P7mSignerServiceImpl(cert, "");

    private final Path tmpDir = Path.of("target", "tmp");

    @BeforeEach
    void prepareData() throws IOException {
        if(!Files.exists(tmpDir)){
            Files.createDirectories(tmpDir);
        }
        Files.copy(Path.of("README.md"), tmpDir.resolve( "README.md"), StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void test() throws IOException {
        Path signedFile = service.sign(tmpDir.resolve("README.md"));

        Assertions.assertNotNull(signedFile);
        Assertions.assertEquals("README.md.p7m", signedFile.getFileName().toString());
        Assertions.assertTrue(Files.exists(signedFile));
        Assertions.assertNotEquals(0, Files.size(signedFile));

        // TODO verify sign
    }
}
