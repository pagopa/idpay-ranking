package it.gov.pagopa.ranking.service.initiative;

import it.gov.pagopa.ranking.dto.initiative.InitiativeBuildDTO;
import it.gov.pagopa.ranking.dto.initiative.InitiativeGeneralDTO;
import it.gov.pagopa.ranking.dto.mapper.InitiativeBuild2ConfigMapper;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.RankingStatusEnum;
import it.gov.pagopa.ranking.service.ErrorNotifierService;
import it.gov.pagopa.ranking.service.RankingContextHolderService;
import it.gov.pagopa.ranking.test.fakers.Initiative2BuildDTOFaker;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.ranking.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.support.MessageBuilder;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class InitiativePersistenceMediatorImplTest {

    @Mock
    private InitiativeBuild2ConfigMapper initiativeBuild2ConfigMapperMock;

    @Mock
    private InitiativeConfigService initiativeConfigServiceMock;

    @Mock
    private RankingContextHolderService rankingContextHolderServiceMock;

    @Mock
    private ErrorNotifierService errorNotifierServiceMock;

    private InitiativePersistenceMediator initiativePersistenceMediator;

    @BeforeEach
    void setUp(){
        initiativePersistenceMediator = new InitiativePersistenceMediatorImpl("applicationName",
                initiativeBuild2ConfigMapperMock,
                initiativeConfigServiceMock,
                rankingContextHolderServiceMock,
                errorNotifierServiceMock,
                TestUtils.objectMapper);
    }

    @Test
    void executeNewInitiative(){
        // Given
        InitiativeBuildDTO initiativeRequest = Initiative2BuildDTOFaker.mockInstance(1);

        Mockito.when(initiativeConfigServiceMock.findById(initiativeRequest.getInitiativeId())).thenReturn(null);

        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        Mockito.when(initiativeBuild2ConfigMapperMock.apply(initiativeRequest)).thenReturn(initiativeConfig);

        Mockito.when(initiativeConfigServiceMock.save(initiativeConfig)).thenAnswer(i -> i.getArguments()[0]);
        // When
        initiativePersistenceMediator.execute(MessageBuilder.withPayload(TestUtils.jsonSerializer(initiativeRequest)).build());

        // Then
        Mockito.verify(initiativeConfigServiceMock).findById(Mockito.anyString());
        Mockito.verify(initiativeBuild2ConfigMapperMock).apply(Mockito.any());
        Mockito.verify(initiativeConfigServiceMock).save(Mockito.any());
        Mockito.verify(rankingContextHolderServiceMock).setInitiativeConfig(Mockito.any());
    }

    @Test
    void executeInitiativeRankingStatusReady(){
        // Given
        InitiativeBuildDTO initiativeRequest = Initiative2BuildDTOFaker.mockInstance(1);

        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setRankingStatus(RankingStatusEnum.RANKING_STATUS_READY);
        Mockito.when(initiativeConfigServiceMock.findById(initiativeRequest.getInitiativeId())).thenReturn(initiativeConfig);

        // When
        initiativePersistenceMediator.execute(MessageBuilder.withPayload(TestUtils.jsonSerializer(initiativeRequest)).build());

        // Then
        Mockito.verify(initiativeConfigServiceMock).findById(Mockito.anyString());
        Mockito.verify(initiativeBuild2ConfigMapperMock, Mockito.never()).apply(Mockito.any());
        Mockito.verify(initiativeConfigServiceMock,Mockito.never()).save(Mockito.any());
        Mockito.verify(rankingContextHolderServiceMock, Mockito.never()).setInitiativeConfig(Mockito.any());
    }

    @Test
    void executeInitiativeRankingStatusCompleted(){
        // Given
        InitiativeBuildDTO initiativeRequest = Initiative2BuildDTOFaker.mockInstance(1);

        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setRankingStatus(RankingStatusEnum.RANKING_STATUS_COMPLETED);
        Mockito.when(initiativeConfigServiceMock.findById(initiativeRequest.getInitiativeId())).thenReturn(initiativeConfig);

        // When
        initiativePersistenceMediator.execute(MessageBuilder.withPayload(TestUtils.jsonSerializer(initiativeRequest)).build());

        // Then
        Mockito.verify(initiativeConfigServiceMock).findById(Mockito.anyString());
        Mockito.verify(initiativeBuild2ConfigMapperMock, Mockito.never()).apply(Mockito.any());
        Mockito.verify(initiativeConfigServiceMock,Mockito.never()).save(Mockito.any());
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
                .rankingStatus(RankingStatusEnum.RANKING_STATUS_WAITING_END)
                .rankingStartDate(now)
                .rankingEndDate(now.plusMonths(7L))
                .build();
        Mockito.when(initiativeConfigServiceMock.findById(initiativeRequest.getInitiativeId())).thenReturn(initiativeConfig);

        InitiativeConfig expectedToSave = getInitiativeConfigExpected(initiativeRequest);
        Mockito.when(initiativeBuild2ConfigMapperMock.apply(initiativeRequest)).thenReturn(expectedToSave);
        Mockito.when(initiativeConfigServiceMock.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);


        // When
        initiativePersistenceMediator.execute(MessageBuilder.withPayload(TestUtils.jsonSerializer(initiativeRequest)).build());

        // Then
        Mockito.verify(initiativeConfigServiceMock).findById(Mockito.anyString());
        Mockito.verify(initiativeBuild2ConfigMapperMock).apply(Mockito.any());
        Mockito.verify(initiativeConfigServiceMock).save(expectedToSave);
        Mockito.verify(rankingContextHolderServiceMock).setInitiativeConfig(Mockito.any());
    }

    @Test
    void executeInitiativeRankingStatusBuilding(){
        // Given
        LocalDate now = LocalDate.now();

        InitiativeBuildDTO initiativeRequest = Initiative2BuildDTOFaker.mockInstance(1);
        InitiativeGeneralDTO general = initiativeRequest.getGeneral();
        general.setRankingStartDate(now);
        general.setRankingEndDate(now.plusMonths(3L));


        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstanceBuilder(1)
                .rankingStatus(RankingStatusEnum.RANKING_STATUS_BUILDING)
                .rankingStartDate(now)
                .rankingEndDate(now.plusMonths(7L))
                .build();
        Mockito.when(initiativeConfigServiceMock.findById(initiativeRequest.getInitiativeId())).thenReturn(initiativeConfig);

        InitiativeConfig expectedToSave = getInitiativeConfigExpected(initiativeRequest);
        Mockito.when(initiativeBuild2ConfigMapperMock.apply(initiativeRequest)).thenReturn(expectedToSave);
        Mockito.when(initiativeConfigServiceMock.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);


        // When
        initiativePersistenceMediator.execute(MessageBuilder.withPayload(TestUtils.jsonSerializer(initiativeRequest)).build());

        // Then
        Mockito.verify(initiativeConfigServiceMock).findById(Mockito.anyString());
        Mockito.verify(initiativeBuild2ConfigMapperMock).apply(Mockito.any());
        Mockito.verify(initiativeConfigServiceMock).save(expectedToSave);
        Mockito.verify(rankingContextHolderServiceMock).setInitiativeConfig(Mockito.any());
    }

    private InitiativeConfig getInitiativeConfigExpected(InitiativeBuildDTO initiativeRequest) {
        InitiativeConfig expectedToSave = InitiativeConfig.builder()
                .initiativeId(initiativeRequest.getInitiativeId())
                .initiativeName(initiativeRequest.getInitiativeName())
                .organizationId(initiativeRequest.getOrganizationId())
                .initiativeStatus(initiativeRequest.getStatus())
                .rankingStartDate(initiativeRequest.getGeneral().getRankingStartDate())
                .rankingEndDate(initiativeRequest.getGeneral().getRankingEndDate())
                .initiativeBudget(initiativeRequest.getGeneral().getBudget())
                .beneficiaryInitiativeBudget(initiativeRequest.getGeneral().getBeneficiaryBudget())
                .rankingStatus(!LocalDate.now().isAfter(initiativeRequest.getGeneral().getRankingEndDate()) ? RankingStatusEnum.RANKING_STATUS_WAITING_END
                        : RankingStatusEnum.RANKING_STATUS_BUILDING)
                .size(InitiativeBuild2ConfigMapper.calculateSize(initiativeRequest))
                .rankingFields(InitiativeBuild2ConfigMapper.retrieveRankingFieldCodes(initiativeRequest.getBeneficiaryRule().getAutomatedCriteria()))
                .build();
        return expectedToSave;
    }
}