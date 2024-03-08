package it.gov.pagopa.ranking.connector.rest.pdv;

import feign.FeignException;
import feign.FeignException.FeignClientException;
import feign.RetryableException;
import it.gov.pagopa.common.wiremock.BaseWireMockTest;
import it.gov.pagopa.ranking.config.RestConnectorConfig;
import it.gov.pagopa.ranking.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static it.gov.pagopa.common.wiremock.BaseWireMockTest.WIREMOCK_TEST_PROP2BASEPATH_MAP_PREFIX;

@ContextConfiguration(
        classes = {UserRestServiceImpl.class,
                FeignAutoConfiguration.class,
                RestConnectorConfig.class,
                HttpMessageConvertersAutoConfiguration.class,
                PdvErrorDecoderSpy.class
})
@TestPropertySource(
        properties = {
                WIREMOCK_TEST_PROP2BASEPATH_MAP_PREFIX + "app.pdv.base-url=",
                "app.pdv.headers.x-api-key=x_api_key0"
        }
)
class UserRestServiceImplIntegrationTest extends BaseWireMockTest {

    @Autowired
    private UserRestService userRestService;

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
            Assertions.fail("Exception expected");
        }catch (Throwable e){
            Assertions.assertTrue(e instanceof FeignClientException);
            Assertions.assertEquals(FeignClientException.NotFound.class, e.getClass());
        }
    }

    @Test
    void retrieveUserInfoInternalServerError() {
        String userId = "USERID_INTERNALSERVERERROR_1";

        try{
            userRestService.getUser(userId);
            Assertions.fail("Exception expected");
        }catch (Throwable e){
            Assertions.assertTrue(e instanceof FeignException);
            Assertions.assertEquals(FeignException.InternalServerError.class,e.getClass());
        }
    }

    @Test
    void retrieveUserInfoBadRequest() {
        String userId = "USERID_BADREQUEST_1";

        try{
            userRestService.getUser(userId);
            Assertions.fail("Exception expected");
        }catch (Throwable e){
            Assertions.assertTrue(e instanceof FeignClientException);
            Assertions.assertEquals(FeignClientException.BadRequest.class,e.getClass());
        }
    }

    @Test
    void retrieveUserInfoTooManyRequest() {
        PdvErrorDecoderSpy.resetCounter();
        String userId = "USERID_TOOMANYREQUEST_1";

        try{
            userRestService.getUser(userId);
            Assertions.fail("Exception expected");
        }catch (Throwable e){
            Assertions.assertEquals(RetryableException.class, e.getClass());
        }

        Assertions.assertEquals(5, PdvErrorDecoderSpy.getInvocationCount());
    }

    @Test
    void retrieveUserInfoHttpForbidden() {
        String userId = "USERID_FORBIDDEN_1";

        try{
            userRestService.getUser(userId);
            Assertions.fail("Exception expected");
        }catch (Throwable e){
            e.printStackTrace();
            Assertions.assertTrue(e instanceof FeignClientException);
            Assertions.assertEquals(FeignClientException.Forbidden.class,e.getClass());
        }
    }

}