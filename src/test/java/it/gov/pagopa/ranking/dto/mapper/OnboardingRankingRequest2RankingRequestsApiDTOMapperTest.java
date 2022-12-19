package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.controller.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import it.gov.pagopa.ranking.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OnboardingRankingRequest2RankingRequestsApiDTOMapperTest {
    @Test
    void test() {
        // Given
        OnboardingRankingRequest2RankingRequestsApiDTOMapper mapper = new OnboardingRankingRequest2RankingRequestsApiDTOMapper();

        OnboardingRankingRequests request = OnboardingRankingRequestsFaker.mockInstance(1);
        request.setRankingValue(-1);
        request.setRankingValueOriginal(-1);

        // When
        RankingRequestsApiDTO result = mapper.apply(request);

        // Then
        Assertions.assertNotNull(request);
        TestUtils.checkNotNullFields(result, "rankingFilePath");
        checkResult(request, result);

    }

    private void checkResult(OnboardingRankingRequests expected, RankingRequestsApiDTO result) {
        Assertions.assertEquals(expected.getUserId(), result.getUserId());
        Assertions.assertEquals(expected.getInitiativeId(), result.getInitiativeId());
        Assertions.assertEquals(expected.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(expected.getAdmissibilityCheckDate(), result.getAdmissibilityCheckDate());
        Assertions.assertEquals(expected.getCriteriaConsensusTimestamp(), result.getCriteriaConsensusTimestamp());
        Assertions.assertEquals(expected.getRankingValue2Show(), result.getRankingValue());
        Assertions.assertEquals(expected.getRank(), result.getRanking());
    }

}