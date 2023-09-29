package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

class OnboardingRankingRequestsServiceImplTest {

    @Test
    void save() {
        // Given
        OnboardingRankingRequestsRepository onboardingRankingRequestsRepositoryMock = Mockito.mock(OnboardingRankingRequestsRepository.class);
        OnboardingRankingRequestsService onboardingRankingRequestsService = new OnboardingRankingRequestsServiceImpl(onboardingRankingRequestsRepositoryMock);

        OnboardingRankingRequests requests = OnboardingRankingRequestsFaker.mockInstance(1);
        Mockito.when(onboardingRankingRequestsRepositoryMock.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);

        // When
        OnboardingRankingRequests result = onboardingRankingRequestsService.save(requests);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(requests.getId(), result.getId());
        Assertions.assertEquals(requests.getUserId(), result.getUserId());
        Assertions.assertEquals(requests.getInitiativeId(), result.getInitiativeId());
        Assertions.assertEquals(requests.getAdmissibilityCheckDate(), result.getAdmissibilityCheckDate());
        Assertions.assertEquals(requests.getCriteriaConsensusTimestamp(), result.getCriteriaConsensusTimestamp());
        Assertions.assertEquals(requests.getRankingValue(), result.getRankingValue());
    }

    @Test
    void deleteByInitiativeId() {
        // Given
        OnboardingRankingRequestsRepository onboardingRankingRequestsRepositoryMock = Mockito.mock(OnboardingRankingRequestsRepository.class);
        OnboardingRankingRequestsService onboardingRankingRequestsService = new OnboardingRankingRequestsServiceImpl(onboardingRankingRequestsRepositoryMock);

        String initiativeId = "InitiativeId";
        int pageSize = 100;
        OnboardingRankingRequests onboardingRankingRequest = OnboardingRankingRequestsFaker.mockInstance(1);
        onboardingRankingRequest.setInitiativeId(initiativeId);
        Mockito.when(onboardingRankingRequestsRepositoryMock.deletePaged(initiativeId, pageSize))
                        .thenReturn(List.of(onboardingRankingRequest));

        // When
        List<OnboardingRankingRequests> result = onboardingRankingRequestsService.deletePaged(initiativeId, pageSize);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(onboardingRankingRequest.getId(), result.get(0).getId());
        Assertions.assertEquals(onboardingRankingRequest.getUserId(), result.get(0).getUserId());
        Assertions.assertEquals(onboardingRankingRequest.getInitiativeId(), result.get(0).getInitiativeId());
        Assertions.assertEquals(onboardingRankingRequest.getAdmissibilityCheckDate(), result.get(0).getAdmissibilityCheckDate());
        Assertions.assertEquals(onboardingRankingRequest.getCriteriaConsensusTimestamp(), result.get(0).getCriteriaConsensusTimestamp());
        Assertions.assertEquals(onboardingRankingRequest.getRankingValue(), result.get(0).getRankingValue());
        Mockito.verify(onboardingRankingRequestsRepositoryMock, Mockito.times(1)).deletePaged(initiativeId, pageSize);
    }
}