package it.gov.pagopa.ranking.service.initiative;

import it.gov.pagopa.ranking.exception.InitiativeNotFoundException;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.repository.InitiativeConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class InitiativeConfigServiceImpl implements InitiativeConfigService{
    private final InitiativeConfigRepository initiativeConfigRepository;

    public InitiativeConfigServiceImpl(InitiativeConfigRepository initiativeConfigRepository) {
        this.initiativeConfigRepository = initiativeConfigRepository;
    }

    @Override
    public InitiativeConfig save(InitiativeConfig initiativeConfig) {
        return initiativeConfigRepository.save(initiativeConfig);
    }

    @Override
    public InitiativeConfig findById(String initiativeId) {
        return initiativeConfigRepository.findById(initiativeId).orElse(null);
    }

    @Override
    public Optional<InitiativeConfig> findByIdOptional(String initiativeId) {
        return initiativeConfigRepository.findById(initiativeId);
    }

    @Override
    public List<InitiativeConfig> findByRankingStatusRankingEndDateBetween(RankingStatus rankingStatus, LocalDate startIntervalExclusive, LocalDate endIntervalExclusive) {
        return initiativeConfigRepository.findByRankingStatusAndRankingEndDateBetween(rankingStatus, startIntervalExclusive, endIntervalExclusive);
    }

    @Override
    public void setInitiativeRankingEndDateAndStatusWaitingEnd(String initiativeId, LocalDate date) {
        InitiativeConfig initiative = this.findById(initiativeId);

        if (initiative != null) {
            initiative.setRankingEndDate(date);
            initiative.setRankingStatus(RankingStatus.WAITING_END);

            initiativeConfigRepository.save(initiative);
        } else {
            throw new InitiativeNotFoundException("[RANKING][FORCE_RANKING_END] Could not find initiative having id %s".formatted(initiativeId));
        }
    }

    @Override
    public Optional<InitiativeConfig> deleteByInitiativeId(String initiativeId){
        return initiativeConfigRepository.deleteByInitiativeId(initiativeId);
    }
}
