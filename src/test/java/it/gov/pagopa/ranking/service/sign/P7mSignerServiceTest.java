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
    void testPkcs8PrivateKey() throws IOException {
        String pkcs8cert = """
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
        String pkcs8key = """
                -----BEGIN PRIVATE KEY-----
                MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCsM4kqzDAJop1g
                ltodnPduAQiXvPUCtmk0jnhkZpdxGK2LCOv9b196dbr2YbsgCUhO4+1oi7iKCBdN
                xJG5q6Wt6zYS+4/sqMvS1+jH8+y2poxnMsXzA5h8kTiInx99xAvrssnf9bCfYWFh
                VX7gPYBhbxA91GbjTockbts0ouZ0z0AVRCnrOhO816DJZDIB8fjSLvZSimW0O5tw
                DKQPpiCRFy0K0p3yH8FGkhG0yfQAYsz+jmrBi754IDLz/s+mPcfMLAeQM4lp3+Da
                lGPiygl2+fms/pMzg5W1ntV6HGKieRpxwE9zIMZKsijnzftdhoa/evtGfSsKmV/G
                dTga2Z3nAgMBAAECggEAEC6FmMJ4Tyd7T3zNgVPjQnCRbKTihz858qjislibqZKO
                mE6d0oJ5P+o5R/bWHUQSCevMPvNGQ55QBkxO/1ocZxP/0FfYZf5UrPsCEmwfFejf
                r8DrLhNr7GS/IcOGM4zNK/hwlP2i+88sVfexRQQygLVtmsnPY1PZSjiqm68lJdu+
                aP8TYM10y1aeiYnfuUYvnvXJFXeTEockhaUJTmeIQNbbUy+pyJ0mAPASPtXRLr8h
                UflutICnWcx4v/qkCn1jmHw+NMA4q7hOH7UuOAqj53FqGMN+IWfjMmmYoQ7MVURx
                8CrnEtlCOua+C8EEIFL2ylvV7X0cv/DqCJLVQoegsQKBgQDLzMaAjNgD8xSXp+Gj
                beeUsSGptEaGMuA89AzyTnCyvU9a1HGwDAghoQPae+pVk7R5uokojWkBVzP/kKxv
                ZldGwPOegUUdBLS4yJML+OkqtoCgf3Mbcozm5dVYtx7bYdhh3PswzRmn/h/YjEAz
                +/mxi6dJir0k0Nd4YNtQbzBctwKBgQDYTtSmJvVQdOHnzqA/LRmMF1l+HaqLuDfu
                B4rDlxCdDfOAvHqz+3YapP3B4MQuz29TSDqwAnzoN2XZX5B6g/jKauWpAwZkFXuO
                fqcfNG/+MewTcHIYNm+EtgXtIsnCXDfAeXdQapzNsOX+XSF/aWcgGHg18xOBPt0R
                7Aoa/h34UQKBgQCsCzGjwcJ2CxXeNPYxfg1ao/HUDoDet0I/kpL/VqKi8Vd1SRS0
                VmPi58eWALfBCJD5ljRFjKMRY6lc3KgE3vNconTG4UAUEC30NDaWi8liqnCJjS4C
                BMDYBzwEyYn+D2qYqvFOsEYxYEFIEJX+jH+sl0VguwOTec38LF/YVhUQnwKBgG5u
                2Kw3SZkZA1ioqjF24gsexKbZmH+avps8qICw+F9mhwIbt/15jVOPFqrMCPzpFKoN
                P0ErFAAugEYZPxb9l6AoMTY3gCTKvvkB+mq5B9BcRm2qQ+XOrOKxV5c44o7jK+eN
                W/fnZkSxYsqZW4fEFU1SkNTiU/vxT0ZeHs6nHD/xAoGAOIqaqQnJfGj/wLo3Z9o5
                /Oxu1zTPGZC6SqpdygCjlQ0kQ8Bp0LV7nL06/VCHAHI2lF12xApRnFk7GY3xyqK8
                nYxeRASCj3GGmLupGshtfCtDBeysE2h7kj3Bo0d6g1Ye+j8BUZuZaZm6WNlo7cgE
                NLHn1k0IpmXFOiFa1Y1D6Bc=
                -----END PRIVATE KEY-----
                """;
        test(pkcs8cert, pkcs8key);
    }

    @Test
    void testPkcs1PrivateKey() throws IOException {
        String pkcs1cert = """
                -----BEGIN CERTIFICATE-----
                MIIFMjCCBBqgAwIBAgISBIekm4ZF5yulsQgFoGUpONXuMA0GCSqGSIb3DQEBCwUA
                MDIxCzAJBgNVBAYTAlVTMRYwFAYDVQQKEw1MZXQncyBFbmNyeXB0MQswCQYDVQQD
                EwJSMzAeFw0yMjExMDUwNDAxMzNaFw0yMzAyMDMwNDAxMzJaMCExHzAdBgNVBAMT
                FmRldi5zZWxmY2FyZS5wYWdvcGEuaXQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAw
                ggEKAoIBAQDbE1LiuuAAfJNJV9aTYxmhF7XXFQFL1OGNibayk+j7ncMUqKC8IS/C
                rckK2HRSKK8O+ZfV7D3UpQCXRmNLDREkg0jA6CcckEVrqjh1vbErzSv6nKubjUzX
                64woJde3ZMuQ0/3zheAUSygtOl2KaPJ8WabmcfkaFUvYjO7Da+qsZ4JHq9j7hEtj
                zRBHVcCMAXomLlEbvMyS0otWfBIrJc+C5QRjW6po7ZxCFFVbLxUkSahi9nmcpDf4
                ZfXPvVTBPaljxhJT8M3p0E4UU5sNVsyJ0LF+reAM5Vymy5F3sJzFGS9ripKXz7qa
                GwKGR5b5lbzDqolsxG2tprMbMEbnBPO5AgMBAAGjggJRMIICTTAOBgNVHQ8BAf8E
                BAMCBaAwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMAwGA1UdEwEB/wQC
                MAAwHQYDVR0OBBYEFK6YbzUW5zKet37UqpMML0QALaB4MB8GA1UdIwQYMBaAFBQu
                sxe3WFbLrlAJQOYfr52LFMLGMFUGCCsGAQUFBwEBBEkwRzAhBggrBgEFBQcwAYYV
                aHR0cDovL3IzLm8ubGVuY3Iub3JnMCIGCCsGAQUFBzAChhZodHRwOi8vcjMuaS5s
                ZW5jci5vcmcvMCEGA1UdEQQaMBiCFmRldi5zZWxmY2FyZS5wYWdvcGEuaXQwTAYD
                VR0gBEUwQzAIBgZngQwBAgEwNwYLKwYBBAGC3xMBAQEwKDAmBggrBgEFBQcCARYa
                aHR0cDovL2Nwcy5sZXRzZW5jcnlwdC5vcmcwggEEBgorBgEEAdZ5AgQCBIH1BIHy
                APAAdgB6MoxU2LcttiDqOOBSHumEFnAyE4VNO9IrwTpXo1LrUgAAAYRGKmNBAAAE
                AwBHMEUCIC9iphDzVrViMG6/iNQpdsID/ignfZSXSx87zjskToXVAiEAkOKPKr7m
                mQZ8MAvT3Rff74KoiNeqyV8+fjpjfdaNTs8AdgDoPtDaPvUGNTLnVyi8iWvJA9PL
                0RFr7Otp4Xd9bQa9bgAAAYRGKmUVAAAEAwBHMEUCIH+FpFfE2Ze2PLP5ZTtyN9uL
                GE8fI/+bldAT2qdBOs6qAiEA+FnRFY4V/z9RSQz/9ggY1G3xX8u5XYQqCJNqTGkJ
                okQwDQYJKoZIhvcNAQELBQADggEBAJe22ItXxNzpOwctjIeIdLQ6JF/8l1+cD/5x
                nyD92b/z4MuDEFb5bFUTVBYyZXcwtjoLUWNeDioHd6of4JULWCRb8bPkZz0u/BZS
                cNSsqBNn7oALfCgMjhpKiGfj68CHdu/dVoQanV2Xq3Sf1c1tQiCk4a5tSM+ND0/+
                3uzo+/GxAklPVfuDg2RypBFY5PHFXawNqjH+Y/uwaDM83PSG9UbpNkoWXAMHp6pF
                vkhIXsJ18TAjcjwxZQ/Y6zezRSNS6YiTwPv4PkpC0UDcROqtXGP+5ie5Q9u67Fey
                PRm/ZEPXQjYLtRgis8ULegXRQqPG/XHE6R2g9V9R5er7MUBFPXQ=
                -----END CERTIFICATE-----
                """;
        String pkcs1key = """
                -----BEGIN RSA PRIVATE KEY-----
                MIIEowIBAAKCAQEA2xNS4rrgAHyTSVfWk2MZoRe11xUBS9ThjYm2spPo+53DFKig
                vCEvwq3JCth0UiivDvmX1ew91KUAl0ZjSw0RJINIwOgnHJBFa6o4db2xK80r+pyr
                m41M1+uMKCXXt2TLkNP984XgFEsoLTpdimjyfFmm5nH5GhVL2Izuw2vqrGeCR6vY
                +4RLY80QR1XAjAF6Ji5RG7zMktKLVnwSKyXPguUEY1uqaO2cQhRVWy8VJEmoYvZ5
                nKQ3+GX1z71UwT2pY8YSU/DN6dBOFFObDVbMidCxfq3gDOVcpsuRd7CcxRkva4qS
                l8+6mhsChkeW+ZW8w6qJbMRtraazGzBG5wTzuQIDAQABAoIBAGldQeAKQPH6+Fuu
                A2WfpBP2c16em6D9If4Q3e3FdaiOl+S3tZbF31Kigq7UrB1Xs9pf8MbSeGptcXtQ
                PzE5d6U3/yXIggca3EiL9durACsw4pYGBAMsUmkGwMIOcVGjCMT/MISPPtaDzy7x
                g0uubfGgrMTozAXfeDsOjUcwAA4qVo6aST3w8GiPZ7BHyld4/2UkEZoz6L1I4Nko
                O48oWqyS0RQqb2QdzQ1rWKY9vnmKAT9Xn3gKVqpt+rSSE6/iSNYmrR0kYFQbxbAF
                Lmc0uWsH/14qc83G2vsKr0qMSV4rCDn3i6Kv6odpxBpr90mUrsCvMDgqY26bTbHx
                pAUxyIECgYEA70GpYmpGtd4zhfFNZVwKeAG39/8mNxlaVtyYMMIE7WrmfaIjp/6F
                1k6wTWnnAtTmY79q4i7rAYwhla3YHYAo9OUHBmiFoY6oqkWjepFFafRHbca0swy9
                whOzCrgsXvjgvsFoGxggucGtga29G3XQEvCJcLfHdm+kU1cyRqa0mgsCgYEA6mgd
                TIbkUoVXZOnKBjTgNiMied93oc9aVPJox8Apo87aa2ihPUtezBjgu6Y/t2qbtVvf
                B1FQnSyN4pSqq+7GRlwNxO+x4t2n0fk5Gy+h4jI+VE1V0Eerj6S5D0hCU0jOWX42
                sYDCY2rYfOnS2a7Ra8ub5CXT8AWfH3aMPziZh8sCgYA5WGec6xQfqy52QCvoy3LG
                6MKLcmBafH0mrKDdruM3dJPiMOaS8XJqt1mSdHumNhkyd0LLC6vQJyI1mgFesMUg
                TXStmIfO/T538VEJo6AbkS09po7RC5C/37DeNN6NtOFJV1n4hIPzgfRwTJAy8L36
                jCSlG6d3P7MHNrMaNAK9mQKBgQC9gwlVCqXj1awviRhtZihzfHpuH+5/or28efOe
                s0y3BBDwz7m0bZAp4F8pNyLCbm2O9cTeDK7pM9FOg7VspfIlhl0XgFbIPT5l82Fp
                ClyOHOLpk1CywI8C5kE8CsacwPsbvYAIiDog2IxIiV6P4desmAPkM3Ui09ivPk/q
                +R6fewKBgF8+fAln+9+txYNTnL4Fr1ogKuzcX0VnDoaitibv0GWC6r1GqPQeIdid
                xVVzPSJsG7VAxA7u/HFFXAkze14v5IsX0namuZ15FM8gXeKjxcC9kQB3owLvyryC
                M+cZ4fghCIO2UhDVyJmu3SXWp805X2IgOgFxe9SM0CPxauwyK/P1
                -----END RSA PRIVATE KEY-----
                """;
        test(pkcs1cert, pkcs1key);
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
