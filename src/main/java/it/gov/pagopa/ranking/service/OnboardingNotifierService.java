package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;

import java.util.List;

public interface OnboardingNotifierService {

    void callOnboardingNotifier(InitiativeConfig initiative, List<OnboardingRankingRequests> onboardingRankingRequests);

}
