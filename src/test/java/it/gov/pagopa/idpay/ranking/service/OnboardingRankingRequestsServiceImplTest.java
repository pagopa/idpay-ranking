package it.gov.pagopa.idpay.ranking.service;

import it.gov.pagopa.idpay.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.idpay.ranking.repository.OnboardingRankingRequestsRepository;
import it.gov.pagopa.idpay.ranking.test.fakers.OnboardingRankingRequestsFaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
}