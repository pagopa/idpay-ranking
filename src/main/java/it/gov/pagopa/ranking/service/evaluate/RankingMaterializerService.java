package it.gov.pagopa.ranking.service.evaluate;

import it.gov.pagopa.ranking.model.InitiativeConfig;

import java.nio.file.Path;

public interface RankingMaterializerService {

    Path materialize(InitiativeConfig initiativeConfig);
}
