package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.connector.rest.pdv.UserRestService;
import it.gov.pagopa.ranking.dto.csv.RankingCsvDTO;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.User;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import it.gov.pagopa.ranking.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class OnboardingRankingRequests2RankingCsvDTOMapperTest {

    UserRestService userRestService = Mockito.mock(UserRestService.class);
    private final OnboardingRankingRequests2RankingCsvDTOMapper mapper = new OnboardingRankingRequests2RankingCsvDTOMapper(userRestService);

    @Test
    void test() {
        OnboardingRankingRequests request = OnboardingRankingRequestsFaker.mockInstance(1);

        Mockito.when(userRestService.getUser(request.getUserId())).thenReturn(User.builder().fiscalCode("FISCALCODE_1").build());

        RankingCsvDTO result = mapper.apply(request);

        Assertions.assertNotNull(result);
        TestUtils.checkNotNullFields(result);
        Assertions.assertEquals("FISCALCODE_1", result.getFiscalCode());
        Assertions.assertEquals(request.getCriteriaConsensusTimestamp(), result.getCriteriaConsensusTimestamp());
        Assertions.assertEquals(request.getRankingValue(), result.getRankingValue());
        Assertions.assertEquals(request.getRank(), result.getRank());
        Assertions.assertEquals(request.getBeneficiaryRankingStatus(), result.getStatus());
    }

}