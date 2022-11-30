package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.RankingPageDTO;
import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequest2RankingRequestsApiDTOMapper;
import it.gov.pagopa.ranking.dto.mapper.PageOnboardingRequests2RankingPageDTOMapper;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import it.gov.pagopa.ranking.test.fakers.RankingRequestsApiDTOFaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RankingRequestsApiServiceImplTest {

    @Mock private OnboardingRankingRequestsRepository requestsRepositoryMock;
    @Mock private RankingContextHolderService contextHolderServiceMock;
    private final OnboardingRankingRequest2RankingRequestsApiDTOMapper mapper = new OnboardingRankingRequest2RankingRequestsApiDTOMapper();
    private final PageOnboardingRequests2RankingPageDTOMapper pageDtoMapper = new PageOnboardingRequests2RankingPageDTOMapper();

    private RankingRequestsApiService service;

    @BeforeEach
    void init() {
        service = new RankingRequestsApiServiceImpl(requestsRepositoryMock, mapper, pageDtoMapper, contextHolderServiceMock);
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
        Mockito.when(requestsRepositoryMock.findByInitiativeId(initiativeConfig.getInitiativeId(), pageable))
                .thenReturn(new PageImpl<>(List.of(request)));

        // When
        List<RankingRequestsApiDTO> results = service.findByInitiativeId(initiativeConfig.getOrganizationId(), initiativeConfig.getInitiativeId(), 0, 10);

        // Then
        Assertions.assertFalse(results.isEmpty());
        RankingRequestsApiDTO result = results.get(0);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRanking());
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
        List<RankingRequestsApiDTO> results = service.findByInitiativeId(initiativeConfig.getOrganizationId(), initiativeConfig.getInitiativeId(), 0, 10);

        // Then
        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void testInitiativeNull() {
        // Given

        Mockito.when(contextHolderServiceMock.getInitiativeConfig(Mockito.any(), Mockito.any()))
                .thenReturn(null);

        // When
        List<RankingRequestsApiDTO> results = service.findByInitiativeId("orgId", "initiativeId", 0, 10);

        // Then
        Assertions.assertNull(results);
    }

    @Test
    void testPagedOk() {
        // Given
        LocalDateTime date = LocalDateTime.of(2022, 11, 1, 0, 0);

        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setRankingStatus(RankingStatus.COMPLETED);
        initiativeConfig.setRankingPublishedTimeStamp(date);
        initiativeConfig.setRankingGeneratedTimeStamp(date);
        Mockito.when(contextHolderServiceMock.getInitiativeConfig(initiativeConfig.getInitiativeId(), initiativeConfig.getOrganizationId()))
                .thenReturn(initiativeConfig);

        OnboardingRankingRequests request = OnboardingRankingRequestsFaker.mockInstanceBuilder(1)
                .admissibilityCheckDate(date)
                .criteriaConsensusTimestamp(date)
                .build();
        Pageable pageable = PageRequest.of(0,10, Sort.by(OnboardingRankingRequests.Fields.rank));
        Mockito.when(requestsRepositoryMock.findByInitiativeId(initiativeConfig.getInitiativeId(), pageable))
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
                .rankingPublishedTimeStamp(initiativeConfig.getRankingPublishedTimeStamp())
                .rankingGeneratedTimeStamp(initiativeConfig.getRankingGeneratedTimeStamp())
                .totalEligibleOk(0)
                .totalEligibleKo(0)
                .build();

        // When
        RankingPageDTO result = service.findByInitiativeIdPaged(initiativeConfig.getOrganizationId(), initiativeConfig.getInitiativeId(), 0, 10);

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
        RankingPageDTO result = service.findByInitiativeIdPaged(initiativeConfig.getOrganizationId(), initiativeConfig.getInitiativeId(), 0, 10);

        // Then
        Assertions.assertTrue(result.getContent().isEmpty());
    }

    @Test
    void testPagedInitiativeNull() {
        // Given

        Mockito.when(contextHolderServiceMock.getInitiativeConfig(Mockito.any(), Mockito.any()))
                .thenReturn(null);

        // When
        RankingPageDTO result = service.findByInitiativeIdPaged("orgId", "initiativeId", 0, 10);

        // Then
        Assertions.assertNull(result);
    }

}