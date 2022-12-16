package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.csv.RankingCsvDTO;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import it.gov.pagopa.ranking.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OnboardingRankingRequests2RankingCsvDTOMapperTest {

    private final OnboardingRankingRequests2RankingCsvDTOMapper mapper = new OnboardingRankingRequests2RankingCsvDTOMapper();

    @Test
    void test() {
        OnboardingRankingRequests request = OnboardingRankingRequestsFaker.mockInstance(1);

        RankingCsvDTO result = mapper.apply(request);

        Assertions.assertNotNull(result);
        TestUtils.checkNotNullFields(result);
        Assertions.assertEquals(request.getUserId(), result.getUserId());
        Assertions.assertEquals(request.getCriteriaConsensusTimestamp(), result.getCriteriaConsensusTimestamp());
        Assertions.assertEquals(request.getRankingValue(), result.getRankingValue());
        Assertions.assertEquals(request.getRank(), result.getRank());
        Assertions.assertEquals(request.getBeneficiaryRankingStatus(), result.getStatus());
    }

}