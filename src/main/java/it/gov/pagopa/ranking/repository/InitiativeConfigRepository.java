package it.gov.pagopa.ranking.repository;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.RankingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface InitiativeConfigRepository extends MongoRepository<InitiativeConfig, String> {
    List<InitiativeConfig> findByRankingStatusAndRankingEndDateBetween(RankingStatus rankingStatus, LocalDate startIntervalExclusive, LocalDate endIntervalExclusive);
}
