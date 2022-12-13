package it.gov.pagopa.ranking.service.initiative;

import it.gov.pagopa.ranking.model.InitiativeConfig;

import java.util.Optional;

public interface InitiativeConfigService {
    InitiativeConfig save(InitiativeConfig initiativeConfig);
    InitiativeConfig findById(String initiativeId);
    Optional<InitiativeConfig> findByIdOptional(String initiativeId);
}
