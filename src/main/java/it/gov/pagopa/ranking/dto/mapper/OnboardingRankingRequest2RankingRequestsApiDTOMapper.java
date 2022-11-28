package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.springframework.stereotype.Service;

@Service
public class OnboardingRankingRequest2RankingRequestsApiDTOMapper {

    public RankingRequestsApiDTO apply(OnboardingRankingRequests onboardingRankingRequests, String organizationId) {
        RankingRequestsApiDTO out = new RankingRequestsApiDTO();

        out.setUserId(onboardingRankingRequests.getUserId());
        out.setInitiativeId(onboardingRankingRequests.getInitiativeId());
        out.setOrganizationId(organizationId);
        out.setAdmissibilityCheckDate(onboardingRankingRequests.getAdmissibilityCheckDate());
        out.setCriteriaConsensusTimestamp(onboardingRankingRequests.getCriteriaConsensusTimestamp());
        out.setRankingValue(onboardingRankingRequests.getRankingValue());
        out.setRank(onboardingRankingRequests.getRank());

        return out;
    }
}
