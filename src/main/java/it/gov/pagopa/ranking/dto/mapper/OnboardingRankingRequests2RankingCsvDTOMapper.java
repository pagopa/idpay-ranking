package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.csv.RankingCsvDTO;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;

import java.util.function.Function;

public class OnboardingRankingRequests2RankingCsvDTOMapper implements Function<OnboardingRankingRequests, RankingCsvDTO> {

    @Override
    public RankingCsvDTO apply(OnboardingRankingRequests onboardingRankingRequests) {

        return RankingCsvDTO.builder()
                .id(onboardingRankingRequests.getId())
                .userId(onboardingRankingRequests.getUserId())
                .initiativeId(onboardingRankingRequests.getInitiativeId())
                .organizationId(onboardingRankingRequests.getOrganizationId())
                .admissibilityCheckDate(onboardingRankingRequests.getAdmissibilityCheckDate())
                .criteriaConsensusTimestamp(onboardingRankingRequests.getCriteriaConsensusTimestamp())
                .rankingValue(onboardingRankingRequests.getRankingValue())
                .rankingValueOriginal(onboardingRankingRequests.getRankingValueOriginal())
                .rank(onboardingRankingRequests.getRank())
                .beneficiaryRankingStatus(onboardingRankingRequests.getBeneficiaryRankingStatus())
                .build();
    }
}
