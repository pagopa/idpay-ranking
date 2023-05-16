package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.event.EvaluationRankingDTO;
import it.gov.pagopa.ranking.dto.event.OnboardingRejectionReason;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static it.gov.pagopa.ranking.constants.OnboardingConstants.REJECTION_REASON_CITIZEN_OUT_OF_RANKING;

@Service
public class OnboardingRankingRequest2EvaluationMapper {

    public static final String ONBOARDING_OK = "ONBOARDING_OK";
    public static final String ONBOARDING_KO = "ONBOARDING_KO";

    public EvaluationRankingDTO apply(OnboardingRankingRequests onboardingRankingRequests, InitiativeConfig initiative) {
        EvaluationRankingDTO evaluationRankingDTO = new EvaluationRankingDTO();
        evaluationRankingDTO.setUserId(onboardingRankingRequests.getUserId());
        evaluationRankingDTO.setInitiativeId(onboardingRankingRequests.getInitiativeId());
        evaluationRankingDTO.setInitiativeName(initiative.getInitiativeName());
        evaluationRankingDTO.setInitiativeEndDate(initiative.getInitiativeEndDate());
        evaluationRankingDTO.setInitiativeRewardType(initiative.getInitiativeRewardType());
        evaluationRankingDTO.setStatus(transcodeRankingStatus(onboardingRankingRequests));
        evaluationRankingDTO.setAdmissibilityCheckDate(onboardingRankingRequests.getAdmissibilityCheckDate());
        evaluationRankingDTO.setCriteriaConsensusTimestamp(onboardingRankingRequests.getCriteriaConsensusTimestamp());
        evaluationRankingDTO.setOrganizationId(onboardingRankingRequests.getOrganizationId());
        evaluationRankingDTO.setOrganizationName(initiative.getOrganizationName());
        evaluationRankingDTO.setBeneficiaryBudget(initiative.getBeneficiaryInitiativeBudget());
        evaluationRankingDTO.setOnboardingRejectionReasons(this.buildRejectionReasons(onboardingRankingRequests.getBeneficiaryRankingStatus(), onboardingRankingRequests.getRank()));
        evaluationRankingDTO.setIsLogoPresent(initiative.getIsLogoPresent());
        return evaluationRankingDTO;
    }

    private List<OnboardingRejectionReason> buildRejectionReasons(BeneficiaryRankingStatus beneficiaryRankingStatus, long rank) {
        if (beneficiaryRankingStatus.equals(BeneficiaryRankingStatus.ELIGIBLE_KO)) {
            List<OnboardingRejectionReason> rejectionReasons = new ArrayList<>();
            //Simple builder with no specific Reason
            rejectionReasons.add(OnboardingRejectionReason.builder()
                    .type(OnboardingRejectionReason.OnboardingRejectionReasonType.OUT_OF_RANKING)
                    .code(REJECTION_REASON_CITIZEN_OUT_OF_RANKING)
                    .detail(String.valueOf(rank))
                    .build());
            return rejectionReasons;
        }
        else {
            return null;
        }
    }

    private static String transcodeRankingStatus(OnboardingRankingRequests onboardingRankingRequests) {
        return switch (onboardingRankingRequests.getBeneficiaryRankingStatus()) {
            case TO_NOTIFY, ONBOARDING_KO, ELIGIBLE_KO -> ONBOARDING_KO;
            case ELIGIBLE_OK -> ONBOARDING_OK;
        };
    }

}
