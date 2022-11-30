package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequest2RankingRequestsApiDTOMapper;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RankingRequestsApiServiceImplTest {

    @Mock private OnboardingRankingRequestsRepository requestsRepositoryMock;
    @Mock private RankingContextHolderService contextHolderServiceMock;
    private final OnboardingRankingRequest2RankingRequestsApiDTOMapper mapper = new OnboardingRankingRequest2RankingRequestsApiDTOMapper();

    private RankingRequestsApiService service;

    @BeforeEach
    void init() {
        service = new RankingRequestsApiServiceImpl(requestsRepositoryMock, mapper, contextHolderServiceMock);
    }

    @Test
    void testOk() {
        // Given
        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setRankingStatus(RankingStatus.COMPLETED);
        Mockito.when(contextHolderServiceMock.getInitiativeConfig(initiativeConfig.getInitiativeId(), initiativeConfig.getOrganizationId()))
                .thenReturn(initiativeConfig);

        OnboardingRankingRequests request = OnboardingRankingRequestsFaker.mockInstance(1);
        Pageable pageable = PageRequest.of(0,10, Sort.by("rank"));
        Mockito.when(requestsRepositoryMock.findByInitiativeId(initiativeConfig.getInitiativeId(), pageable))
                .thenReturn(List.of(request));

        // When
        List<RankingRequestsApiDTO> results = service.findByInitiativeId(initiativeConfig.getOrganizationId(), initiativeConfig.getInitiativeId(), 0, 10);

        // Then
        Assertions.assertFalse(results.isEmpty());
        RankingRequestsApiDTO result = results.get(0);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getRanking());
    }

    @Test
    void testInitiativeExpired() {
        // Given
        LocalDate today = LocalDate.now();

        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setRankingEndDate(today.minusDays(2));
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

}