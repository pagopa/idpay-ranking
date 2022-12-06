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
            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
            -----END CERTIFICATE-----
            """;

    private final String key = """
            -----BEGIN PRIVATE KEY-----
            MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDbE1LiuuAAfJNJ
            V9aTYxmhF7XXFQFL1OGNibayk+j7ncMUqKC8IS/CrckK2HRSKK8O+ZfV7D3UpQCX
            RmNLDREkg0jA6CcckEVrqjh1vbErzSv6nKubjUzX64woJde3ZMuQ0/3zheAUSygt
            Ol2KaPJ8WabmcfkaFUvYjO7Da+qsZ4JHq9j7hEtjzRBHVcCMAXomLlEbvMyS0otW
            fBIrJc+C5QRjW6po7ZxCFFVbLxUkSahi9nmcpDf4ZfXPvVTBPaljxhJT8M3p0E4U
            U5sNVsyJ0LF+reAM5Vymy5F3sJzFGS9ripKXz7qaGwKGR5b5lbzDqolsxG2tprMb
            MEbnBPO5AgMBAAECggEAaV1B4ApA8fr4W64DZZ+kE/ZzXp6boP0h/hDd7cV1qI6X
            5Le1lsXfUqKCrtSsHVez2l/wxtJ4am1xe1A/MTl3pTf/JciCBxrcSIv126sAKzDi
            lgYEAyxSaQbAwg5xUaMIxP8whI8+1oPPLvGDS65t8aCsxOjMBd94Ow6NRzAADipW
            jppJPfDwaI9nsEfKV3j/ZSQRmjPovUjg2Sg7jyharJLRFCpvZB3NDWtYpj2+eYoB
            P1efeApWqm36tJITr+JI1iatHSRgVBvFsAUuZzS5awf/Xipzzcba+wqvSoxJXisI
            OfeLoq/qh2nEGmv3SZSuwK8wOCpjbptNsfGkBTHIgQKBgQDvQaliaka13jOF8U1l
            XAp4Abf3/yY3GVpW3JgwwgTtauZ9oiOn/oXWTrBNaecC1OZjv2riLusBjCGVrdgd
            gCj05QcGaIWhjqiqRaN6kUVp9EdtxrSzDL3CE7MKuCxe+OC+wWgbGCC5wa2Brb0b
            ddAS8Ilwt8d2b6RTVzJGprSaCwKBgQDqaB1MhuRShVdk6coGNOA2IyJ533ehz1pU
            8mjHwCmjztpraKE9S17MGOC7pj+3apu1W98HUVCdLI3ilKqr7sZGXA3E77Hi3afR
            +TkbL6HiMj5UTVXQR6uPpLkPSEJTSM5ZfjaxgMJjath86dLZrtFry5vkJdPwBZ8f
            dow/OJmHywKBgDlYZ5zrFB+rLnZAK+jLcsbowotyYFp8fSasoN2u4zd0k+Iw5pLx
            cmq3WZJ0e6Y2GTJ3QssLq9AnIjWaAV6wxSBNdK2Yh879PnfxUQmjoBuRLT2mjtEL
            kL/fsN403o204UlXWfiEg/OB9HBMkDLwvfqMJKUbp3c/swc2sxo0Ar2ZAoGBAL2D
            CVUKpePVrC+JGG1mKHN8em4f7n+ivbx5856zTLcEEPDPubRtkCngXyk3IsJubY71
            xN4Mrukz0U6DtWyl8iWGXReAVsg9PmXzYWkKXI4c4umTULLAjwLmQTwKxpzA+xu9
            gAiIOiDYjEiJXo/h16yYA+QzdSLT2K8+T+r5Hp97AoGAXz58CWf7363Fg1OcvgWv
            WiAq7NxfRWcOhqK2Ju/QZYLqvUao9B4h2J3FVXM9ImwbtUDEDu78cUVcCTN7Xi/k
            ixfSdqa5nXkUzyBd4qPFwL2RAHejAu/KvIIz5xnh+CEIg7ZSENXIma7dJdanzTlf
            YiA6AXF71IzQI/Fq7DIr8/U=
            -----END PRIVATE KEY-----
            """;

    private final P7mSignerService service = new P7mSignerServiceImpl(cert, key);

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
