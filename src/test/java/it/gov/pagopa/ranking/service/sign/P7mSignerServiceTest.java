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
                MIIDIzCCAgugAwIBAgIUMOhnsZiwScrMf1dt9j/cdQHFcb0wDQYJKoZIhvcNAQEL
                BQAwEDEOMAwGA1UEAwwFVXNlcjEwHhcNMjQwMTA0MDkyNDE1WhcNMjUwMTAzMDky
                NDE1WjAQMQ4wDAYDVQQDDAVVc2VyMTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCC
                AQoCggEBANA4y+4dejV/gJSSoo81nY4okV3cPcowBnSHWIDGUVrmRH8wQPII6tPk
                PIDi3xx5PcNZ37InkGRwmm8qvE6gnUMOHvdwnwyxmWjVu3Wi91zXdy1yL6xMrFW6
                X4e3bFamQebn1xVysQu8x0fKrLgJKMuWs8FhsM+cZ50ey8Nwv6SEuIKff228a0Q5
                xJ1e0Hrm0adYyCyu7HeeZVUG/LUS/HhnwOpcBccp+N/mP3LCRtQiLVKzbY0yFNp6
                THJNXJ46Ihw/XY+bToGSc0Un1aG+rhj+KS+Mp9GjACbDqt0l8ctNb1W0/fI/mNi7
                xp/ySh5l2DhV4frg/Ur5NEbWl7zwk1sCAwEAAaN1MHMwHQYDVR0OBBYEFJ7rAGw8
                STLpuMDr7rNtn+VIiljlMB8GA1UdIwQYMBaAFJ7rAGw8STLpuMDr7rNtn+VIiljl
                MA8GA1UdEwEB/wQFMAMBAf8wEwYDVR0lBAwwCgYIKwYBBQUHAwMwCwYDVR0PBAQD
                AgeAMA0GCSqGSIb3DQEBCwUAA4IBAQAReZ/vBQqIgejtSY6w3CqoC9e8+uPGehK6
                UA7gcAMa4BdFPlekZQcRqQ6zwa5ln6dMC7EI6rak9MY33HhQFFYpqEZ9vgdveq8f
                JWw/zxiJM6VcGSMUCUZIXKGB97uCsgRabVLuApE7RNX0DN799RDsgG2+/7bCAuac
                2HI0IPX0O3tcILF1M8gRUSYvl8ujRy62v8l7Ghim0DurEnZ0MQYHqg5H9/I33A8w
                b7A33+qXLrmkU9VzbcHITGGWhXiiu2iDrXTf/AFQ3gQx/PyFGP5r+eBliRq2s8/3
                wuSCCEvki4z3sJEIvKBxs/eNa4AC1HDnMt4vg6vZog242VX1k8GG
                -----END CERTIFICATE-----
                """;
        String pkcs8key = """
                -----BEGIN PRIVATE KEY-----
                MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDQOMvuHXo1f4CU
                kqKPNZ2OKJFd3D3KMAZ0h1iAxlFa5kR/MEDyCOrT5DyA4t8ceT3DWd+yJ5BkcJpv
                KrxOoJ1DDh73cJ8MsZlo1bt1ovdc13ctci+sTKxVul+Ht2xWpkHm59cVcrELvMdH
                yqy4CSjLlrPBYbDPnGedHsvDcL+khLiCn39tvGtEOcSdXtB65tGnWMgsrux3nmVV
                Bvy1Evx4Z8DqXAXHKfjf5j9ywkbUIi1Ss22NMhTaekxyTVyeOiIcP12Pm06BknNF
                J9Whvq4Y/ikvjKfRowAmw6rdJfHLTW9VtP3yP5jYu8af8koeZdg4VeH64P1K+TRG
                1pe88JNbAgMBAAECggEAQvUIqvUCsAJkxCIVMS0iEa089axVmFdyM/W0AZEEZ1El
                1KqdL9erP+EkjaOm8F6SfGfBteXgGlRVbQsWi8g9WetIDPTDyI6x0NLuGamClvHY
                qp4N83+iaVzpzFgw9/o1tf12njiH80jM4mqruPCFFFbNThHOjCx060MUbjArCElq
                7GF73NUReamfgYwQ4fr2ghbptSlBVOd2HvY48wNkRJkFVKfY9Noi5ZPtzuv8JKNi
                uqTlPl1ceELCMm1e32zq7ZLP3OkonZqjoqOzb0d8w3mxG5twq/+gjBRVnkphy3FV
                4WdPEGq2HlpgB8cPvNW84BG+CwuXelkwEzX9n+MR/QKBgQDyV+CiNuOzhwUB7cp/
                cuOMmMBWzOtm11F/5bK3xA1sNOs/WBq+Zi+jfXKrr9Vdq6mRXNZxCByha7RvBxNE
                SuFYPzOc1Kppg44cPNunity+IPV8GcpyLpiV6TNSBF1sDXPVbBRSuVl4KgsJYPT9
                s+hnJ3dZKDcJfUn9wH8GGo+4LwKBgQDb9KwwzXgG43zEo7dxCHOzMloth7FRt+32
                0ZdcQWo/8G5iu6g9GYadUyj4J8BPlVRc8H4ZNwLkR8h6JGHw3HgR340qbx3k526P
                Ues04taK6MDGVfT7cIQeexd/FdLp2a2WW0nST0645oOFo7oAdzoR2doz2sO5BeNq
                05k+5mOglQKBgQCHVRe2vpxNxxq+xwIMRiZYf3z5fBoBJkD3AVjaUnuEb9DnYNYe
                eYYk8Is24UxvsMtSx9tg57zFUBZE4liaDI7m7vRtoj1ACx0zG7E2UM+QxWsRUH3D
                Yc4H+WCVbqaVDrYe+uomgCc8eY4MQL7PZsZjxKBbg5+ohvTo5bSJvg+ANQKBgHt4
                tLRMkwUi7GYiFRbBjNlZ+Z83XcWmv1FfRF1IZ1FvMTgBaaaqzpNAHG55Og4yoIHv
                iomzBM+KsDJIfCZOTKST3lWkvJ5DgB+595PfhlnB4H01wWjoN3I43O69HIGjbTtL
                EXyagnFvDQOW7hHW2+Urar7THi2d7ZSJJaxWfjSlAoGAFc0rtkIY0D4bw1O5II24
                xO6q/08PBkHnX+GXQq3zH9WLOdQ1Vqx5Y7/ssLtkENW+QeLVwCJvjQu/5TdTiF68
                Cv9luVG8+HKKkeSVUAWmLKJtBMDggcWbGMaLDwEHb3RsQkiMJAGkmPYzT8H+Tki3
                PA6pVB77irbtdbocsugAWu0=
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
