package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.event.EvaluationDTO;
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
    void testSingleFieldsWithNoStatus(){
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = new OnboardingRankingRequest2EvaluationMapper();

        OnboardingRankingRequests requests = OnboardingRankingRequestsFaker.mockInstance(1);

        // When
        EvaluationDTO result = mapper.apply(requests);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(requests.getInitiativeId(), result.getInitiativeId());
        Assertions.assertEquals(requests.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(requests.getAdmissibilityCheckDate(), result.getAdmissibilityCheckDate());
        Assertions.assertEquals(requests.getCriteriaConsensusTimestamp(), result.getCriteriaConsensusTimestamp());
        Assertions.assertEquals(requests.getUserId(), result.getUserId());
    }

    @Test
    void testEligibleOK(){
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = new OnboardingRankingRequest2EvaluationMapper();

        OnboardingRankingRequests requests = OnboardingRankingRequestsFaker.mockInstance(1);
        requests.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_OK);

        // When
        EvaluationDTO result = mapper.apply(requests);

        // Then
        Assertions.assertNotNull(result);
        if(result instanceof EvaluationRankingDTO evaluationRankingDTO){
            Assertions.assertEquals(requests.getRank(), evaluationRankingDTO.getRanking());
            Assertions.assertEquals(ONBOARDING_OK, evaluationRankingDTO.getStatus());
        }
    }

    @Test
    void testEligibleKO(){
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = new OnboardingRankingRequest2EvaluationMapper();

        OnboardingRankingRequests requests = OnboardingRankingRequestsFaker.mockInstance(1);
        requests.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_KO);

        // When
        EvaluationDTO result = mapper.apply(requests);

        // Then
        Assertions.assertNotNull(result);
        if(result instanceof EvaluationRankingDTO evaluationRankingDTO){
            Assertions.assertEquals(requests.getRank(), evaluationRankingDTO.getRanking());
            Assertions.assertEquals(ONBOARDING_KO, evaluationRankingDTO.getStatus());
        }
    }

    @Test
    void testOnboardingKO(){
        // Given
        OnboardingRankingRequest2EvaluationMapper mapper = new OnboardingRankingRequest2EvaluationMapper();

        OnboardingRankingRequests requests = OnboardingRankingRequestsFaker.mockInstance(1);
        requests.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ONBOARDING_KO);

        // When
        EvaluationDTO result = mapper.apply(requests);

        // Then
        Assertions.assertNotNull(result);
        if(result instanceof EvaluationRankingDTO evaluationRankingDTO){
            Assertions.assertEquals(requests.getRank(), evaluationRankingDTO.getRanking());
            Assertions.assertEquals(ONBOARDING_KO, evaluationRankingDTO.getStatus());
        }
    }

}
