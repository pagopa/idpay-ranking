package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.event.EvaluationRankingDTO;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OnboardingRankingRequest2EvaluationMapperTest {

    public static final String ONBOARDING_OK = "ONBOARDING_OK";
    public static final String ONBOARDING_KO = "ONBOARDING_KO";

    @Test
    void testEligibleOK(){
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = new OnboardingRankingRequest2EvaluationMapper();

        OnboardingRankingRequests request = OnboardingRankingRequestsFaker.mockInstance(1);
        request.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_OK);
        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);

        // When
        EvaluationRankingDTO result = mapper.apply(request, initiativeConfig);

        // Then
        commonChecks(request, result, ONBOARDING_OK, initiativeConfig);
    }

    @Test
    void testEligibleKO(){
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = new OnboardingRankingRequest2EvaluationMapper();

        OnboardingRankingRequests requests = OnboardingRankingRequestsFaker.mockInstance(1);
        requests.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_KO);
        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);

        // When
        EvaluationRankingDTO result = mapper.apply(requests, initiativeConfig);

        // Then
        commonChecks(requests, result, ONBOARDING_KO, initiativeConfig);
    }

    @Test
    void testOnboardingKO(){
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = new OnboardingRankingRequest2EvaluationMapper();

        OnboardingRankingRequests requests = OnboardingRankingRequestsFaker.mockInstance(1);
        requests.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ONBOARDING_KO);
        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);

        // When
        EvaluationRankingDTO result = mapper.apply(requests, initiativeConfig);

        // Then
        commonChecks(requests, result, ONBOARDING_KO, initiativeConfig);
    }

    private static void commonChecks(OnboardingRankingRequests requests, EvaluationRankingDTO result, String expectedStatus, InitiativeConfig initiative) {
        Assertions.assertNotNull(result);
        Assertions.assertEquals(requests.getInitiativeId(), result.getInitiativeId());
        Assertions.assertEquals(initiative.getInitiativeName(), result.getInitiativeName());
        Assertions.assertEquals(initiative.getInitiativeEndDate(), result.getInitiativeEndDate());
        Assertions.assertEquals(requests.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(initiative.getOrganizationName(), result.getOrganizationName());
        Assertions.assertEquals(requests.getAdmissibilityCheckDate(), result.getAdmissibilityCheckDate());
        Assertions.assertEquals(requests.getCriteriaConsensusTimestamp(), result.getCriteriaConsensusTimestamp());
        Assertions.assertEquals(requests.getUserId(), result.getUserId());
        Assertions.assertEquals(expectedStatus, result.getStatus());
        Assertions.assertEquals(initiative.getInitiativeRewardType(), result.getInitiativeRewardType());
        Assertions.assertEquals(initiative.getIsLogoPresent(), result.getIsLogoPresent());
    }

}
