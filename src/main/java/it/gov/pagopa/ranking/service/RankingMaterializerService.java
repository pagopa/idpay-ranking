package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.model.InitiativeConfig;

public interface RankingMaterializerService {

    String materialize(InitiativeConfig initiativeConfig);
}
