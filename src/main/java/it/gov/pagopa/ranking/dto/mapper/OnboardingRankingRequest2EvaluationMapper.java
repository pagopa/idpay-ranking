package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.event.EvaluationDTO;
import it.gov.pagopa.ranking.dto.event.EvaluationRankingDTO;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.springframework.stereotype.Service;

@Service
public class OnboardingRankingRequest2EvaluationMapper {

    public static final String ONBOARDING_OK = "ONBOARDING_OK";
    public static final String ONBOARDING_KO = "ONBOARDING_KO";

    public EvaluationDTO apply(OnboardingRankingRequests onboardingRankingRequests) {
        EvaluationRankingDTO evaluationRankingDTO = new EvaluationRankingDTO();
        evaluationRankingDTO.setUserId(onboardingRankingRequests.getUserId());
        evaluationRankingDTO.setInitiativeId(onboardingRankingRequests.getInitiativeId());
        //Map to Onboarding Status
        if (onboardingRankingRequests.getBeneficiaryRankingStatus() == BeneficiaryRankingStatus.ELIGIBLE_KO || onboardingRankingRequests.getBeneficiaryRankingStatus() == BeneficiaryRankingStatus.ONBOARDING_KO) {
            evaluationRankingDTO.setStatus(ONBOARDING_KO);
        } else if (onboardingRankingRequests.getBeneficiaryRankingStatus() == BeneficiaryRankingStatus.ELIGIBLE_OK) {
            evaluationRankingDTO.setStatus(ONBOARDING_OK);
        }
        evaluationRankingDTO.setAdmissibilityCheckDate(onboardingRankingRequests.getAdmissibilityCheckDate());
        //TODO Missing rejectionReason for ELIBIGLE_KO and ONBOARDING_KO
        evaluationRankingDTO.setCriteriaConsensusTimestamp(onboardingRankingRequests.getCriteriaConsensusTimestamp());
        evaluationRankingDTO.setOrganizationId(onboardingRankingRequests.getOrganizationId());
        evaluationRankingDTO.setRanking(onboardingRankingRequests.getRank());
        //TODO Are these values [initiativeName, initiativeEndDate] needed?

        return evaluationRankingDTO;
    }

}
