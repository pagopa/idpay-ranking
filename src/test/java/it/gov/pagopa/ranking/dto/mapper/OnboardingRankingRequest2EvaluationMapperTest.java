package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.event.EvaluationRankingDTO;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OnboardingRankingRequest2EvaluationMapperTest {

    public static final String ONBOARDING_OK = "ONBOARDING_OK";
    public static final String ONBOARDING_KO = "ONBOARDING_KO";

    @Test
    void testToNotify(){
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = new OnboardingRankingRequest2EvaluationMapper();

        OnboardingRankingRequests requests = OnboardingRankingRequestsFaker.mockInstance(1);

        // When
        EvaluationRankingDTO result = mapper.apply(requests);

        // Then
        commonChecks(requests, result, ONBOARDING_OK);
    }

    @Test
    void testEligibleOK(){
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = new OnboardingRankingRequest2EvaluationMapper();

        OnboardingRankingRequests request = OnboardingRankingRequestsFaker.mockInstance(1);
        request.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_OK);

        // When
        EvaluationRankingDTO result = mapper.apply(request);

        // Then
        commonChecks(request, result, ONBOARDING_OK);
    }

    @Test
    void testEligibleKO(){
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = new OnboardingRankingRequest2EvaluationMapper();

        OnboardingRankingRequests requests = OnboardingRankingRequestsFaker.mockInstance(1);
        requests.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_KO);

        // When
        EvaluationRankingDTO result = mapper.apply(requests);

        // Then
        commonChecks(requests, result, ONBOARDING_OK);
    }

    @Test
    void testOnboardingKO(){
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = new OnboardingRankingRequest2EvaluationMapper();

        OnboardingRankingRequests requests = OnboardingRankingRequestsFaker.mockInstance(1);
        requests.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ONBOARDING_KO);

        // When
        EvaluationRankingDTO result = mapper.apply(requests);

        // Then
        commonChecks(requests, result, ONBOARDING_KO);
    }

    private static void commonChecks(OnboardingRankingRequests requests, EvaluationRankingDTO result, String expectedStatus) {
        Assertions.assertNotNull(result);
        Assertions.assertEquals(requests.getInitiativeId(), result.getInitiativeId());
        Assertions.assertEquals(requests.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(requests.getAdmissibilityCheckDate(), result.getAdmissibilityCheckDate());
        Assertions.assertEquals(requests.getCriteriaConsensusTimestamp(), result.getCriteriaConsensusTimestamp());
        Assertions.assertEquals(requests.getUserId(), result.getUserId());
        Assertions.assertEquals(requests.getRankingValue2Show(), result.getRankingValue());
        Assertions.assertEquals(expectedStatus, result.getStatus());
    }

}
