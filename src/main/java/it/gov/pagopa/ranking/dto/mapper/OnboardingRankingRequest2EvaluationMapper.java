package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.event.EvaluationRankingDTO;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.springframework.stereotype.Service;

@Service
public class OnboardingRankingRequest2EvaluationMapper {

    public static final String ONBOARDING_OK = "ONBOARDING_OK";
    public static final String ONBOARDING_KO = "ONBOARDING_KO";

    public EvaluationRankingDTO apply(OnboardingRankingRequests onboardingRankingRequests) {
        EvaluationRankingDTO evaluationRankingDTO = new EvaluationRankingDTO();
        evaluationRankingDTO.setUserId(onboardingRankingRequests.getUserId());
        evaluationRankingDTO.setInitiativeId(onboardingRankingRequests.getInitiativeId());
        evaluationRankingDTO.setStatus(transcodeRankingStatus(onboardingRankingRequests));
        evaluationRankingDTO.setAdmissibilityCheckDate(onboardingRankingRequests.getAdmissibilityCheckDate());
        evaluationRankingDTO.setCriteriaConsensusTimestamp(onboardingRankingRequests.getCriteriaConsensusTimestamp());
        evaluationRankingDTO.setOrganizationId(onboardingRankingRequests.getOrganizationId());
        evaluationRankingDTO.setRankingValue(onboardingRankingRequests.getRankingValue2Show());

        return evaluationRankingDTO;
    }

    private static String transcodeRankingStatus(OnboardingRankingRequests onboardingRankingRequests) {
        return switch (onboardingRankingRequests.getBeneficiaryRankingStatus()) {
            case TO_NOTIFY, ONBOARDING_KO, ELIGIBLE_KO -> ONBOARDING_KO;
            case ELIGIBLE_OK -> ONBOARDING_OK;
        };
    }

}
