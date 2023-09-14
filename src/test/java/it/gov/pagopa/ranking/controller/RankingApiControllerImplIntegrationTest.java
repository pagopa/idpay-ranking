package it.gov.pagopa.ranking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.gov.pagopa.ranking.BaseIntegrationTest;
import it.gov.pagopa.ranking.constants.OnboardingConstants;
import it.gov.pagopa.ranking.dto.event.EvaluationRankingDTO;
import it.gov.pagopa.ranking.dto.initiative.InitiativeGeneralDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequest2EvaluationMapper;
import it.gov.pagopa.ranking.event.producer.OnboardingNotifierProducer;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.repository.InitiativeConfigRepository;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestPropertySource(properties = {
        "logging.level.it.gov.pagopa.ranking.controller.RankingApiControllerImpl=WARN",

})
class RankingApiControllerImplIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private InitiativeConfigRepository initiativeConfigRepository;

    @Autowired
    private OnboardingRankingRequestsRepository onboardingRankingRequestsRepository;

    @Autowired
    private OnboardingRankingRequest2EvaluationMapper onboardingRankingRequest2EvaluationMapper;

    @SpyBean
    private OnboardingNotifierProducer onboardingNotifierProducerSpy;


    @Test
    void notifyCitizen() throws Exception {
        long maxWaitingMs = 60000;
        Set<EvaluationRankingDTO> expectedOutcome = new HashSet<>();

        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setRankingStatus(RankingStatus.READY);
        initiativeConfig.setBeneficiaryType(InitiativeGeneralDTO.BeneficiaryTypeEnum.NF);
        initiativeConfig.setRankingEndDate(LocalDate.now().minusDays(1));
        initiativeConfigRepository.save(initiativeConfig);

        OnboardingRankingRequests onboardingRankingRequestsEligibleOk = OnboardingRankingRequestsFaker.mockInstance(1);
        onboardingRankingRequestsEligibleOk.setOrganizationId(initiativeConfig.getOrganizationId());
        onboardingRankingRequestsEligibleOk.setInitiativeId(initiativeConfig.getInitiativeId());
        onboardingRankingRequestsEligibleOk.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_OK);
        onboardingRankingRequestsEligibleOk.setFamilyId("FAMILYID1");
        onboardingRankingRequestsEligibleOk.setMemberIds(Set.of(onboardingRankingRequestsEligibleOk.getUserId(),
                "FAMILYID1_MEMBER2"));
        onboardingRankingRequestsRepository.save(onboardingRankingRequestsEligibleOk);

        EvaluationRankingDTO evRequestEligibleOk = onboardingRankingRequest2EvaluationMapper.apply(onboardingRankingRequestsEligibleOk, initiativeConfig);

        expectedOutcome.add(evRequestEligibleOk.toBuilder()
                .userId("FAMILYID1_MEMBER2")
                .status(OnboardingConstants.ONBOARDING_STATUS_DEMANDED)
                .build());

        OnboardingRankingRequests onboardingRankingRequestsEligibleKO = OnboardingRankingRequestsFaker.mockInstance(2);
        onboardingRankingRequestsEligibleKO.setOrganizationId(initiativeConfig.getOrganizationId());
        onboardingRankingRequestsEligibleKO.setInitiativeId(initiativeConfig.getInitiativeId());
        onboardingRankingRequestsEligibleKO.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_KO);
        onboardingRankingRequestsEligibleKO.setFamilyId("FAMILYID2");
        onboardingRankingRequestsEligibleKO.setMemberIds(Set.of(onboardingRankingRequestsEligibleKO.getUserId(),
                "FAMILYID2_MEMBER2"));
        onboardingRankingRequestsRepository.save(onboardingRankingRequestsEligibleKO);

        EvaluationRankingDTO evRequestEligibleKo = onboardingRankingRequest2EvaluationMapper.apply(onboardingRankingRequestsEligibleKO, initiativeConfig);
        expectedOutcome.add(evRequestEligibleKo);


        Mockito.doReturn(false).when(onboardingNotifierProducerSpy).notify(evRequestEligibleOk);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/idpay/ranking/organization/{organizationId}/initiative/{initiativeId}/notified",
                                initiativeConfig.getOrganizationId(), initiativeConfig.getInitiativeId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(print())
                .andReturn();

        List<ConsumerRecord<String, String>> payloadOutcomeConsumer = kafkaTestUtilitiesService.consumeMessages(topicEvaluationOnboardingRankingOutcome, 2, maxWaitingMs);
        Assertions.assertEquals(expectedOutcome.size(), payloadOutcomeConsumer.size());
        Set<EvaluationRankingDTO> outcomeResult = payloadOutcomeConsumer.stream()
                .map(this::deserializerMessage)
                .collect(Collectors.toSet());
        Assertions.assertEquals(expectedOutcome, outcomeResult);
    }

    private EvaluationRankingDTO deserializerMessage(ConsumerRecord<String, String> rc) {
        try {
            return objectMapper.readValue(rc.value(), EvaluationRankingDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
