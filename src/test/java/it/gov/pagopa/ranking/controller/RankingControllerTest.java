package it.gov.pagopa.ranking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.service.evaluate.OnboardingRankingBuildFileMediatorService;
import it.gov.pagopa.ranking.service.initiative.InitiativeConfigService;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.ranking.utils.TestUtils;
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

import java.time.LocalDate;
import java.util.List;

@WebMvcTest(RankingControllerImpl.class)
class RankingControllerTest {

    @MockBean
    private OnboardingRankingBuildFileMediatorService onboardingRankingBuildFileMediatorService;
    @MockBean
    private InitiativeConfigService initiativeConfigService;

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper objectMapper = TestUtils.objectMapper;

    @Test
    void testSuccess() throws Exception {
        List<InitiativeConfig> expectedResult = List.of(InitiativeConfigFaker.mockInstance(0));
        expectedResult.get(0).setRankingFilePath("path");
        Mockito.when(onboardingRankingBuildFileMediatorService.execute()).thenReturn(expectedResult);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/idpay/ranking/build/file/start")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        Assertions.assertEquals(objectMapper.writeValueAsString(expectedResult), result.getResponse().getContentAsString());

        Mockito.verify(onboardingRankingBuildFileMediatorService).execute();
    }

    @Test
    void testSetDate() throws Exception {
        String initiativeId = "INITIATIVE_ID";
        InitiativeConfig initiative = InitiativeConfigFaker.mockInstanceBuilder(1).initiativeId(initiativeId).build();

        LocalDate date = LocalDate.now();

        Mockito.when(initiativeConfigService.findById(initiativeId)).thenReturn(initiative);

        mvc.perform(MockMvcRequestBuilders
                        .put("/idpay/initiative/{initiativeId}/reset-status-set-ranking-end-date",
                                initiativeId)
                        .param("rankingEndDate", date.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        Mockito.verify(initiativeConfigService).setInitiativeRankingEndDateAndStatusWaitingEnd(initiativeId, date);
    }
}
