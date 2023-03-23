package it.gov.pagopa.ranking.service.initiative;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.RankingStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InitiativeConfigService {
    InitiativeConfig save(InitiativeConfig initiativeConfig);
    InitiativeConfig findById(String initiativeId);
    Optional<InitiativeConfig> findByIdOptional(String initiativeId);
    List<InitiativeConfig> findByRankingStatusRankingEndDateBetween(RankingStatus rankingStatus, LocalDate startIntervalExclusive, LocalDate endIntervalExclusive);
    void setInitiativeRankingEndDateAndStatusWaitingEnd(String initiativeId, LocalDate date);
}
