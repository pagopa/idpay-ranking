package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import it.gov.pagopa.ranking.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OnboardingRankingRequest2RankingRequestsApiDTOMapperTest {
    private static final String ORGANIZATION_ID = "organizationId_1";

    @Test
    void test() {
        // Given
        OnboardingRankingRequest2RankingRequestsApiDTOMapper mapper = new OnboardingRankingRequest2RankingRequestsApiDTOMapper();

        OnboardingRankingRequests request = OnboardingRankingRequestsFaker.mockInstance(1);

        // When
        RankingRequestsApiDTO result = mapper.apply(request, ORGANIZATION_ID);

        // Then
        Assertions.assertNotNull(request);
        TestUtils.checkNotNullFields(result);
        checkResult(request, result);

    }

    private void checkResult(OnboardingRankingRequests expected, RankingRequestsApiDTO result) {
        Assertions.assertEquals(expected.getUserId(), result.getUserId());
        Assertions.assertEquals(expected.getInitiativeId(), result.getInitiativeId());
        Assertions.assertEquals(ORGANIZATION_ID, result.getOrganizationId());
        Assertions.assertEquals(expected.getAdmissibilityCheckDate(), result.getAdmissibilityCheckDate());
        Assertions.assertEquals(expected.getCriteriaConsensusTimestamp(), result.getCriteriaConsensusTimestamp());
        Assertions.assertEquals(expected.getRankingValue(), result.getRankingValue());
        Assertions.assertEquals(expected.getRank(), result.getRank());
    }

}