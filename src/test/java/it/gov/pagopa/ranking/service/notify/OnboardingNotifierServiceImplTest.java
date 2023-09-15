package it.gov.pagopa.ranking.service.notify;

import it.gov.pagopa.ranking.dto.event.EvaluationRankingDTO;
import it.gov.pagopa.ranking.dto.initiative.InitiativeGeneralDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequest2EvaluationMapper;
import it.gov.pagopa.ranking.event.producer.OnboardingNotifierProducer;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.service.RankingContextHolderService;
import it.gov.pagopa.ranking.service.RankingErrorNotifierService;
import it.gov.pagopa.ranking.test.fakers.EvaluationRankingDTOFaker;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        RankingContextHolderService rankingContextHolderService = Mockito.mock(RankingContextHolderService.class);
        RankingErrorNotifierService rankingErrorNotifierService = Mockito.mock(RankingErrorNotifierService.class);

        EvaluationRankingDTO evaluationDTO = EvaluationRankingDTOFaker.mockInstance(1);
        OnboardingRankingRequests onboardingRankingRequests1 = OnboardingRankingRequestsFaker.mockInstance(1);
        OnboardingRankingRequests onboardingRankingRequests2 = OnboardingRankingRequestsFaker.mockInstance(2);
        onboardingRankingRequests.add(onboardingRankingRequests1);
        onboardingRankingRequests.add(onboardingRankingRequests2);

        Mockito.when(mapper.apply(Mockito.any(OnboardingRankingRequests.class), Mockito.any(InitiativeConfig.class))).thenReturn(evaluationDTO);
        Mockito.when(onboardingNotifierProducer.notify(Mockito.any(EvaluationRankingDTO.class))).thenReturn(true);

        OnboardingNotifierServiceImpl onboardingNotifierService = new OnboardingNotifierServiceImpl(onboardingNotifierProducer, mapper, rankingContextHolderService, rankingErrorNotifierService);
        onboardingNotifierService.callOnboardingNotifier(initiativeConfig, onboardingRankingRequests);

        Mockito.verify(mapper, Mockito.times(onboardingRankingRequests.size())).apply(Mockito.any(OnboardingRankingRequests.class), Mockito.any(InitiativeConfig.class));
        Mockito.verify(onboardingNotifierProducer, Mockito.times(onboardingRankingRequests.size())).notify(Mockito.any(EvaluationRankingDTO.class));
        Mockito.verify(rankingContextHolderService, Mockito.times(1)).setInitiativeConfig(Mockito.any(InitiativeConfig.class));
        Mockito.verify(rankingErrorNotifierService, Mockito.never()).notifyRankingOnboardingOutcome(Mockito.any(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any(Throwable.class));
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
        RankingContextHolderService rankingContextHolderService = Mockito.mock(RankingContextHolderService.class);
        RankingErrorNotifierService rankingErrorNotifierService = Mockito.mock(RankingErrorNotifierService.class);

        EvaluationRankingDTO evaluationDTO = EvaluationRankingDTOFaker.mockInstance(1);
        OnboardingRankingRequests onboardingRankingRequests1 = OnboardingRankingRequestsFaker.mockInstance(1);
        OnboardingRankingRequests onboardingRankingRequests2 = OnboardingRankingRequestsFaker.mockInstance(2);
        onboardingRankingRequests.add(onboardingRankingRequests1);
        onboardingRankingRequests.add(onboardingRankingRequests2);

        Mockito.when(mapper.apply(Mockito.any(OnboardingRankingRequests.class), Mockito.any(InitiativeConfig.class))).thenReturn(evaluationDTO);
        Mockito.when(onboardingNotifierProducer.notify(Mockito.any(EvaluationRankingDTO.class))).thenReturn(false);

        OnboardingNotifierServiceImpl onboardingNotifierService = new OnboardingNotifierServiceImpl(onboardingNotifierProducer, mapper, rankingContextHolderService, rankingErrorNotifierService);
        onboardingNotifierService.callOnboardingNotifier(initiativeConfig, onboardingRankingRequests);

        Mockito.verify(mapper, Mockito.times(onboardingRankingRequests.size())).apply(Mockito.any(OnboardingRankingRequests.class), Mockito.any(InitiativeConfig.class));
        Mockito.verify(onboardingNotifierProducer, Mockito.times(onboardingRankingRequests.size())).notify(Mockito.any(EvaluationRankingDTO.class));
        Mockito.verify(rankingContextHolderService, Mockito.times(1)).setInitiativeConfig(Mockito.any(InitiativeConfig.class));
        Mockito.verify(rankingErrorNotifierService, Mockito.times(onboardingRankingRequests.size())).notifyRankingOnboardingOutcome(Mockito.any(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any(Throwable.class));
    }

    @Test
    void testOnboardingNotifier_withInitiativeNfType_whenProducerReturnTrue(){
        // Given organizationId and initiativeID on DB for Status Publishing
        InitiativeConfig initiativeConfig = new InitiativeConfig();
        initiativeConfig.setRankingStatus(RankingStatus.READY);
        initiativeConfig.setBeneficiaryType(InitiativeGeneralDTO.BeneficiaryTypeEnum.NF);
        List<OnboardingRankingRequests> onboardingRankingRequests = new ArrayList<>();
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = Mockito.mock(OnboardingRankingRequest2EvaluationMapper.class);
        OnboardingNotifierProducer onboardingNotifierProducer = Mockito.mock(OnboardingNotifierProducer.class);
        RankingContextHolderService rankingContextHolderService = Mockito.mock(RankingContextHolderService.class);
        RankingErrorNotifierService rankingErrorNotifierService = Mockito.mock(RankingErrorNotifierService.class);

        EvaluationRankingDTO evaluationDTO = EvaluationRankingDTOFaker.mockInstance(1);
        OnboardingRankingRequests onboardingRankingRequests1 = OnboardingRankingRequestsFaker.mockInstance(1);
        onboardingRankingRequests1.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_OK);
        onboardingRankingRequests1.setFamilyId("FAMILYID_" + onboardingRankingRequests1.getUserId());
        onboardingRankingRequests1.setMemberIds(Set.of(onboardingRankingRequests1.getUserId(),
                onboardingRankingRequests1.getUserId() + "MEMBER2"));

        OnboardingRankingRequests onboardingRankingRequests2 = OnboardingRankingRequestsFaker.mockInstance(2);
        onboardingRankingRequests2.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_OK);
        onboardingRankingRequests2.setFamilyId("FAMILYID_" + onboardingRankingRequests2.getUserId());
        onboardingRankingRequests2.setMemberIds(Set.of(onboardingRankingRequests2.getUserId(),
                onboardingRankingRequests2.getUserId() + "MEMBER2"));

        onboardingRankingRequests.add(onboardingRankingRequests1);
        onboardingRankingRequests.add(onboardingRankingRequests2);

        Mockito.when(mapper.apply(Mockito.any(OnboardingRankingRequests.class), Mockito.any(InitiativeConfig.class))).thenReturn(evaluationDTO);
        Mockito.when(onboardingNotifierProducer.notify(Mockito.any(EvaluationRankingDTO.class))).thenReturn(true);

        OnboardingNotifierServiceImpl onboardingNotifierService = new OnboardingNotifierServiceImpl(onboardingNotifierProducer, mapper, rankingContextHolderService, rankingErrorNotifierService);
        onboardingNotifierService.callOnboardingNotifier(initiativeConfig, onboardingRankingRequests);

        Mockito.verify(mapper, Mockito.times(onboardingRankingRequests.size())).apply(Mockito.any(OnboardingRankingRequests.class), Mockito.any(InitiativeConfig.class));
        Mockito.verify(onboardingNotifierProducer, Mockito.times(onboardingRankingRequests.size()*2)).notify(Mockito.any(EvaluationRankingDTO.class)); //2 members for single family
        Mockito.verify(rankingContextHolderService, Mockito.times(1)).setInitiativeConfig(Mockito.any(InitiativeConfig.class));
        Mockito.verify(rankingErrorNotifierService, Mockito.never()).notifyRankingOnboardingOutcome(Mockito.any(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any(Throwable.class));

    }

    @Test
    void testOnboardingNotifier_withInitiativeNfType_whenProducerReturnFalse_thenThrowExceptionInMethod(){
        // Given organizationId and initiativeID on DB for Status Publishing
        InitiativeConfig initiativeConfig = new InitiativeConfig();
        initiativeConfig.setRankingStatus(RankingStatus.READY);
        initiativeConfig.setBeneficiaryType(InitiativeGeneralDTO.BeneficiaryTypeEnum.NF);
        List<OnboardingRankingRequests> onboardingRankingRequests = new ArrayList<>();
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = Mockito.mock(OnboardingRankingRequest2EvaluationMapper.class);
        OnboardingNotifierProducer onboardingNotifierProducer = Mockito.mock(OnboardingNotifierProducer.class);
        RankingContextHolderService rankingContextHolderService = Mockito.mock(RankingContextHolderService.class);
        RankingErrorNotifierService rankingErrorNotifierService = Mockito.mock(RankingErrorNotifierService.class);

        EvaluationRankingDTO evaluationDTO = EvaluationRankingDTOFaker.mockInstance(1);
        OnboardingRankingRequests onboardingRankingRequests1 = OnboardingRankingRequestsFaker.mockInstance(1);
        onboardingRankingRequests1.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_OK);
        onboardingRankingRequests1.setFamilyId("FAMILYID_" + onboardingRankingRequests1.getUserId());
        onboardingRankingRequests1.setMemberIds(Set.of(onboardingRankingRequests1.getUserId(),
                onboardingRankingRequests1.getUserId() + "MEMBER2"));

        OnboardingRankingRequests onboardingRankingRequests2 = OnboardingRankingRequestsFaker.mockInstance(2);
        onboardingRankingRequests2.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_OK);
        onboardingRankingRequests2.setFamilyId("FAMILYID_" + onboardingRankingRequests2.getUserId());
        onboardingRankingRequests2.setMemberIds(Set.of(onboardingRankingRequests2.getUserId(),
                onboardingRankingRequests2.getUserId() + "MEMBER2"));

        onboardingRankingRequests.add(onboardingRankingRequests1);
        onboardingRankingRequests.add(onboardingRankingRequests2);

        Mockito.when(mapper.apply(Mockito.any(OnboardingRankingRequests.class), Mockito.any(InitiativeConfig.class))).thenReturn(evaluationDTO);
        Mockito.when(onboardingNotifierProducer.notify(Mockito.any(EvaluationRankingDTO.class))).thenReturn(false);

        OnboardingNotifierServiceImpl onboardingNotifierService = new OnboardingNotifierServiceImpl(onboardingNotifierProducer, mapper, rankingContextHolderService, rankingErrorNotifierService);
        onboardingNotifierService.callOnboardingNotifier(initiativeConfig, onboardingRankingRequests);

        Mockito.verify(mapper, Mockito.times(onboardingRankingRequests.size())).apply(Mockito.any(OnboardingRankingRequests.class), Mockito.any(InitiativeConfig.class));
        Mockito.verify(onboardingNotifierProducer, Mockito.times(onboardingRankingRequests.size()*2)).notify(Mockito.any(EvaluationRankingDTO.class));
        Mockito.verify(rankingContextHolderService, Mockito.times(1)).setInitiativeConfig(Mockito.any(InitiativeConfig.class));
        Mockito.verify(rankingErrorNotifierService, Mockito.times(onboardingRankingRequests.size()*2)).notifyRankingOnboardingOutcome(Mockito.any(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any(Throwable.class));
    }
}
