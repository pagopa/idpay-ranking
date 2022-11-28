package it.gov.pagopa.ranking.controller;

import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.service.RankingRequestsApiServiceImpl;
import it.gov.pagopa.ranking.test.fakers.RankingRequestsApiDTOFaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(RankingApiControllerImpl.class)
class RankingApiControllerImplTest {

    @MockBean
    private RankingRequestsApiServiceImpl service;

    @Autowired
    private MockMvc mvc;

    @Test
    void testSuccess() throws Exception {
        RankingRequestsApiDTO dto = RankingRequestsApiDTOFaker.mockInstance(1);
        dto.setAdmissibilityCheckDate(LocalDateTime.of(2022,11,1,0,0));
        dto.setCriteriaConsensusTimestamp(LocalDateTime.of(2022,11,1,1,0));

        Mockito.when(service.findByInitiativeId(dto.getOrganizationId(), dto.getInitiativeId(), 0, 10))
                .thenReturn(List.of(dto));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/idpay/ranking/organization/{organizationId}/initiative/{initiativeId}",
                        dto.getOrganizationId(), dto.getInitiativeId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        String expected = "[{\"userId\":\"userId_1\",\"initiativeId\":\"initiativeId_1\",\"organizationId\":\"organizationId_1\",\"admissibilityCheckDate\":\"2022-11-01T00:00:00\",\"criteriaConsensusTimestamp\":\"2022-11-01T01:00:00\",\"rankingValue\":1155869325,\"rank\":1}]";
        Assertions.assertEquals(expected, result.getResponse().getContentAsString());
    }

    @Test
    void testNotFound() throws Exception {

        Mockito.when(service.findByInitiativeId("orgId", "initiativeId", 0, 10))
                .thenReturn(null);

        mvc.perform(MockMvcRequestBuilders
                .get("/idpay/ranking/organization/{organizationId}/initiative/{initiativeId}",
                        "orgId", "initiativeId"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}