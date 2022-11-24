package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class OnboardingRankingRequest2RankingRequestsApiDTOMapper implements Function<OnboardingRankingRequests, RankingRequestsApiDTO> {

    @Override
    public RankingRequestsApiDTO apply(OnboardingRankingRequests onboardingRankingRequests) {
        RankingRequestsApiDTO out = new RankingRequestsApiDTO();

        out.setUserId(onboardingRankingRequests.getUserId());
        out.setInitiativeId(onboardingRankingRequests.getInitiativeId());
        out.setAdmissibilityCheckDate(onboardingRankingRequests.getAdmissibilityCheckDate());
        out.setCriteriaConsensusTimestamp(onboardingRankingRequests.getCriteriaConsensusTimestamp());
        out.setRankingValue(onboardingRankingRequests.getRankingValue());

        return out;
    }
}
