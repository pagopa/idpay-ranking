package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.OnboardingRankingRequestDTO;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class OnboardingRankingRequestsDTO2ModelMapper implements Function<OnboardingRankingRequestDTO, OnboardingRankingRequests> {
    @Override
    public OnboardingRankingRequests apply(OnboardingRankingRequestDTO onboardingRankingRequestDTO) {
        OnboardingRankingRequests out = new OnboardingRankingRequests();
        out.setId(buildId(onboardingRankingRequestDTO));
        out.setUserId(onboardingRankingRequestDTO.getUserId());
        out.setInitiativeId(onboardingRankingRequestDTO.getInitiativeId());
        out.setAdmissibilityCheckDate(onboardingRankingRequestDTO.getAdmissibilityCheckDate());
        out.setCriteriaConsensusTimestamp(onboardingRankingRequestDTO.getCriteriaConsensusTimestamp());
        out.setRankingValue(onboardingRankingRequestDTO.getRankingValue());
        out.setRankingValueOriginal(onboardingRankingRequestDTO.getRankingValue());

        return out;
    }

    public static String buildId(OnboardingRankingRequestDTO onboardingRankingRequestDTO) {
        return onboardingRankingRequestDTO.getUserId()
                .concat(onboardingRankingRequestDTO.getInitiativeId());
    }
}
