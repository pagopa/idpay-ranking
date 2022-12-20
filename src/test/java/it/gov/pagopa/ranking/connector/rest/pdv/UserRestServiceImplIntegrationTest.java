package it.gov.pagopa.ranking.connector.rest.pdv;

import feign.FeignException;
import feign.FeignException.FeignClientException;
import feign.RetryableException;
import it.gov.pagopa.ranking.BaseIntegrationTest;
import it.gov.pagopa.ranking.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "logging.level.it.gov.pagopa.reward.notification.rest.UserRestClientImpl=WARN",
})
class UserRestServiceImplIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRestService userRestService;

    private final PdvErrorDecoderExt pdvErrorDecoder = new PdvErrorDecoderExt();

    @Test
    void retrieveUserInfoOk() {
        String userId = "USERID_OK_1";

        User result = userRestService.getUser(userId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("fiscalCode", result.getFiscalCode());
    }

    @Test
    void retrieveUserInfoNotFound() {
        String userId = "USERID_NOTFOUND_1";

        try{
            userRestService.getUser(userId);
        }catch (Throwable e){
            Assertions.assertTrue(e instanceof FeignClientException);
            Assertions.assertEquals(FeignClientException.NotFound.class, e.getClass());
        }
    }

    /*@Test
    void retrieveUserInfoInternalServerError() {
        String userId = "USERID_INTERNALSERVERERROR_1";

        try{
            userRestService.getUser(userId);
        }catch (Throwable e){
            Assertions.assertTrue(e instanceof FeignClientException);
            Assertions.assertEquals(FeignClientException.InternalServerError.class,e.getClass());
        }
    }*/

    /*@Test
    void retrieveUserInfoBadRequest() {
        String userId = "USERID_BADREQUEST_1";

        try{
            userRestService.getUser(userId);
        }catch (Throwable e){
            Assertions.assertTrue(e instanceof FeignClientException);
            Assertions.assertEquals(FeignClientException.BadRequest.class,e.getClass());
        }
    }*/

    @Test
    void retrieveUserInfoTooManyRequest() {
        String userId = "USERID_TOOMANYREQUEST_1";

        try{
            userRestService.getUser(userId);
        }catch (Throwable e){
            Assertions.assertEquals(RetryableException.class, e.getClass());
        }

        // 5 times is default for Feign retryer
        Assertions.assertEquals(5, pdvErrorDecoder.getInvocationCount());
    }

    /*@Test
    void retrieveUserInfoHttpForbidden() {
        String userId = "USERID_FORBIDDEN_1";

        try{
            userRestService.getUser(userId);
        }catch (Throwable e){
            e.printStackTrace();
            Assertions.assertTrue(e instanceof FeignClientException);
            Assertions.assertEquals(FeignClientException.Forbidden.class,e.getClass());
        }
    }*/

}