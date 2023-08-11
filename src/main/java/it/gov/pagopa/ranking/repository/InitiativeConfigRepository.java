package it.gov.pagopa.ranking.repository;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.RankingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InitiativeConfigRepository extends MongoRepository<InitiativeConfig, String> {
    /**
     * Return an initiative list which rankingStatus and rankingEndDate between interval
     * @param rankingStatus - ranking status for the initiative
     * @param startIntervalExclusive starting value for the interval (not included)
     * @param endIntervalExclusive ending value for the interval (not included)
     * */
    List<InitiativeConfig> findByRankingStatusAndRankingEndDateBetween(RankingStatus rankingStatus, LocalDate startIntervalExclusive, LocalDate endIntervalExclusive);

    Optional<InitiativeConfig> deleteByInitiativeId(String initiativeId);
}
