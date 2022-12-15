package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.controller.RankingPageDTO;
import it.gov.pagopa.ranking.dto.controller.RankingRequestFilter;
import it.gov.pagopa.ranking.dto.controller.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequest2RankingRequestsApiDTOMapper;
import it.gov.pagopa.ranking.dto.mapper.PageOnboardingRequests2RankingPageDTOMapper;
import it.gov.pagopa.ranking.exception.ClientExceptionNoBody;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import it.gov.pagopa.ranking.service.initiative.InitiativeConfigService;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import it.gov.pagopa.ranking.test.fakers.RankingRequestsApiDTOFaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RankingRequestsApiServiceImplTest {

    @Mock private OnboardingRankingRequestsRepository requestsRepositoryMock;
    @Mock private RankingContextHolderService contextHolderServiceMock;
    @Mock private OnboardingNotifierService onboardingNotifierService;
    private final OnboardingRankingRequest2RankingRequestsApiDTOMapper mapper = new OnboardingRankingRequest2RankingRequestsApiDTOMapper();
    private final PageOnboardingRequests2RankingPageDTOMapper pageDtoMapper = new PageOnboardingRequests2RankingPageDTOMapper();

    private RankingRequestsApiService service;

    @BeforeEach
    void init() {
        service = new RankingRequestsApiServiceImpl(requestsRepositoryMock, mapper, pageDtoMapper, contextHolderServiceMock, onboardingNotifierService);
    }

    @Test
    void testOk() {
        // Given
        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setRankingStatus(RankingStatus.COMPLETED);
        Mockito.when(contextHolderServiceMock.getInitiativeConfig(initiativeConfig.getInitiativeId(), initiativeConfig.getOrganizationId()))
                .thenReturn(initiativeConfig);

        OnboardingRankingRequests request = OnboardingRankingRequestsFaker.mockInstance(1);
        Pageable pageable = PageRequest.of(0,10, Sort.by(OnboardingRankingRequests.Fields.rank));
        Mockito.when(requestsRepositoryMock.findAllBy(initiativeConfig.getInitiativeId(), new RankingRequestFilter(), pageable))
                .thenReturn(new PageImpl<>(List.of(request)));

        // When
        List<RankingRequestsApiDTO> results = service.findByInitiativeId(initiativeConfig.getOrganizationId(), initiativeConfig.getInitiativeId(), 0, 10, new RankingRequestFilter());

        // Then
        Assertions.assertFalse(results.isEmpty());
        RankingRequestsApiDTO result = results.get(0);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getRanking());
    }

    @Test
    void testOkWithStatusFilter() {
        // Given
        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setRankingStatus(RankingStatus.COMPLETED);
        Mockito.when(contextHolderServiceMock.getInitiativeConfig(initiativeConfig.getInitiativeId(), initiativeConfig.getOrganizationId()))
                .thenReturn(initiativeConfig);

        OnboardingRankingRequests request1 = OnboardingRankingRequestsFaker.mockInstance(1);
        request1.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_OK);
        OnboardingRankingRequests request2 = OnboardingRankingRequestsFaker.mockInstance(2);
        OnboardingRankingRequests request3 = OnboardingRankingRequestsFaker.mockInstance(3);
        request1.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_KO);

        Pageable pageable = PageRequest.of(0,10, Sort.by(OnboardingRankingRequests.Fields.rank));
        RankingRequestFilter rankingRequestFilter = RankingRequestFilter.builder().beneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_OK).userId(request1.getUserId()).build();
        Mockito.when(requestsRepositoryMock.findAllBy(initiativeConfig.getInitiativeId(), rankingRequestFilter, pageable))
                .thenReturn(new PageImpl<>(List.of(request1, request2, request3)));

        // When
        List<RankingRequestsApiDTO> results = service.findByInitiativeId(initiativeConfig.getOrganizationId(), initiativeConfig.getInitiativeId(), 0, 10, rankingRequestFilter);

        // Then
        Assertions.assertFalse(results.isEmpty());
        RankingRequestsApiDTO result = results.get(0);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getRanking());
    }

    @Test
    void testStatusWaitingEnd() {
        // Given
        LocalDate today = LocalDate.now();

        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setRankingStatus(RankingStatus.WAITING_END);
        Mockito.when(contextHolderServiceMock.getInitiativeConfig(initiativeConfig.getInitiativeId(), initiativeConfig.getOrganizationId()))
                .thenReturn(initiativeConfig);

        // When
        List<RankingRequestsApiDTO> results = service.findByInitiativeId(initiativeConfig.getOrganizationId(), initiativeConfig.getInitiativeId(), 0, 10, new RankingRequestFilter());

        // Then
        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void testInitiativeNotFound() {
        // Given

        Mockito.when(contextHolderServiceMock.getInitiativeConfig(Mockito.any(), Mockito.any()))
                .thenThrow(ClientExceptionNoBody.class);

        // When
        Executable executable = () -> service.findByInitiativeId("orgId", "initiativeId", 0, 10, new RankingRequestFilter());

        // Then
        Assertions.assertThrows(ClientExceptionNoBody.class, executable);
    }

    @Test
    void testPagedOk() {
        // Given
        LocalDateTime date = LocalDateTime.of(2022, 11, 1, 0, 0);

        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setRankingStatus(RankingStatus.COMPLETED);
        initiativeConfig.setRankingPublishedTimestamp(date);
        initiativeConfig.setRankingGeneratedTimestamp(date);
        Mockito.when(contextHolderServiceMock.getInitiativeConfig(initiativeConfig.getInitiativeId(), initiativeConfig.getOrganizationId()))
                .thenReturn(initiativeConfig);

        OnboardingRankingRequests request = OnboardingRankingRequestsFaker.mockInstanceBuilder(1)
                .admissibilityCheckDate(date)
                .criteriaConsensusTimestamp(date)
                .beneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_OK)
                .build();
        Pageable pageable = PageRequest.of(0,10, Sort.by(OnboardingRankingRequests.Fields.rank));
        Mockito.when(requestsRepositoryMock.findAllBy(initiativeConfig.getInitiativeId(), new RankingRequestFilter(), pageable))
                .thenReturn(new PageImpl<>(List.of(request)));

        List<RankingRequestsApiDTO> dtoList = List.of(RankingRequestsApiDTOFaker.mockInstanceBuilder(1)
                .admissibilityCheckDate(date)
                .criteriaConsensusTimestamp(date)
                .build()
        );

        RankingPageDTO expected = RankingPageDTO.builder()
                .content(dtoList)
                .pageNumber(0)
                .pageSize(1)
                .totalElements(1)
                .totalPages(1)
                .rankingStatus(RankingStatus.COMPLETED)
                .rankingPublishedTimestamp(initiativeConfig.getRankingPublishedTimestamp())
                .rankingGeneratedTimestamp(initiativeConfig.getRankingGeneratedTimestamp())
                .totalEligibleOk(0)
                .totalEligibleKo(0)
                .totalOnboardingKo(0)
                .build();

        // When
        RankingPageDTO result = service.findByInitiativeIdPaged(initiativeConfig.getOrganizationId(), initiativeConfig.getInitiativeId(), 0, 10, new RankingRequestFilter());

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testPagedStatusWaitingEnd() {
        // Given
        LocalDate today = LocalDate.now();

        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setRankingStatus(RankingStatus.WAITING_END);
        Mockito.when(contextHolderServiceMock.getInitiativeConfig(initiativeConfig.getInitiativeId(), initiativeConfig.getOrganizationId()))
                .thenReturn(initiativeConfig);

        // When
        RankingPageDTO result = service.findByInitiativeIdPaged(initiativeConfig.getOrganizationId(), initiativeConfig.getInitiativeId(), 0, 10, new RankingRequestFilter());

        // Then
        Assertions.assertTrue(result.getContent().isEmpty());
    }

    @Test
    void testPagedInitiativeNotFound() {
        // Given

        Mockito.when(contextHolderServiceMock.getInitiativeConfig(Mockito.any(), Mockito.any()))
                .thenThrow(ClientExceptionNoBody.class);

        // When
        Executable executable = () -> service.findByInitiativeIdPaged("orgId", "initiativeId", 0, 10, new RankingRequestFilter());

        // Then
        Assertions.assertThrows(ClientExceptionNoBody.class, executable);
    }

    @Test
    void givenInitiativeInREADYStatus_thenNotifySuccess() {
        // Given organizationId and initiativeID on DB for Status Publishing
        InitiativeConfig initiativeConfig = new InitiativeConfig();
        initiativeConfig.setRankingStatus(RankingStatus.READY);

        // When
        Mockito.when(contextHolderServiceMock.getInitiativeConfig(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(initiativeConfig);
        List<OnboardingRankingRequests> onboardingRankingRequests = new ArrayList<>();
        OnboardingRankingRequests rankingRequests = new OnboardingRankingRequests();
        onboardingRankingRequests.add(rankingRequests);
        Mockito.when(requestsRepositoryMock.findAllByOrganizationIdAndInitiativeId(Mockito.any(), Mockito.any()))
                .thenReturn(onboardingRankingRequests);
        Mockito.doNothing().when(onboardingNotifierService).callOnboardingNotifier(initiativeConfig, onboardingRankingRequests);

        // Then
        Executable executable = () -> service.notifyCitizenRankings("orgId", "initiativeId");
        Assertions.assertDoesNotThrow(executable);
    }

    @Test
    void givenInitiativeNOTInREADYStatus_thenThrowException() {
        // Given organizationId and initiativeID on DB for Status Publishing
        InitiativeConfig initiativeConfig = new InitiativeConfig();
        initiativeConfig.setRankingStatus(RankingStatus.WAITING_END);

        // When
        Mockito.when(contextHolderServiceMock.getInitiativeConfig(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(initiativeConfig);

        // Then
        Executable executable = () -> service.notifyCitizenRankings("orgId", "initiativeId");
        IllegalStateException illegalStateException = Assertions.assertThrows(IllegalStateException.class, executable);
    }

}