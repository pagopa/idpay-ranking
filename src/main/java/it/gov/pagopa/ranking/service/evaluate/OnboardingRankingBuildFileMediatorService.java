package it.gov.pagopa.ranking.service.evaluate;

import it.gov.pagopa.ranking.model.InitiativeConfig;

import java.util.List;

public interface OnboardingRankingBuildFileMediatorService {
    List<InitiativeConfig> execute();

     void forceRankingInitiativeEnd(String initiativeId);
}
