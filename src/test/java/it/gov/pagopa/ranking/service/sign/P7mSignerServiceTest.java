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
         * generated through the following commands:
         *    openssl genrsa 2048 > ca-key.pem
         *    openssl req -new -x509 -nodes -days 3600 -key ca-key.pem -out ca.pem
         */
        String pkcs1cert = """
                -----BEGIN CERTIFICATE-----
                MIIDIDCCAggCCQDfvekeOeeawzANBgkqhkiG9w0BAQQFADBSMRMwEQYDVQQKEwpN
                eSBDb21wYW55MRAwDgYDVQQHEwdNeSBUb3duMRwwGgYDVQQIExNTdGF0ZSBvciBQ
                cm92aWRlbmNlMQswCQYDVQQGEwJVUzAeFw0yMjEyMDcxNjI4MTRaFw0zMjEwMTUx
                NjI4MTRaMFIxEzARBgNVBAoTCk15IENvbXBhbnkxEDAOBgNVBAcTB015IFRvd24x
                HDAaBgNVBAgTE1N0YXRlIG9yIFByb3ZpZGVuY2UxCzAJBgNVBAYTAlVTMIIBIjAN
                BgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxMj9ISyb8s7vUed/NGTspsOMzlFN
                U1dUcxs5q9rx75iIlRSVzqLUBAzJx4oiHiU8xknKejkAtIyGzSjJ0Wba7I65RxCF
                evPZ+19aPGunnqQfBG1M1iu4M7Ur8qKV5oJnPFLldTj8h157b766m6kLYfLrT2e8
                CzHdO+49Iy/GpaeTWAcB2Y7lRCQE7BzgWo73H0xcvcQExpHfX1U6wX2ypkj8n8q+
                hZVcUACLP4hlFbhT9v3xYzRdKq+KWSPNz9xqwY6MU6bK5E/y70Ws8FNk6m5Wnxvf
                RKriA4NeHCrN4p1Tl5dKzVgCh55qXTB07wEhqJPS4Cugp/oJsvDoTHUFkQIDAQAB
                MA0GCSqGSIb3DQEBBAUAA4IBAQABqSAp4fhsMq5D0e0YjktPN9urmAZCkyhrTczB
                SIqARe+JT9prs9B1damOh+N80Vf4fI47c5TLAVrHt6aXfwJpLAVR+1wVPB9n190v
                77Eq6T+KPKRlTvCwv2IzPe5nB3KNMHvW4KmUZgQTej9UCZUBXkSpibTyxplUm9Jt
                usGEnKzaz0QiqTtyNLpKG0Yl7ErOexSy5RXvkLbriP2xZJXUhkNjeMW1Sk2tWhbi
                UXppabHommx4yEiPDi69k0Wibh0kdKNbApsGDkA3JKmzCP5HZx4OvAGARFrao0+/
                KqWbTiXjp9UZa7RxKKvKvLAaWX40qLjt/DoPj39/czjE5e9g
                -----END CERTIFICATE-----
                """;
        String pkcs1key = """
                -----BEGIN RSA PRIVATE KEY-----
                MIIEowIBAAKCAQEAxMj9ISyb8s7vUed/NGTspsOMzlFNU1dUcxs5q9rx75iIlRSV
                zqLUBAzJx4oiHiU8xknKejkAtIyGzSjJ0Wba7I65RxCFevPZ+19aPGunnqQfBG1M
                1iu4M7Ur8qKV5oJnPFLldTj8h157b766m6kLYfLrT2e8CzHdO+49Iy/GpaeTWAcB
                2Y7lRCQE7BzgWo73H0xcvcQExpHfX1U6wX2ypkj8n8q+hZVcUACLP4hlFbhT9v3x
                YzRdKq+KWSPNz9xqwY6MU6bK5E/y70Ws8FNk6m5WnxvfRKriA4NeHCrN4p1Tl5dK
                zVgCh55qXTB07wEhqJPS4Cugp/oJsvDoTHUFkQIDAQABAoIBAHWFXv7H6F6KzQwK
                B8LfnyE82cOBdH+YQPv5P2jt+dzQKIl34LumLJ46kOrVCOkd7fyxpvV1Q4GQBxVR
                Da48EfSCjA3vxPeX2A+yc25wEIvCki+Povqo43ol8ZgXFxSxvjudeiGWpeGmQGuM
                6V77Bqw5aeRjHD9HSUt4TRTNfIIC0u0DlGCK0ayx0VabrfrQVd/pso2vy1Rh9+v/
                KS86sndkt5rUk1y+f66daZbhvUUo1P7rEDm9JlXCusEjAN3cbK5N8/KTKuM7JCPz
                YNXG1LUpWnhC77IsIpr/IJz/thJHHkK0TH8ATiIfdDiOUo5/85vuPHS+yWueT4U5
                pICqOlECgYEA/gD6JXp4Gdc1t1BSVBvO9rccZlKw71Jm5a8M/M/yVP+RazNYMMtT
                6/x8RhKI1zFSDxexel3QNzORyhvK7tKWgtoLx7GBJ2Das+C1VeghKn144PBRlVKS
                qD2VL11MdSlqaT5IBQDQdle9cOtTUbj0HApfOwASjIjS1tau3zvw3WUCgYEAxlTl
                H3qp6ktwVXfCA8m1AIQ8sBos/5Ev+iCHNX8vnRVOu3bYnWdbyfDcf2MK69GPtDRm
                iRuONIGNNzlaMTiW76naab02Sd6O1Np9PFg2Pbjj9X4J/WlDLIijbjJsLTbTnNJx
                Ajy2Pc5RrtwuSdRBlUEoPDTRuVo84xQv61CjKr0CgYAiBWt73Z18iJDPTtjYHyln
                bjtFkUnVpEnX9cxdiFTDAcAuhaAIWROShiz3DcB6YkbFcrz40Nv0qNmcIoDZ4Cb8
                u8vcIXBVH+nrMevX59GZaVjpeAbVY0v1cNosj+iPwM5Z7lejI+rIDxR2rW5pgcZg
                gxsiQe46GJF06ShfCh5G6QKBgQC6XaXb2bj216KcPWeLhavW62vXkxE7qaNqp8xF
                4djNA+uyzm+oISqobPPVpCzsAUTG8gJzzffnsOEQxozQ+bsjQ/lgKRNtgHu42gKa
                hM2bXDShR6l9fb9IhrED6M+sAPwCPeFBV+lr1Ib/CBla+OATr2FuibeVM41JWHva
                5DAfBQKBgD4T6eKBoHLWeYDyOgvOlkO8+wDbcl0n/A//V4kUsbK2BDZMv8Sh/cUk
                qnDdt2RRkggbnbThjREcbsNWtyPyVhlfJwjwCN+K257LsGimFk32p0kbVXdJX4Ec
                aPRFC2d8CUEwafV+OHBHFzlFlZIn2jkDR3guL8FfEd8MPw29ImWz
                -----END RSA PRIVATE KEY-----
                """;
        test(pkcs1cert, pkcs1key);
    }

    @Test
    void testPkcs8PrivateKey() throws IOException {
        /*
         * generated through the following command executed considering the ca-key.pem generated with the commands described in testPkcs1PrivateKey:
         *    openssl pkcs8 -topk8 -inform pem -in ca-key.pem -outform pem -nocrypt
         */
        String pkcs8cert = """
                -----BEGIN CERTIFICATE-----
                MIIDIDCCAggCCQDfvekeOeeawzANBgkqhkiG9w0BAQQFADBSMRMwEQYDVQQKEwpN
                eSBDb21wYW55MRAwDgYDVQQHEwdNeSBUb3duMRwwGgYDVQQIExNTdGF0ZSBvciBQ
                cm92aWRlbmNlMQswCQYDVQQGEwJVUzAeFw0yMjEyMDcxNjI4MTRaFw0zMjEwMTUx
                NjI4MTRaMFIxEzARBgNVBAoTCk15IENvbXBhbnkxEDAOBgNVBAcTB015IFRvd24x
                HDAaBgNVBAgTE1N0YXRlIG9yIFByb3ZpZGVuY2UxCzAJBgNVBAYTAlVTMIIBIjAN
                BgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxMj9ISyb8s7vUed/NGTspsOMzlFN
                U1dUcxs5q9rx75iIlRSVzqLUBAzJx4oiHiU8xknKejkAtIyGzSjJ0Wba7I65RxCF
                evPZ+19aPGunnqQfBG1M1iu4M7Ur8qKV5oJnPFLldTj8h157b766m6kLYfLrT2e8
                CzHdO+49Iy/GpaeTWAcB2Y7lRCQE7BzgWo73H0xcvcQExpHfX1U6wX2ypkj8n8q+
                hZVcUACLP4hlFbhT9v3xYzRdKq+KWSPNz9xqwY6MU6bK5E/y70Ws8FNk6m5Wnxvf
                RKriA4NeHCrN4p1Tl5dKzVgCh55qXTB07wEhqJPS4Cugp/oJsvDoTHUFkQIDAQAB
                MA0GCSqGSIb3DQEBBAUAA4IBAQABqSAp4fhsMq5D0e0YjktPN9urmAZCkyhrTczB
                SIqARe+JT9prs9B1damOh+N80Vf4fI47c5TLAVrHt6aXfwJpLAVR+1wVPB9n190v
                77Eq6T+KPKRlTvCwv2IzPe5nB3KNMHvW4KmUZgQTej9UCZUBXkSpibTyxplUm9Jt
                usGEnKzaz0QiqTtyNLpKG0Yl7ErOexSy5RXvkLbriP2xZJXUhkNjeMW1Sk2tWhbi
                UXppabHommx4yEiPDi69k0Wibh0kdKNbApsGDkA3JKmzCP5HZx4OvAGARFrao0+/
                KqWbTiXjp9UZa7RxKKvKvLAaWX40qLjt/DoPj39/czjE5e9g
                -----END CERTIFICATE-----
                """;
        String pkcs8key = """
                -----BEGIN PRIVATE KEY-----
                MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDEyP0hLJvyzu9R
                5380ZOymw4zOUU1TV1RzGzmr2vHvmIiVFJXOotQEDMnHiiIeJTzGScp6OQC0jIbN
                KMnRZtrsjrlHEIV689n7X1o8a6eepB8EbUzWK7gztSvyopXmgmc8UuV1OPyHXntv
                vrqbqQth8utPZ7wLMd077j0jL8alp5NYBwHZjuVEJATsHOBajvcfTFy9xATGkd9f
                VTrBfbKmSPyfyr6FlVxQAIs/iGUVuFP2/fFjNF0qr4pZI83P3GrBjoxTpsrkT/Lv
                RazwU2TqblafG99EquIDg14cKs3inVOXl0rNWAKHnmpdMHTvASGok9LgK6Cn+gmy
                8OhMdQWRAgMBAAECggEAdYVe/sfoXorNDAoHwt+fITzZw4F0f5hA+/k/aO353NAo
                iXfgu6YsnjqQ6tUI6R3t/LGm9XVDgZAHFVENrjwR9IKMDe/E95fYD7JzbnAQi8KS
                L4+i+qjjeiXxmBcXFLG+O516IZal4aZAa4zpXvsGrDlp5GMcP0dJS3hNFM18ggLS
                7QOUYIrRrLHRVput+tBV3+myja/LVGH36/8pLzqyd2S3mtSTXL5/rp1pluG9RSjU
                /usQOb0mVcK6wSMA3dxsrk3z8pMq4zskI/Ng1cbUtSlaeELvsiwimv8gnP+2Ekce
                QrRMfwBOIh90OI5Sjn/zm+48dL7Ja55PhTmkgKo6UQKBgQD+APolengZ1zW3UFJU
                G872txxmUrDvUmblrwz8z/JU/5FrM1gwy1Pr/HxGEojXMVIPF7F6XdA3M5HKG8ru
                0paC2gvHsYEnYNqz4LVV6CEqfXjg8FGVUpKoPZUvXUx1KWppPkgFANB2V71w61NR
                uPQcCl87ABKMiNLW1q7fO/DdZQKBgQDGVOUfeqnqS3BVd8IDybUAhDywGiz/kS/6
                IIc1fy+dFU67dtidZ1vJ8Nx/Ywrr0Y+0NGaJG440gY03OVoxOJbvqdppvTZJ3o7U
                2n08WDY9uOP1fgn9aUMsiKNuMmwtNtOc0nECPLY9zlGu3C5J1EGVQSg8NNG5Wjzj
                FC/rUKMqvQKBgCIFa3vdnXyIkM9O2NgfKWduO0WRSdWkSdf1zF2IVMMBwC6FoAhZ
                E5KGLPcNwHpiRsVyvPjQ2/So2ZwigNngJvy7y9whcFUf6esx69fn0ZlpWOl4BtVj
                S/Vw2iyP6I/AzlnuV6Mj6sgPFHatbmmBxmCDGyJB7joYkXTpKF8KHkbpAoGBALpd
                pdvZuPbXopw9Z4uFq9bra9eTETupo2qnzEXh2M0D67LOb6ghKqhs89WkLOwBRMby
                AnPN9+ew4RDGjND5uyND+WApE22Ae7jaApqEzZtcNKFHqX19v0iGsQPoz6wA/AI9
                4UFX6WvUhv8IGVr44BOvYW6Jt5UzjUlYe9rkMB8FAoGAPhPp4oGgctZ5gPI6C86W
                Q7z7ANtyXSf8D/9XiRSxsrYENky/xKH9xSSqcN23ZFGSCBudtOGNERxuw1a3I/JW
                GV8nCPAI34rbnsuwaKYWTfanSRtVd0lfgRxo9EULZ3wJQTBp9X44cEcXOUWVkifa
                OQNHeC4vwV8R3ww/Db0iZbM=
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
