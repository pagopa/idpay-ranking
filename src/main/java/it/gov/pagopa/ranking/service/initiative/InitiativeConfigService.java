package it.gov.pagopa.ranking.service.initiative;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.RankingStatus;

import java.time.LocalDate;
import java.util.List;

public interface InitiativeConfigService {
    InitiativeConfig save(InitiativeConfig initiativeConfig);
    InitiativeConfig findById(String initiativeId);
    List<InitiativeConfig> findByRankingEndDateBeforeAndRankingStatus(LocalDate date, RankingStatus rankingStatus);
}
