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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.support.MessageBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

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

    private static final String ONBOARDING_RANKING_REQUEST_ID = "REQUEST_ID";
    private static final String OPERATION_TYPE_DELETE_INITIATIVE = "DELETE_INITIATIVE";
    private static final String INITIATIVE_ID = "TEST_INITIATIVE_ID";
    private static final String PAGINATION_KEY = "pagination";
    private static final String PAGINATION_VALUE = "100";
    private static final String DELAY_KEY = "delay";
    private static final String DELAY_VALUE = "1500";

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

    @ParameterizedTest
    @MethodSource("operationTypeAndInvocationTimes")
    void processCommand(String operationType, int times) {
        // Given
        Map<String, String> additionalParams = new HashMap<>();
        additionalParams.put(PAGINATION_KEY, PAGINATION_VALUE);
        additionalParams.put(DELAY_KEY, DELAY_VALUE);
        final QueueCommandOperationDTO queueCommandOperationDTO = QueueCommandOperationDTO.builder()
                .entityId(INITIATIVE_ID)
                .operationType(operationType)
                .operationTime(LocalDateTime.now().minusMinutes(5))
                .additionalParams(additionalParams)
                .build();
        OnboardingRankingRequests onboardingRankingRequest = OnboardingRankingRequests.builder()
                .id(ONBOARDING_RANKING_REQUEST_ID)
                .initiativeId(INITIATIVE_ID)
                .build();
        InitiativeConfig initiativeConfig = InitiativeConfig.builder()
                .initiativeId(INITIATIVE_ID)
                .initiativeName("InitiativeName")
                .build();
        final List<OnboardingRankingRequests> deletedPage = List.of(onboardingRankingRequest);

        if(times == 2){
            final List<OnboardingRankingRequests> onboardingRankingRequestPage = createOnboardingRankingRequestPage(Integer.parseInt(PAGINATION_VALUE));
            when(onboardingRankingRequestsServiceMock.deletePaged(queueCommandOperationDTO.getEntityId(), Integer.parseInt(queueCommandOperationDTO.getAdditionalParams().get(PAGINATION_KEY))))
                    .thenReturn(onboardingRankingRequestPage)
                    .thenReturn(deletedPage);
            Mockito.when(initiativeConfigServiceMock.deleteByInitiativeId(Mockito.any()))
                    .thenReturn(Optional.empty());
        } else if (times == 1) {
            when(onboardingRankingRequestsServiceMock.deletePaged(queueCommandOperationDTO.getEntityId(), Integer.parseInt(queueCommandOperationDTO.getAdditionalParams().get(PAGINATION_KEY))))
                    .thenReturn(deletedPage);
            Mockito.when(initiativeConfigServiceMock.deleteByInitiativeId(Mockito.any()))
                    .thenReturn(Optional.of(initiativeConfig));
        }

        // When
        if(times == 2){
            Thread.currentThread().interrupt();
        }
        initiativePersistenceMediator.processCommand(queueCommandOperationDTO);

        // Then
        Mockito.verify(initiativeConfigServiceMock, Mockito.times(times == 0 ? 0 : 1)).deleteByInitiativeId(queueCommandOperationDTO.getEntityId());
        Mockito.verify(onboardingRankingRequestsServiceMock, Mockito.times(times)).deletePaged(queueCommandOperationDTO.getEntityId(), Integer.parseInt(queueCommandOperationDTO.getAdditionalParams().get(PAGINATION_KEY)));
    }

    private static Stream<Arguments> operationTypeAndInvocationTimes() {
        return Stream.of(
                Arguments.of(OPERATION_TYPE_DELETE_INITIATIVE, 1),
                Arguments.of(OPERATION_TYPE_DELETE_INITIATIVE, 2),
                Arguments.of("OPERATION_TYPE_TEST", 0)
        );
    }

    private List<OnboardingRankingRequests> createOnboardingRankingRequestPage(int pageSize){
        List<OnboardingRankingRequests> onboardingRankingRequestPage = new ArrayList<>();

        for(int i=0;i<pageSize; i++){
            onboardingRankingRequestPage.add(OnboardingRankingRequests.builder()
                    .id(ONBOARDING_RANKING_REQUEST_ID+i)
                    .initiativeId(INITIATIVE_ID)
                    .build());
        }

        return onboardingRankingRequestPage;
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