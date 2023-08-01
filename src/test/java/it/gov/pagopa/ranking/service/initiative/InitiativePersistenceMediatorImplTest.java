package it.gov.pagopa.ranking.service.initiative;

import it.gov.pagopa.ranking.dto.event.QueueCommandOperationDTO;
import it.gov.pagopa.ranking.dto.initiative.InitiativeBuildDTO;
import it.gov.pagopa.ranking.dto.initiative.InitiativeGeneralDTO;
import it.gov.pagopa.ranking.dto.mapper.InitiativeBuild2ConfigMapper;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.service.OnboardingRankingRequestsService;
import it.gov.pagopa.ranking.service.RankingErrorNotifierService;
import it.gov.pagopa.ranking.service.RankingContextHolderService;
import it.gov.pagopa.ranking.test.fakers.Initiative2BuildDTOFaker;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.common.utils.TestUtils;
import it.gov.pagopa.ranking.utils.AuditUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.support.MessageBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class InitiativePersistenceMediatorImplTest {

    @Mock
    private InitiativeBuild2ConfigMapper initiativeBuild2ConfigMapperMock;

    @Mock
    private InitiativeConfigService initiativeConfigServiceMock;

    @Mock
    private RankingContextHolderService rankingContextHolderServiceMock;

    @Mock
    private RankingErrorNotifierService rankingErrorNotifierServiceMock;

    @Mock
    private OnboardingRankingRequestsService onboardingRankingRequestsServiceMock;

    private InitiativePersistenceMediator initiativePersistenceMediator;

    @Mock
    private AuditUtilities auditUtilities;

    @BeforeEach
    void setUp(){
        initiativePersistenceMediator = new InitiativePersistenceMediatorImpl("applicationName",
                initiativeBuild2ConfigMapperMock,
                initiativeConfigServiceMock,
                rankingContextHolderServiceMock,
                rankingErrorNotifierServiceMock,
                onboardingRankingRequestsServiceMock,
                auditUtilities,

                TestUtils.objectMapper);
    }

    @Test
    void executeNewInitiative(){
        // Given
        InitiativeBuildDTO initiativeRequest = Initiative2BuildDTOFaker.mockInstance(1);

        Mockito.when(initiativeConfigServiceMock.findById(initiativeRequest.getInitiativeId())).thenReturn(null);

        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        Mockito.when(initiativeBuild2ConfigMapperMock.apply(initiativeRequest)).thenReturn(initiativeConfig);

        // When
        initiativePersistenceMediator.execute(MessageBuilder.withPayload(TestUtils.jsonSerializer(initiativeRequest)).build());

        // Then
        Mockito.verify(initiativeConfigServiceMock).findById(Mockito.anyString());
        Mockito.verify(initiativeBuild2ConfigMapperMock).apply(Mockito.any());
        Mockito.verify(rankingContextHolderServiceMock).setInitiativeConfig(Mockito.any());
    }

    @Test
    void executeInitiativeRankingStatusReady(){
        // Given
        InitiativeBuildDTO initiativeRequest = Initiative2BuildDTOFaker.mockInstance(1);

        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setRankingStatus(RankingStatus.READY);
        Mockito.when(initiativeConfigServiceMock.findById(initiativeRequest.getInitiativeId())).thenReturn(initiativeConfig);

        // When
        initiativePersistenceMediator.execute(MessageBuilder.withPayload(TestUtils.jsonSerializer(initiativeRequest)).build());

        // Then
        Mockito.verify(initiativeConfigServiceMock).findById(Mockito.anyString());
        Mockito.verify(initiativeBuild2ConfigMapperMock, Mockito.never()).apply(Mockito.any());
        Mockito.verify(rankingContextHolderServiceMock, Mockito.never()).setInitiativeConfig(Mockito.any());
    }

    @Test
    void executeInitiativeRankingStatusCompleted(){
        // Given
        InitiativeBuildDTO initiativeRequest = Initiative2BuildDTOFaker.mockInstance(1);

        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setRankingStatus(RankingStatus.COMPLETED);
        Mockito.when(initiativeConfigServiceMock.findById(initiativeRequest.getInitiativeId())).thenReturn(initiativeConfig);

        // When
        initiativePersistenceMediator.execute(MessageBuilder.withPayload(TestUtils.jsonSerializer(initiativeRequest)).build());

        // Then
        Mockito.verify(initiativeConfigServiceMock).findById(Mockito.anyString());
        Mockito.verify(initiativeBuild2ConfigMapperMock, Mockito.never()).apply(Mockito.any());
        Mockito.verify(rankingContextHolderServiceMock, Mockito.never()).setInitiativeConfig(Mockito.any());
    }

    @Test
    void executeInitiativeRankingStatusWaitingEnd(){
        // Given
        LocalDate now = LocalDate.now();

        InitiativeBuildDTO initiativeRequest = Initiative2BuildDTOFaker.mockInstance(1);
        InitiativeGeneralDTO general = initiativeRequest.getGeneral();
        general.setRankingStartDate(now);
        general.setRankingEndDate(now.plusMonths(3L));


        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstanceBuilder(1)
                .rankingStatus(RankingStatus.WAITING_END)
                .build();
        Mockito.when(initiativeConfigServiceMock.findById(initiativeRequest.getInitiativeId())).thenReturn(initiativeConfig);

        InitiativeConfig expectedToSave = getInitiativeConfigExpected(initiativeRequest);
        Mockito.when(initiativeBuild2ConfigMapperMock.apply(initiativeRequest)).thenReturn(expectedToSave);


        // When
        initiativePersistenceMediator.execute(MessageBuilder.withPayload(TestUtils.jsonSerializer(initiativeRequest)).build());

        // Then
        Mockito.verify(initiativeConfigServiceMock).findById(Mockito.anyString());
        Mockito.verify(initiativeBuild2ConfigMapperMock).apply(Mockito.any());
        Mockito.verify(rankingContextHolderServiceMock).setInitiativeConfig(Mockito.any());
    }

    @Test
    void executeInitiativeRankingStatusPublishing(){
        // Given
        LocalDate now = LocalDate.now();

        InitiativeBuildDTO initiativeRequest = Initiative2BuildDTOFaker.mockInstance(1);
        InitiativeGeneralDTO general = initiativeRequest.getGeneral();
        general.setRankingStartDate(now);
        general.setRankingEndDate(now.plusMonths(3L));


        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstanceBuilder(1)
                .rankingStatus(RankingStatus.PUBLISHING)
                .rankingStartDate(now)
                .rankingEndDate(now.plusMonths(7L))
                .build();
        Mockito.when(initiativeConfigServiceMock.findById(initiativeRequest.getInitiativeId())).thenReturn(initiativeConfig);
        // When
        initiativePersistenceMediator.execute(MessageBuilder.withPayload(TestUtils.jsonSerializer(initiativeRequest)).build());

        // Then
        Mockito.verify(initiativeConfigServiceMock).findById(Mockito.anyString());
        Mockito.verify(initiativeBuild2ConfigMapperMock, Mockito.never()).apply(Mockito.any());
        Mockito.verify(rankingContextHolderServiceMock, Mockito.never()).setInitiativeConfig(Mockito.any());
    }

    @Test
    void processCommand_commandNotDeleteInitiative(){
        // Given
        QueueCommandOperationDTO queueCommandOperationDTO = QueueCommandOperationDTO.builder()
                .operationId("OperationId")
                .operationType("NOT_DELETE_INITIATIVE")
                .build();

        // When
        initiativePersistenceMediator.processCommand(queueCommandOperationDTO);

        // Then
        Mockito.verify(initiativeConfigServiceMock, Mockito.never()).deleteByInitiativeId(queueCommandOperationDTO.getOperationId());
        Mockito.verify(onboardingRankingRequestsServiceMock, Mockito.never()).deleteByInitiativeId(queueCommandOperationDTO.getOperationId());
    }

    @Test
    void processCommand_emptyDeletedInitiativeConfig(){
        // Given
        QueueCommandOperationDTO queueCommandOperationDTO = QueueCommandOperationDTO.builder()
                .operationId("OperationId")
                .operationType("DELETE_INITIATIVE")
                .build();
        Mockito.when(initiativeConfigServiceMock.deleteByInitiativeId(Mockito.any()))
                .thenReturn(Optional.empty());

        // When
        initiativePersistenceMediator.processCommand(queueCommandOperationDTO);

        // Then
        Mockito.verify(initiativeConfigServiceMock, Mockito.times(1)).deleteByInitiativeId(queueCommandOperationDTO.getOperationId());
        Mockito.verify(onboardingRankingRequestsServiceMock, Mockito.times(1)).deleteByInitiativeId(queueCommandOperationDTO.getOperationId());

    }

    @Test
    void processCommand(){
        // Given
        QueueCommandOperationDTO queueCommandOperationDTO = QueueCommandOperationDTO.builder()
                .operationId("OperationId")
                .operationType("DELETE_INITIATIVE")
                .build();
        InitiativeConfig initiativeConfig = InitiativeConfig.builder()
                .initiativeId(queueCommandOperationDTO.getOperationId())
                .initiativeName("InitiativeName")
                .build();
        OnboardingRankingRequests onboardingRankingRequests = OnboardingRankingRequests.builder()
                .id("Id")
                .userId("UserId")
                .initiativeId(queueCommandOperationDTO.getOperationId())
                .organizationId("OrganizationId")
                .build();
        Mockito.when(initiativeConfigServiceMock.deleteByInitiativeId(Mockito.any()))
                .thenReturn(Optional.of(initiativeConfig));
        Mockito.when(onboardingRankingRequestsServiceMock.deleteByInitiativeId(Mockito.any()))
                .thenReturn(List.of(onboardingRankingRequests));

        // When
        initiativePersistenceMediator.processCommand(queueCommandOperationDTO);

        // Then
        Mockito.verify(initiativeConfigServiceMock, Mockito.times(1)).deleteByInitiativeId(queueCommandOperationDTO.getOperationId());
        Mockito.verify(onboardingRankingRequestsServiceMock, Mockito.times(1)).deleteByInitiativeId(queueCommandOperationDTO.getOperationId());
    }

    private InitiativeConfig getInitiativeConfigExpected(InitiativeBuildDTO initiativeRequest) {
        return InitiativeConfig.builder()
                .initiativeId(initiativeRequest.getInitiativeId())
                .initiativeName(initiativeRequest.getInitiativeName())
                .organizationId(initiativeRequest.getOrganizationId())
                .initiativeStatus(initiativeRequest.getStatus())
                .rankingStartDate(initiativeRequest.getGeneral().getRankingStartDate())
                .rankingEndDate(initiativeRequest.getGeneral().getRankingEndDate())
                .initiativeBudget(initiativeRequest.getGeneral().getBudget())
                .beneficiaryInitiativeBudget(initiativeRequest.getGeneral().getBeneficiaryBudget())
                .rankingStatus(RankingStatus.WAITING_END)
                .size(InitiativeBuild2ConfigMapper.calculateSize(initiativeRequest))
                .rankingFields(InitiativeBuild2ConfigMapper.retrieveRankingFieldCodes(initiativeRequest.getBeneficiaryRule().getAutomatedCriteria()))
                .build();
    }
}