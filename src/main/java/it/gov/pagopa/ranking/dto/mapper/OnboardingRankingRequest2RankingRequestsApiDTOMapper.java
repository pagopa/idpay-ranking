package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.springframework.stereotype.Service;

@Service
public class OnboardingRankingRequest2RankingRequestsApiDTOMapper {

    public RankingRequestsApiDTO apply(OnboardingRankingRequests onboardingRankingRequests) {
        RankingRequestsApiDTO out = new RankingRequestsApiDTO();

        out.setUserId(onboardingRankingRequests.getUserId());
        out.setInitiativeId(onboardingRankingRequests.getInitiativeId());
        out.setOrganizationId(onboardingRankingRequests.getOrganizationId());
        out.setAdmissibilityCheckDate(onboardingRankingRequests.getAdmissibilityCheckDate());
        out.setCriteriaConsensusTimestamp(onboardingRankingRequests.getCriteriaConsensusTimestamp());
        out.setRankingValue(onboardingRankingRequests.getRankingValue());
        out.setRanking(onboardingRankingRequests.getRanking());

        return out;
    }
}
