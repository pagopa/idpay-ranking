package it.gov.pagopa.ranking.service.initiative.ranking;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.service.initiative.InitiativeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class OnboardingRankingBuildFileMediatorServiceImpl implements OnboardingRankingBuildFileMediatorService{
    private final InitiativeConfigService initiativeConfigService;
    private final long beforeDays;

    public OnboardingRankingBuildFileMediatorServiceImpl(InitiativeConfigService initiativeConfigService,
                                                         @Value("${app.ranking-build-file.retrieve-initiative.day-before}") long beforeDays) {
        this.initiativeConfigService = initiativeConfigService;
        this.beforeDays = beforeDays;
    }

    @Scheduled(cron = "${app.ranking-build-file.retrieve-initiative.schedule}")
    void schedule(){
        log.debug("[RANKING_BUILD_ONBOARDING_RANKING_FILE][SCHEDULE] Starting schedule to retrieve initiative");
        this.execute();
    }

    @Override
    public void execute() {
        List<InitiativeConfig> initiativeEndOnboardingDate = initiativeConfigService.findByRankingEndDateBeforeAndRankingStatus(LocalDate.now().minusDays(beforeDays), RankingStatus.WAITING_END)
                .stream().filter(this::searchAllMessagesRead).toList();
    }

    private boolean searchAllMessagesRead(InitiativeConfig initiativeConfig){
        return true;
    }
}
