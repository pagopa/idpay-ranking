package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.event.EvaluationDTO;
import it.gov.pagopa.ranking.dto.event.EvaluationRankingDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequest2EvaluationMapper;
import it.gov.pagopa.ranking.event.producer.OnboardingNotifierProducer;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.test.fakers.EvaluationRankingDTOFaker;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class OnboardingNotifierServiceImplTest {

    @Test
    void testOnboardingNotifier_whenProducerReturnTrue(){
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = Mockito.mock(OnboardingRankingRequest2EvaluationMapper.class);
        OnboardingNotifierProducer onboardingNotifierProducer = Mockito.mock(OnboardingNotifierProducer.class);

        EvaluationDTO evaluationDTO = EvaluationRankingDTOFaker.mockInstance(1);
        OnboardingRankingRequests onboardingRankingRequests = OnboardingRankingRequestsFaker.mockInstance(1);

        Mockito.when(mapper.apply(Mockito.any(OnboardingRankingRequests.class))).thenReturn(evaluationDTO);
        Mockito.when(onboardingNotifierProducer.notify(Mockito.any(EvaluationRankingDTO.class))).thenReturn(true);

        OnboardingNotifierServiceImpl onboardingNotifierService = new OnboardingNotifierServiceImpl(onboardingNotifierProducer, mapper);
        onboardingNotifierService.callOnboardingNotifier(onboardingRankingRequests);
    }

    @Test
    void testOnboardingNotifier_whenProducerReturnFalse_thenThrowExceptionInMethod(){
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = Mockito.mock(OnboardingRankingRequest2EvaluationMapper.class);
        OnboardingNotifierProducer onboardingNotifierProducer = Mockito.mock(OnboardingNotifierProducer.class);

        EvaluationDTO evaluationDTO = EvaluationRankingDTOFaker.mockInstance(1);
        OnboardingRankingRequests onboardingRankingRequests = OnboardingRankingRequestsFaker.mockInstance(1);

        Mockito.when(mapper.apply(Mockito.any(OnboardingRankingRequests.class))).thenReturn(evaluationDTO);
        Mockito.when(onboardingNotifierProducer.notify(Mockito.any(EvaluationRankingDTO.class))).thenReturn(false);

        OnboardingNotifierServiceImpl onboardingNotifierService = new OnboardingNotifierServiceImpl(onboardingNotifierProducer, mapper);
        onboardingNotifierService.callOnboardingNotifier(onboardingRankingRequests);
    }
}
