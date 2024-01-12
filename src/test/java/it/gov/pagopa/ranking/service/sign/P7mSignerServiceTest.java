package it.gov.pagopa.ranking.service.sign;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

class P7mSignerServiceTest {

    private final Path tmpDir = Path.of("target", "tmp");

    @BeforeEach
    void prepareData() throws IOException {
        if(!Files.exists(tmpDir)){
            Files.createDirectories(tmpDir);
        }
        Files.copy(Path.of("README.md"), tmpDir.resolve( "README.md"), StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void testPkcs1PrivateKey() throws IOException {
        /*
         * generated through the following command using openssl 3.2:
         *    openssl genrsa -traditional 2048 > ca-key.pem
         *    openssl req -new -x509 -nodes -days 3600 -key ca-key.pem -out ca.pem
         *
         * If you want to transform a pkcs8 into pkcs1 (to convert the key in testPkcs8PrivateKey) execute the following command
         *    openssl rsa -in privateKey.key -traditional
         *
         * To inspect the certificate run the following command:
         *    openssl x509 -in ca.pem -text -noout
         */
        // This certificate will expire in 2033-11-20
        String pkcs1cert = """
                -----BEGIN CERTIFICATE-----
                MIIDazCCAlOgAwIBAgIUXt0R+RC8BjoagZQ8mXxqp758YpMwDQYJKoZIhvcNAQEL
                BQAwRTELMAkGA1UEBhMCQVUxEzARBgNVBAgMClNvbWUtU3RhdGUxITAfBgNVBAoM
                GEludGVybmV0IFdpZGdpdHMgUHR5IEx0ZDAeFw0yNDAxMTIwOTQ3MjlaFw0zMzEx
                MjAwOTQ3MjlaMEUxCzAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEw
                HwYDVQQKDBhJbnRlcm5ldCBXaWRnaXRzIFB0eSBMdGQwggEiMA0GCSqGSIb3DQEB
                AQUAA4IBDwAwggEKAoIBAQCxIhBXR8dM0LAW+MQv8A0YSrTquK2C+IfkV+Fmi6ip
                ooJF+r4m8bFE/5GlDQzPd+tkv2rxA1/gcuTi/t5qJMdmfL7LpOrVL3y7fQ0gsf1j
                azGZ+ySF6CM606SShY3j1V2h7r+osGq9cWsq2Wl3fY9O2kVCwvsMT8gUEHNTGENS
                NoUYAa/+Vs1keiR/TnJmkacoC65SC42/nlFcZOdJeI+hKbAaaTCn/oWEY0t12Rj6
                5fhrkb1aHKeaNMIEu5fJIExqoWZUa0RTU3eGNHci42nDaXxuhigJq2qt9WLZf1+U
                W1a+rQkJchQZDfloutSPOR+0GFU2GcxjGzagIHQI2J91AgMBAAGjUzBRMB0GA1Ud
                DgQWBBTI8zLmC7Uhn2YMtA31eMuPtcvvTTAfBgNVHSMEGDAWgBTI8zLmC7Uhn2YM
                tA31eMuPtcvvTTAPBgNVHRMBAf8EBTADAQH/MA0GCSqGSIb3DQEBCwUAA4IBAQCg
                5ogfzsim3jOAxAtLB7zQ8fbkf/bnwc/Lek91rCYYw3opeLlOwtBjeNPvHk3XNUFW
                Elr7E1ursXSsg8MVyz27gYc+1jpX9vLRAk40MIYmNiiigm9iDJR5zGajV//XtYSH
                3PQGexxQKQOfN/B20rNsFt53pBSpo2s1RT1CbDs5vJvedL/mP0L1J2a5kLXKltil
                M1ls/OHiWQ6mUHSmmC4+rdqddm4zVjFeeBah/g2C1PczMPwVcUCdmgLAC+zQ8gFE
                kFsikDfTjDaMiNrCzPg5qjkKm3iIt4GugS56Ju+ggkZssoQ4XTBS8Fng9UjhaMX6
                3TJCwlaKaP+wAtwTut3n
                -----END CERTIFICATE-----
                """;
        String pkcs1key = """
                -----BEGIN RSA PRIVATE KEY-----
                MIIEoQIBAAKCAQEAsSIQV0fHTNCwFvjEL/ANGEq06ritgviH5FfhZouoqaKCRfq+
                JvGxRP+RpQ0Mz3frZL9q8QNf4HLk4v7eaiTHZny+y6Tq1S98u30NILH9Y2sxmfsk
                hegjOtOkkoWN49Vdoe6/qLBqvXFrKtlpd32PTtpFQsL7DE/IFBBzUxhDUjaFGAGv
                /lbNZHokf05yZpGnKAuuUguNv55RXGTnSXiPoSmwGmkwp/6FhGNLddkY+uX4a5G9
                WhynmjTCBLuXySBMaqFmVGtEU1N3hjR3IuNpw2l8boYoCatqrfVi2X9flFtWvq0J
                CXIUGQ35aLrUjzkftBhVNhnMYxs2oCB0CNifdQIDAQABAoH/S6ZAqbCowtBduXHa
                V/Qto4eZvKUiysyvPNZLbYA33ZA0va5Y3POxbHEPGBklX3XXvS9YfOuesZJgLpIA
                a1SfKdDHq4bDbvODpznBDDWirNMbBqg7m5+IwZb3XPGs/PYjBwWa1Rst0GVKtQIl
                XjhBpsDm05xyRiUh+MUN0Rng++WW87sJcyFrs1WohxYhupAu/wdi5CmtRS0Ka/tE
                9Z+RGhG+OxpyasYgYdO/kRyflSn/gy1elO55+EYbYiHLcnZLb3MeUq6TJhD3JBK4
                R3DW3mGcrZSN/QSQ6CDnbjwdtaN32OjGujstG/8a7qbgxpBWOGH14XuDC+IkufVe
                oAshAoGBANwwHJEiFjhvZfsvjWBxAnnfonT5ZcQPs8XD1GEqV4FGPNvRihhB7KMs
                IXEr1VBHyTM0WLIfYt4zJbsa0AgKmIb1xp++vO0/dYq5YUXBBpznwssDiKNH7Tl9
                lJLe+kr9zhASBW2Q+fW6iVxA5y6YnfeUQhbGGQCOP4zl8EVM8F6DAoGBAM3xSpwY
                vPuYxg2JU9GchHSO8CIeYuWNI5LIH0u5IvIe5ir/FbQO6yXhu02oyAWDOEngLZz2
                5iNjva0uCyUFu+Na6qZWJf5/olc958/G8yg7InIPORJBLthj0zTpzfJgfgfg2Bky
                /QbwEJlrhHuYsYcRqh0KSHZM6HyXm6o+16inAoGAOFKmPAm4HV+BeqtDQrQlqf8m
                kz1oZqqNlbzuCpzGHW9WCfSjmmjimyqis0w/GHYEZXCyb33P0H7M2/uT0zrCoWMl
                F5+2vAlLi9y3oRwrEN/H2B5jSUEQ0W/qHVekNqKNn1a8xLYc+lpo4IxJloW7gqUN
                8r1hj97TKrt2poNzyPkCgYAQfo/sOqsAN7c9JE+bTT7qmvwur2JaYliEDBU9mmFr
                JQBoVy/k0QEScqbuzGLg1iau/A6LMNcz5Gwvr61i1Le8uO5sTiAS5yQYerPTV2ro
                QPw8SG3YwXHAERa8eTFuhT3y4ZZNRmEvqhCaSRs+TveBCvAmDUnKd9RzDi2AkDHz
                pwKBgQDGbULryADQSIBFAF3HriKtX8WpdbgBEUemmvDxYCp7usLTUGABqYBRt3Yx
                9AxpLt2g0MjCrxzQuhH19mtO/pGk765CxNfYmsLYE1bTPXYNeGlrTdBXSjtrfpVH
                yxTubnebM2dMI6jy08Hrbj7AQzt9PZaTKipiHusMQd9huyOkFA==
                -----END RSA PRIVATE KEY-----
                """;
        test(pkcs1cert, pkcs1key);
    }

    @Test
    void testPkcs8PrivateKey() throws IOException {
        /*
         * generated through the following command executed considering the ca-key.pem generated with the commands described in testPkcs1PrivateKey:
         *    openssl req -x509 -sha256 -nodes -days 3600 -newkey rsa:2048 -keyout privateKey.key -out certificate.crt -subj "/CN=User1" -addext extendedKeyUsage=codeSigning -addext keyUsage=digitalSignature
         *
         * If you want to transform a pkcs1 into pkcs8 (to convert the key in testPkcs1PrivateKey) execute the following command
         *    openssl pkcs8 -topk8 -inform pem -in ca-key.pem -outform pem -nocrypt
         *
         * To inspect the certificate run the following command:
         *    openssl x509 -in ca.pem -text -noout
         */
        // This certificate will expire in 2033-11-20
        String pkcs8cert = """
                -----BEGIN CERTIFICATE-----
                MIIDazCCAlOgAwIBAgIUXt0R+RC8BjoagZQ8mXxqp758YpMwDQYJKoZIhvcNAQEL
                BQAwRTELMAkGA1UEBhMCQVUxEzARBgNVBAgMClNvbWUtU3RhdGUxITAfBgNVBAoM
                GEludGVybmV0IFdpZGdpdHMgUHR5IEx0ZDAeFw0yNDAxMTIwOTQ3MjlaFw0zMzEx
                MjAwOTQ3MjlaMEUxCzAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEw
                HwYDVQQKDBhJbnRlcm5ldCBXaWRnaXRzIFB0eSBMdGQwggEiMA0GCSqGSIb3DQEB
                AQUAA4IBDwAwggEKAoIBAQCxIhBXR8dM0LAW+MQv8A0YSrTquK2C+IfkV+Fmi6ip
                ooJF+r4m8bFE/5GlDQzPd+tkv2rxA1/gcuTi/t5qJMdmfL7LpOrVL3y7fQ0gsf1j
                azGZ+ySF6CM606SShY3j1V2h7r+osGq9cWsq2Wl3fY9O2kVCwvsMT8gUEHNTGENS
                NoUYAa/+Vs1keiR/TnJmkacoC65SC42/nlFcZOdJeI+hKbAaaTCn/oWEY0t12Rj6
                5fhrkb1aHKeaNMIEu5fJIExqoWZUa0RTU3eGNHci42nDaXxuhigJq2qt9WLZf1+U
                W1a+rQkJchQZDfloutSPOR+0GFU2GcxjGzagIHQI2J91AgMBAAGjUzBRMB0GA1Ud
                DgQWBBTI8zLmC7Uhn2YMtA31eMuPtcvvTTAfBgNVHSMEGDAWgBTI8zLmC7Uhn2YM
                tA31eMuPtcvvTTAPBgNVHRMBAf8EBTADAQH/MA0GCSqGSIb3DQEBCwUAA4IBAQCg
                5ogfzsim3jOAxAtLB7zQ8fbkf/bnwc/Lek91rCYYw3opeLlOwtBjeNPvHk3XNUFW
                Elr7E1ursXSsg8MVyz27gYc+1jpX9vLRAk40MIYmNiiigm9iDJR5zGajV//XtYSH
                3PQGexxQKQOfN/B20rNsFt53pBSpo2s1RT1CbDs5vJvedL/mP0L1J2a5kLXKltil
                M1ls/OHiWQ6mUHSmmC4+rdqddm4zVjFeeBah/g2C1PczMPwVcUCdmgLAC+zQ8gFE
                kFsikDfTjDaMiNrCzPg5qjkKm3iIt4GugS56Ju+ggkZssoQ4XTBS8Fng9UjhaMX6
                3TJCwlaKaP+wAtwTut3n
                -----END CERTIFICATE-----
                """;
        String pkcs8key = """
                -----BEGIN PRIVATE KEY-----
                MIIEuwIBADANBgkqhkiG9w0BAQEFAASCBKUwggShAgEAAoIBAQCxIhBXR8dM0LAW
                +MQv8A0YSrTquK2C+IfkV+Fmi6ipooJF+r4m8bFE/5GlDQzPd+tkv2rxA1/gcuTi
                /t5qJMdmfL7LpOrVL3y7fQ0gsf1jazGZ+ySF6CM606SShY3j1V2h7r+osGq9cWsq
                2Wl3fY9O2kVCwvsMT8gUEHNTGENSNoUYAa/+Vs1keiR/TnJmkacoC65SC42/nlFc
                ZOdJeI+hKbAaaTCn/oWEY0t12Rj65fhrkb1aHKeaNMIEu5fJIExqoWZUa0RTU3eG
                NHci42nDaXxuhigJq2qt9WLZf1+UW1a+rQkJchQZDfloutSPOR+0GFU2GcxjGzag
                IHQI2J91AgMBAAECgf9LpkCpsKjC0F25cdpX9C2jh5m8pSLKzK881kttgDfdkDS9
                rljc87FscQ8YGSVfdde9L1h8656xkmAukgBrVJ8p0MerhsNu84OnOcEMNaKs0xsG
                qDubn4jBlvdc8az89iMHBZrVGy3QZUq1AiVeOEGmwObTnHJGJSH4xQ3RGeD75Zbz
                uwlzIWuzVaiHFiG6kC7/B2LkKa1FLQpr+0T1n5EaEb47GnJqxiBh07+RHJ+VKf+D
                LV6U7nn4RhtiIctydktvcx5SrpMmEPckErhHcNbeYZytlI39BJDoIOduPB21o3fY
                6Ma6Oy0b/xrupuDGkFY4YfXhe4ML4iS59V6gCyECgYEA3DAckSIWOG9l+y+NYHEC
                ed+idPllxA+zxcPUYSpXgUY829GKGEHsoywhcSvVUEfJMzRYsh9i3jMluxrQCAqY
                hvXGn7687T91irlhRcEGnOfCywOIo0ftOX2Ukt76Sv3OEBIFbZD59bqJXEDnLpid
                95RCFsYZAI4/jOXwRUzwXoMCgYEAzfFKnBi8+5jGDYlT0ZyEdI7wIh5i5Y0jksgf
                S7ki8h7mKv8VtA7rJeG7TajIBYM4SeAtnPbmI2O9rS4LJQW741rqplYl/n+iVz3n
                z8bzKDsicg85EkEu2GPTNOnN8mB+B+DYGTL9BvAQmWuEe5ixhxGqHQpIdkzofJeb
                qj7XqKcCgYA4UqY8CbgdX4F6q0NCtCWp/yaTPWhmqo2VvO4KnMYdb1YJ9KOaaOKb
                KqKzTD8YdgRlcLJvfc/Qfszb+5PTOsKhYyUXn7a8CUuL3LehHCsQ38fYHmNJQRDR
                b+odV6Q2oo2fVrzEthz6WmjgjEmWhbuCpQ3yvWGP3tMqu3amg3PI+QKBgBB+j+w6
                qwA3tz0kT5tNPuqa/C6vYlpiWIQMFT2aYWslAGhXL+TRARJypu7MYuDWJq78Dosw
                1zPkbC+vrWLUt7y47mxOIBLnJBh6s9NXauhA/DxIbdjBccARFrx5MW6FPfLhlk1G
                YS+qEJpJGz5O94EK8CYNScp31HMOLYCQMfOnAoGBAMZtQuvIANBIgEUAXceuIq1f
                xal1uAERR6aa8PFgKnu6wtNQYAGpgFG3djH0DGku3aDQyMKvHNC6EfX2a07+kaTv
                rkLE19iawtgTVtM9dg14aWtN0FdKO2t+lUfLFO5ud5szZ0wjqPLTwetuPsBDO309
                lpMqKmIe6wxB32G7I6QU
                -----END PRIVATE KEY-----
                """;
        test(pkcs8cert, pkcs8key);
    }


    private void test(String cert, String key) throws IOException {
        P7mSignerService service = new P7mSignerServiceImpl(cert, key);

        Path signedFile = service.sign(tmpDir.resolve("README.md"));

        Assertions.assertNotNull(signedFile);
        Assertions.assertEquals("README.md.p7m", signedFile.getFileName().toString());
        Assertions.assertTrue(Files.exists(signedFile));
        Assertions.assertNotEquals(0, Files.size(signedFile));

        Assertions.assertTrue(service.verifySign(signedFile), "Sign validation failed! maybe is test certificate expired?");
    }
}
