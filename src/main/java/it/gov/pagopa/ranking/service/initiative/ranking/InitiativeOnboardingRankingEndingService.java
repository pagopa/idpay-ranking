package it.gov.pagopa.ranking.service.initiative.ranking;

import it.gov.pagopa.ranking.model.InitiativeConfig;

import java.util.List;

public interface InitiativeOnboardingRankingEndingService {
    List<InitiativeConfig> retrieve();
}
