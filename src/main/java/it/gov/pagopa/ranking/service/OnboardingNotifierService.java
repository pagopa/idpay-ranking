package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.model.OnboardingRankingRequests;

public interface OnboardingNotifierService {

    void callOnboardingNotifier(OnboardingRankingRequests onboardingRankingRequests);

}
