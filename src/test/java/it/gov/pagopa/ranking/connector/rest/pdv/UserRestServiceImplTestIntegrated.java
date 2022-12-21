package it.gov.pagopa.ranking.connector.rest.pdv;

import it.gov.pagopa.ranking.BaseIntegrationTest;
import it.gov.pagopa.ranking.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import feign.FeignException.FeignClientException;

@SuppressWarnings("squid:S3577") // suppressing class name not match alert
@TestPropertySource(locations = {
        "classpath:/secrets/appPdv.properties",
        },
        properties = {
                "app.pdv.base-url=https://api.uat.tokenizer.pdv.pagopa.it/tokenizer/v1"
        })
class UserRestServiceImplTestIntegrated extends BaseIntegrationTest {

    @Autowired
    private UserRestService userRestService;

    @Value("${app.pdv.userIdOk:02105b50-9a81-4cd2-8e17-6573ebb09196}")
    private String userIdOK;
    @Value("${app.pdv.userFiscalCodeExpected:125}")
    private String fiscalCodeOKExpected;
    @Value("${app.pdv.userIdNotFound:02105b50-9a81-4cd2-8e17-6573ebb09195}")
    private String userIdNotFound;

    @Test
    void getUserOk() {
        User result = userRestService.getUser(userIdOK);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(fiscalCodeOKExpected, result.getFiscalCode());

    }

    @Test
    void getUserNotFound() {
        try {
            userRestService.getUser(userIdNotFound);
        } catch (Throwable e) {
            Assertions.assertTrue(e instanceof FeignClientException);
            Assertions.assertEquals(FeignClientException.NotFound.class, e.getClass());
        }
    }
}