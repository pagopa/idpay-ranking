//package it.gov.pagopa.ranking.connector.rest.pdv; //todo check and remove
//
//import feign.FeignException.FeignClientException;
//import it.gov.pagopa.ranking.BaseIntegrationTest;
//import it.gov.pagopa.ranking.model.User;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.test.context.TestPropertySource;
//
///**
// * See confluence page: <a href="https://pagopa.atlassian.net/wiki/spaces/IDPAY/pages/615974424/Secrets+UnitTests">Secrets for UnitTests</a>
// */
//@SuppressWarnings({"squid:S3577", "NewClassNamingConvention"}) // suppressing class name not match alert: we are not using the Test suffix in order to let not execute this test by default maven configuration because it depends on properties not pushable. See
//@TestPropertySource(locations = {
//        "classpath:/secrets/appPdv.properties",
//        },
//        properties = {
//                "app.pdv.base-url=https://api.uat.tokenizer.pdv.pagopa.it/tokenizer/v1"
//        })
//class UserRestServiceImplTestIntegrated extends BaseIntegrationTest {
//
//    @Autowired
//    private UserRestService userRestService;
//
//    @Value("${app.pdv.userIdOk:a85268f9-1d62-4123-8f86-8cf630b60998}")
//    private String userIdOK;
//    @Value("${app.pdv.userFiscalCodeExpected:A4p9Y4QUlTtutHT}")
//    private String fiscalCodeOKExpected;
//    @Value("${app.pdv.userIdNotFound:02105b50-9a81-4cd2-8e17-6573ebb09195}")
//    private String userIdNotFound;
//
//    @Test
//    void getUserOk() {
//        User result = userRestService.getUser(userIdOK);
//
//        Assertions.assertNotNull(result);
//        Assertions.assertEquals(fiscalCodeOKExpected, result.getFiscalCode());
//
//    }
//
//    @Test
//    void getUserNotFound() {
//        try {
//            userRestService.getUser(userIdNotFound);
//        } catch (Throwable e) {
//            Assertions.assertTrue(e instanceof FeignClientException);
//            Assertions.assertEquals(FeignClientException.NotFound.class, e.getClass());
//        }
//    }
//}