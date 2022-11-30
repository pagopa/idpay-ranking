package it.gov.pagopa.ranking.controller;

import it.gov.pagopa.ranking.dto.RankingPageDTO;
import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.service.RankingRequestsApiServiceImpl;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import it.gov.pagopa.ranking.test.fakers.RankingRequestsApiDTOFaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

        Mockito.when(service.findByInitiativeId(dto.getOrganizationId(), dto.getInitiativeId(), 0, 10, null))
                .thenReturn(List.of(dto));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/idpay/ranking/organization/{organizationId}/initiative/{initiativeId}",
                        dto.getOrganizationId(), dto.getInitiativeId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        String expected = "[{\"userId\":\"userId_1\",\"initiativeId\":\"initiativeId_1\",\"organizationId\":\"organizationId_1\",\"admissibilityCheckDate\":\"2022-11-01T00:00:00\",\"criteriaConsensusTimestamp\":\"2022-11-01T01:00:00\",\"rankingValue\":1155869325,\"ranking\":1,\"beneficiaryRankingStatus\":\"ELIGIBLE_OK\",\"rankingPathFile\":null}]";
        Assertions.assertEquals(expected, result.getResponse().getContentAsString());
    }

    @Test
    void testSuccessWithStatusFilter() throws Exception {
        RankingRequestsApiDTO dto1 = RankingRequestsApiDTOFaker.mockInstance(1);
        dto1.setAdmissibilityCheckDate(LocalDateTime.of(2022,11,1,0,0));
        dto1.setCriteriaConsensusTimestamp(LocalDateTime.of(2022,11,1,1,0));
        RankingRequestsApiDTO dto2 = RankingRequestsApiDTOFaker.mockInstance(1);
        dto2.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.TO_NOTIFY);
        RankingRequestsApiDTO dto3 = RankingRequestsApiDTOFaker.mockInstance(1);
        dto3.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_KO);

        Mockito.when(service.findByInitiativeId(dto1.getOrganizationId(), dto1.getInitiativeId(), 0, 10, BeneficiaryRankingStatus.ELIGIBLE_OK))
                .thenReturn(List.of(dto1));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/idpay/ranking/organization/{organizationId}/initiative/{initiativeId}",
                                        dto1.getOrganizationId(), dto1.getInitiativeId())
                        .param("beneficiaryRankingStatus", "ELIGIBLE_OK")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        String expected = "[{\"userId\":\"userId_1\",\"initiativeId\":\"initiativeId_1\",\"organizationId\":\"organizationId_1\",\"admissibilityCheckDate\":\"2022-11-01T00:00:00\",\"criteriaConsensusTimestamp\":\"2022-11-01T01:00:00\",\"rankingValue\":1155869325,\"ranking\":1,\"beneficiaryRankingStatus\":\"ELIGIBLE_OK\",\"rankingPathFile\":null}]";
        Assertions.assertEquals(expected, result.getResponse().getContentAsString());
    }

    @Test
    void testNotFound() throws Exception {

        Mockito.when(service.findByInitiativeId("orgId", "initiativeId", 0, 10, null))
                .thenReturn(null);

        mvc.perform(MockMvcRequestBuilders
                .get("/idpay/ranking/organization/{organizationId}/initiative/{initiativeId}",
                        "orgId", "initiativeId"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testPagedSuccess() throws Exception {
        LocalDateTime date = LocalDateTime.of(2022, 11, 1, 0, 0);

        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setRankingStatus(RankingStatus.COMPLETED);
        initiativeConfig.setRankingPublishedTimeStamp(date);
        initiativeConfig.setRankingGeneratedTimeStamp(date);

        List<RankingRequestsApiDTO> dtoList = List.of(RankingRequestsApiDTOFaker.mockInstanceBuilder(1)
                .admissibilityCheckDate(date)
                .criteriaConsensusTimestamp(date)
                .build()
        );

        RankingPageDTO rankingPageDTO = RankingPageDTO.builder()
                .content(dtoList)
                .pageNumber(0)
                .pageSize(1)
                .totalElements(1)
                .totalPages(1)
                .rankingStatus(RankingStatus.COMPLETED)
                .rankingPublishedTimeStamp(initiativeConfig.getRankingPublishedTimeStamp())
                .rankingGeneratedTimeStamp(initiativeConfig.getRankingGeneratedTimeStamp())
                .totalEligibleOk(0)
                .totalEligibleKo(0)
                .build();

        Mockito.when(service.findByInitiativeIdPaged(initiativeConfig.getOrganizationId(), initiativeConfig.getInitiativeId(), 0, 10, null))
                .thenReturn(rankingPageDTO);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/idpay/ranking/organization/{organizationId}/initiative/{initiativeId}/paged",
                                initiativeConfig.getOrganizationId(), initiativeConfig.getInitiativeId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        String expected = "{\"content\":[{\"userId\":\"userId_1\",\"initiativeId\":\"initiativeId_1\",\"organizationId\":\"organizationId_1\",\"admissibilityCheckDate\":\"2022-11-01T00:00:00\",\"criteriaConsensusTimestamp\":\"2022-11-01T00:00:00\",\"rankingValue\":1155869325,\"ranking\":1,\"beneficiaryRankingStatus\":\"ELIGIBLE_OK\",\"rankingPathFile\":null}],\"pageNumber\":0,\"pageSize\":1,\"totalElements\":1,\"totalPages\":1,\"rankingStatus\":\"COMPLETED\",\"rankingPublishedTimeStamp\":\"2022-11-01T00:00:00\",\"rankingGeneratedTimeStamp\":\"2022-11-01T00:00:00\",\"totalEligibleOk\":0,\"totalEligibleKo\":0}";
        Assertions.assertEquals(expected, result.getResponse().getContentAsString());
    }

    @Test
    void testPagedSuccessWithStatusFilter() throws Exception {
        LocalDateTime date = LocalDateTime.of(2022, 11, 1, 0, 0);

        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setRankingStatus(RankingStatus.COMPLETED);
        initiativeConfig.setRankingPublishedTimeStamp(date);
        initiativeConfig.setRankingGeneratedTimeStamp(date);

        RankingRequestsApiDTO dto1 = RankingRequestsApiDTOFaker.mockInstanceBuilder(1)
                .admissibilityCheckDate(date)
                .criteriaConsensusTimestamp(date)
                .build();
        RankingRequestsApiDTO dto2 = RankingRequestsApiDTOFaker.mockInstance(1);
        dto2.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.TO_NOTIFY);
        RankingRequestsApiDTO dto3 = RankingRequestsApiDTOFaker.mockInstance(1);
        dto3.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_KO);

        List<RankingRequestsApiDTO> dtoList = List.of(dto1);

        RankingPageDTO rankingPageDTO = RankingPageDTO.builder()
                .content(dtoList)
                .pageNumber(0)
                .pageSize(1)
                .totalElements(1)
                .totalPages(1)
                .rankingStatus(RankingStatus.COMPLETED)
                .rankingPublishedTimeStamp(initiativeConfig.getRankingPublishedTimeStamp())
                .rankingGeneratedTimeStamp(initiativeConfig.getRankingGeneratedTimeStamp())
                .totalEligibleOk(0)
                .totalEligibleKo(0)
                .build();

        Mockito.when(service.findByInitiativeIdPaged(initiativeConfig.getOrganizationId(), initiativeConfig.getInitiativeId(), 0, 10, null))
                .thenReturn(rankingPageDTO);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/idpay/ranking/organization/{organizationId}/initiative/{initiativeId}/paged",
                                initiativeConfig.getOrganizationId(), initiativeConfig.getInitiativeId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        String expected = "{\"content\":[{\"userId\":\"userId_1\",\"initiativeId\":\"initiativeId_1\",\"organizationId\":\"organizationId_1\",\"admissibilityCheckDate\":\"2022-11-01T00:00:00\",\"criteriaConsensusTimestamp\":\"2022-11-01T00:00:00\",\"rankingValue\":1155869325,\"ranking\":1,\"beneficiaryRankingStatus\":\"ELIGIBLE_OK\",\"rankingPathFile\":null}],\"pageNumber\":0,\"pageSize\":1,\"totalElements\":1,\"totalPages\":1,\"rankingStatus\":\"COMPLETED\",\"rankingPublishedTimeStamp\":\"2022-11-01T00:00:00\",\"rankingGeneratedTimeStamp\":\"2022-11-01T00:00:00\",\"totalEligibleOk\":0,\"totalEligibleKo\":0}";
        Assertions.assertEquals(expected, result.getResponse().getContentAsString());
    }

    @Test
    void testPagedNotFound() throws Exception {

        Mockito.when(service.findByInitiativeId("orgId", "initiativeId", 0, 10, null))
                .thenReturn(null);

        mvc.perform(MockMvcRequestBuilders
                        .get("/idpay/ranking/organization/{organizationId}/initiative/{initiativeId}/paged",
                                "orgId", "initiativeId"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}