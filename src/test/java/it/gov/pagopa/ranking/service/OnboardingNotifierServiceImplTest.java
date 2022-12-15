package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.event.EvaluationRankingDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequest2EvaluationMapper;
import it.gov.pagopa.ranking.event.producer.OnboardingNotifierProducer;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.service.initiative.InitiativeConfigService;
import it.gov.pagopa.ranking.test.fakers.EvaluationRankingDTOFaker;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

class OnboardingNotifierServiceImplTest {

    @Test
    void testOnboardingNotifier_whenProducerReturnTrue(){
        // Given organizationId and initiativeID on DB for Status Publishing
        InitiativeConfig initiativeConfig = new InitiativeConfig();
        initiativeConfig.setRankingStatus(RankingStatus.READY);
        List<OnboardingRankingRequests> onboardingRankingRequests = new ArrayList<>();
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = Mockito.mock(OnboardingRankingRequest2EvaluationMapper.class);
        OnboardingNotifierProducer onboardingNotifierProducer = Mockito.mock(OnboardingNotifierProducer.class);
        InitiativeConfigService initiativeConfigService = Mockito.mock(InitiativeConfigService.class);

        EvaluationRankingDTO evaluationDTO = EvaluationRankingDTOFaker.mockInstance(1);
        OnboardingRankingRequests onboardingRankingRequests1 = OnboardingRankingRequestsFaker.mockInstance(1);
        OnboardingRankingRequests onboardingRankingRequests2 = OnboardingRankingRequestsFaker.mockInstance(2);
        onboardingRankingRequests.add(onboardingRankingRequests1);
        onboardingRankingRequests.add(onboardingRankingRequests2);

        Mockito.when(mapper.apply(Mockito.any(OnboardingRankingRequests.class))).thenReturn(evaluationDTO);
        Mockito.when(onboardingNotifierProducer.notify(Mockito.any(EvaluationRankingDTO.class))).thenReturn(true);

        OnboardingNotifierServiceImpl onboardingNotifierService = new OnboardingNotifierServiceImpl(onboardingNotifierProducer, mapper, initiativeConfigService);
        onboardingNotifierService.callOnboardingNotifier(initiativeConfig, onboardingRankingRequests);

        Mockito.verify(mapper, Mockito.times(onboardingRankingRequests.size())).apply(Mockito.any(OnboardingRankingRequests.class));
        Mockito.verify(onboardingNotifierProducer, Mockito.times(onboardingRankingRequests.size())).notify(Mockito.any(EvaluationRankingDTO.class));
        Mockito.verify(initiativeConfigService, Mockito.times(2)).save(Mockito.any(InitiativeConfig.class));
    }

    @Test
    void testOnboardingNotifier_whenProducerReturnFalse_thenThrowExceptionInMethod(){
        // Given organizationId and initiativeID on DB for Status Publishing
        InitiativeConfig initiativeConfig = new InitiativeConfig();
        initiativeConfig.setRankingStatus(RankingStatus.READY);
        List<OnboardingRankingRequests> onboardingRankingRequests = new ArrayList<>();
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = Mockito.mock(OnboardingRankingRequest2EvaluationMapper.class);
        OnboardingNotifierProducer onboardingNotifierProducer = Mockito.mock(OnboardingNotifierProducer.class);
        InitiativeConfigService initiativeConfigService = Mockito.mock(InitiativeConfigService.class);

        EvaluationRankingDTO evaluationDTO = EvaluationRankingDTOFaker.mockInstance(1);
        OnboardingRankingRequests onboardingRankingRequests1 = OnboardingRankingRequestsFaker.mockInstance(1);
        OnboardingRankingRequests onboardingRankingRequests2 = OnboardingRankingRequestsFaker.mockInstance(2);
        onboardingRankingRequests.add(onboardingRankingRequests1);
        onboardingRankingRequests.add(onboardingRankingRequests2);

        Mockito.when(mapper.apply(Mockito.any(OnboardingRankingRequests.class))).thenReturn(evaluationDTO);
        Mockito.when(onboardingNotifierProducer.notify(Mockito.any(EvaluationRankingDTO.class))).thenReturn(false);

        OnboardingNotifierServiceImpl onboardingNotifierService = new OnboardingNotifierServiceImpl(onboardingNotifierProducer, mapper, initiativeConfigService);
        onboardingNotifierService.callOnboardingNotifier(initiativeConfig, onboardingRankingRequests);

        Mockito.verify(mapper, Mockito.times(onboardingRankingRequests.size())).apply(Mockito.any(OnboardingRankingRequests.class));
        Mockito.verify(onboardingNotifierProducer, Mockito.times(onboardingRankingRequests.size())).notify(Mockito.any(EvaluationRankingDTO.class));
        Mockito.verify(initiativeConfigService, Mockito.times(2)).save(Mockito.any(InitiativeConfig.class));
    }
}
