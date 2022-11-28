package it.gov.pagopa.ranking.service.initiative;

import it.gov.pagopa.ranking.model.InitiativeConfig;

public interface InitiativeConfigService {
    InitiativeConfig save(InitiativeConfig initiativeConfig);
    InitiativeConfig findById(String initiativeId);
}
