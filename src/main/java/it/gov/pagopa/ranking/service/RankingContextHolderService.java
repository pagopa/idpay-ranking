package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.model.InitiativeConfig;

public interface RankingContextHolderService {
    InitiativeConfig getInitiativeConfig(String initiativeId);
    InitiativeConfig getInitiativeConfig(String initiativeId, String organizationId);

    void setInitiativeConfig(InitiativeConfig initiativeConfig);
}
