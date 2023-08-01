package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.model.OnboardingRankingRequests;

import java.util.List;

public interface OnboardingRankingRequestsService {
    OnboardingRankingRequests save(OnboardingRankingRequests onboardingRankingRequests);

    List<OnboardingRankingRequests> deleteByInitiativeId(String initiativeId);
}
