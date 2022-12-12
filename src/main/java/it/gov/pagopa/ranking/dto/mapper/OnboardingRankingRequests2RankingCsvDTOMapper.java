package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.csv.RankingCsvDTO;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class OnboardingRankingRequests2RankingCsvDTOMapper implements Function<OnboardingRankingRequests, RankingCsvDTO> {

    @Override
    public RankingCsvDTO apply(OnboardingRankingRequests onboardingRankingRequests) {

        return RankingCsvDTO.builder()
                .userId(onboardingRankingRequests.getUserId())
                .criteriaConsensusTimestamp(onboardingRankingRequests.getCriteriaConsensusTimestamp())
                .rankingValue(onboardingRankingRequests.getRankingValue())
                .rank(onboardingRankingRequests.getRank())
                .build();
    }
}
