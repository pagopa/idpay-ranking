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
         * generated through the following command:
         *    openssl genrsa 2048 -traditional > ca-key.pem
         *    openssl req -new -x509 -nodes -days 3600 -key ca-key.pem -out ca.pem
         *
         * If you want to transform a pkcs8 into pkcs1 execute the following command
         *    openssl rsa -in privateKey.key -traditional
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
         *    openssl req -x509 -sha256 -nodes -days 365 -newkey rsa:2048 -keyout privateKey.key -out certificate.crt -subj "/CN=User1" -addext extendedKeyUsage=codeSigning -addext keyUsage=digitalSignature
         *
         * If you want to transform a pkcs1 into pkcs8 execute the following command
         *    openssl pkcs8 -topk8 -inform pem -in ca-key.pem -outform pem -nocrypt
         */
        String pkcs8cert = """
                -----BEGIN CERTIFICATE-----
                MIIDIzCCAgugAwIBAgIUZ0wvLaDDvte7Wg8FzD/INK03PfkwDQYJKoZIhvcNAQEL
                BQAwEDEOMAwGA1UEAwwFVXNlcjEwHhcNMjIxMjA3MTgxMTI2WhcNMjMxMjA3MTgx
                MTI2WjAQMQ4wDAYDVQQDDAVVc2VyMTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCC
                AQoCggEBAMBIMugJZJScReW54lywzoL6VPCuWUBmDlW+1/CRbPky0fnSaTxjRv4d
                GTpJj0knUAYFabrL1KXRtri0rjH3QXdMtlHutUMqD5Vlf+l3eJeiYk1HdKRy2PHL
                0NPpkAB0bwCFlaSMmR5MiGNr3jE9Kbir3yWAoRa0Xh2mD/vuO9Yd0jMLC5eNzQ8k
                duT2HZRyVsBUF0ahdt4iTgk/qOsZbnZhgQTS/gCZu39R0DxDh+MKrdYqWvDT8fac
                ci2GKYf+OQ3pO4ANKQgsfNtZn+e5dyREQcpW+6ehwnpwutO+HcPGnqBxGy/zn+so
                RQ3vPpoA6wpHpG8gnRqq0eRqWfcWOfUCAwEAAaN1MHMwHQYDVR0OBBYEFKzHn9uw
                GMuLTzMUOO3eJj4JAM+JMB8GA1UdIwQYMBaAFKzHn9uwGMuLTzMUOO3eJj4JAM+J
                MA8GA1UdEwEB/wQFMAMBAf8wEwYDVR0lBAwwCgYIKwYBBQUHAwMwCwYDVR0PBAQD
                AgeAMA0GCSqGSIb3DQEBCwUAA4IBAQC6GAtCnpKrPjKNzTUNQIOXCZO8CIA/B7s1
                rn/rVufHIM6CwJcHcNRpVUgKsUB0N7x4HKUB6IjJdmsqV1QMd/gzdxdIFF++1Ror
                EgclqEFFVVF+BwLVyXH3cTANut8UJPydY8Ww75jGz07Qd9UvxYBB05II6C93Eae8
                PabZXDMeV/Y9nQgw6SEhUsYawSRWBpIGtbZm3fb0Ycz3FMSHwdakh7s+fgNIJVNx
                NjUQWbaF5pD9NcUPPe6/34qUmjuIj1sSX3EehbfgQ7N29JWAlKp7ZvKOWmYgiVUl
                qqeCE00xKwa/EN/GnN03AibJ+LYaleSPmf/QglTLKAUmhxU7oBGX
                -----END CERTIFICATE-----
                """;
        String pkcs8key = """
                -----BEGIN PRIVATE KEY-----
                MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDASDLoCWSUnEXl
                ueJcsM6C+lTwrllAZg5VvtfwkWz5MtH50mk8Y0b+HRk6SY9JJ1AGBWm6y9Sl0ba4
                tK4x90F3TLZR7rVDKg+VZX/pd3iXomJNR3Skctjxy9DT6ZAAdG8AhZWkjJkeTIhj
                a94xPSm4q98lgKEWtF4dpg/77jvWHdIzCwuXjc0PJHbk9h2UclbAVBdGoXbeIk4J
                P6jrGW52YYEE0v4Ambt/UdA8Q4fjCq3WKlrw0/H2nHIthimH/jkN6TuADSkILHzb
                WZ/nuXckREHKVvunocJ6cLrTvh3Dxp6gcRsv85/rKEUN7z6aAOsKR6RvIJ0aqtHk
                aln3Fjn1AgMBAAECggEATJzXz7ViYoYroX9hjn4SIoTc0DMfs7WWM5sBTkSbb2VM
                3aX1MU77K+frM9q4YTTtQSDqAjgR5+n5zHmNSLJUXtTtdhLPHU5GfEe/YgYswfo9
                Ab6dXqK2Bw2nDLBspIm/6qzPNYuhvL1QwJBrrSHKHF8636XzSWkfcN/IUaCYLdTt
                6VSJLUU1v69fiy6bZ2z2ySRK23LRDfnJLJkOYGlRJCFAx0KXg1ta3AjvhmQf/hoG
                rK7yeYCHzdbH28vbdC5P0s5E1qVE+JTbPF9EnZqit0kMcMEqsYZBz0WLc059pVGH
                +r85hqUnrDQ4Ex6A7/4HNMk7J8sT61ZOI9QorliTNwKBgQDqU+Gp9HwaXaL7y1GN
                z46laUj7O5AKCoyu9tZXfSEpxP47KHNqJEqebLu/ptsmjU+7Ta52jw0DR0xmUJIa
                FQ+w/d3JocQo2iW9FdZAWHSas0Wa9dDgZzCUm2vY7tgWHgSs1xFILzsfp0xS9PgS
                qU1xc4k0pydMg/5F+Ks+r/dPawKBgQDSENAsGIYcvcxolxXDtD00Iq+P8g1YwqKn
                aoFn9rmfOJtYo8cVLV9ccy3y03qF7hSBtvFR11QlseFjwLitOtjObsb74ZaEhtmS
                BSn0G2kwyyHwITUl0XqhsTPnYXrmaOizW85HvLXe2lZ10TJOpT85qReaKFUeqfSs
                QJXIod3UHwKBgQCVMekpa9ekdd/yz4ZSc0eQe9OS2l6gdg0SzWi1dZ8q2BlCk0PA
                3fCApBx6LwOzrR+J0zD0naocX0X+kugjISvHdivDWHLry9FhbcjnWSqM4P29Zyuh
                5TGiPL7S9Wex1VUGszx2qFPNmJhY7U4Rm6gKRxSh6Jd1+UhpRqXJmQIulwKBgQCB
                2ZDE4TfthklXkaULf1uh4ZsCcM5dQpsGv+hUGogtavFj/oEujwh2fmA1zRHcvgmB
                EVPkkiVa07UOU3AU7N5d5M4tnwnKzAyrnXOMiHEijz5gUDapNO8ICiCac4Bj8w98
                51AAuh72LaLqWzEsuir1+pczXKEZPleXLqkoBx63YwKBgGJiXmbN9mxtf+rqYjpA
                fYEYcVcm181GKv5y5kdDVlDHvylEye79NWzQeO5fMjBgg4DVllJKv0JPeC/VsIqM
                BqJfIzCFKBXthV02fm7oacpcsTlgoc/wlOx+mhXOsnDRIOz3Hs6mOMAPjgSNqmE7
                rsdP7V0364qw8+r0lRZ9NsjU
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
