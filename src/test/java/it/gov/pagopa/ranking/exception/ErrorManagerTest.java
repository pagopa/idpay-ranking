package it.gov.pagopa.ranking.exception;

import it.gov.pagopa.ranking.BaseIntegrationTest;
import it.gov.pagopa.ranking.controller.RankingApiController;
import it.gov.pagopa.ranking.dto.ErrorDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class ErrorManagerTest extends BaseIntegrationTest {

    @MockBean
    RankingApiController controller;

    @Autowired
    MockMvc mvc;

    @Test
    void handleExceptionClientExceptionNoBody() throws Exception {

        Mockito.when(controller.rankingRequests("ClientExceptionNoBody", "INITIATIVE_ID", 0, 10))
                .thenThrow(new ClientExceptionNoBody(HttpStatus.NOT_FOUND));

        mvc.perform(MockMvcRequestBuilders.get("/idpay/ranking/organization/{organizationId}/initiative/{initiativeId}",
                        "ClientExceptionNoBody", "INITIATIVE_ID"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void handleExceptionClientExceptionWithBody() throws Exception {

        Mockito.when(controller.rankingRequests("ClientExceptionWithBody", "INITIATIVE_ID", 0, 10))
                .thenThrow(new ClientExceptionWithBody(HttpStatus.BAD_REQUEST, "Error","Error ClientExceptionWithBody"));
        ErrorDTO errorClientExceptionWithBody= new ErrorDTO("Error","Error ClientExceptionWithBody");

        mvc.perform(MockMvcRequestBuilders.get("/idpay/ranking/organization/{organizationId}/initiative/{initiativeId}",
                       "ClientExceptionWithBody", "INITIATIVE_ID"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(r ->
                        Assertions.assertEquals(
                                "{\"code\":\"Error\",\"message\":\"Error ClientExceptionWithBody\"}",
                                r.getResponse().getContentAsString()
                        ));

        Mockito.when(controller.rankingRequests("ClientExceptionWithBodyWithStatusAndTitleAndMessageAndThrowable", "INITIATIVE_ID", 0,10))
                .thenThrow(new ClientExceptionWithBody(HttpStatus.BAD_REQUEST, "Error","Error ClientExceptionWithBody", new Throwable()));
        ErrorDTO errorClientExceptionWithBodyWithStatusAndTitleAndMessageAndThrowable = new ErrorDTO("Error","Error ClientExceptionWithBody");

        mvc.perform(MockMvcRequestBuilders.get("/idpay/ranking/organization/{organizationId}/initiative/{initiativeId}",
                        "ClientExceptionWithBodyWithStatusAndTitleAndMessageAndThrowable", "INITIATIVE_ID"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(r ->
                        Assertions.assertEquals(
                                "{\"code\":\"Error\",\"message\":\"Error ClientExceptionWithBody\"}",
                                r.getResponse().getContentAsString()
                        ));
    }

    @Test
    void handleExceptionClientExceptionTest() throws Exception {

        Mockito.when(controller.rankingRequests("ClientException", "INITIATIVE_ID", 0,10))
                .thenThrow(ClientException.class);
        mvc.perform(MockMvcRequestBuilders.get("/idpay/ranking/organization/{organizationId}/initiative/{initiativeId}",
                                "ClientException", "INITIATIVE_ID"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());


        Mockito.when(controller.rankingRequests("ClientExceptionStatusAndMessage", "INITIATIVE_ID", 0,10))
                .thenThrow(new ClientException(HttpStatus.BAD_REQUEST, "ClientException with httpStatus and message"));
        mvc.perform(MockMvcRequestBuilders.get("/idpay/ranking/organization/{organizationId}/initiative/{initiativeId}",
                        "ClientExceptionStatusAndMessage", "INITIATIVE_ID"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(r -> Assertions.assertEquals(
                        "{\"code\":\"Error\",\"message\":\"Something gone wrong\"}",
                        r.getResponse().getContentAsString()
                ));

        Mockito.when(controller.rankingRequests("ClientExceptionStatusAndMessageAndThrowable", "INITIATIVE_ID", 0,10))
                .thenThrow(new ClientException(HttpStatus.BAD_REQUEST, "ClientException with httpStatus, message and throwable", new Throwable()));
        mvc.perform(MockMvcRequestBuilders.get("/idpay/ranking/organization/{organizationId}/initiative/{initiativeId}",
                        "ClientExceptionStatusAndMessageAndThrowable", "INITIATIVE_ID"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(r -> Assertions.assertEquals(
                        "{\"code\":\"Error\",\"message\":\"Something gone wrong\"}",
                        r.getResponse().getContentAsString()
                ));
    }

    @Test
    void handleExceptionRuntimeException() throws Exception {

        Mockito.when(controller.rankingRequests("RuntimeException", "INITIATIVE_ID", 0,10))
                .thenThrow(RuntimeException.class);
        mvc.perform(MockMvcRequestBuilders.get("/idpay/ranking/organization/{organizationId}/initiative/{initiativeId}",
                        "RuntimeException", "INITIATIVE_ID"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}