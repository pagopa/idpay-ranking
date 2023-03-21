package it.gov.pagopa.ranking.controller;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.service.evaluate.OnboardingRankingBuildFileMediatorService;
import it.gov.pagopa.ranking.service.initiative.InitiativeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
public class RankingControllerImpl implements RankingController{
    private final OnboardingRankingBuildFileMediatorService onboardingRankingBuildFileMediatorService;
    private final InitiativeConfigService initiativeConfigService;

    public RankingControllerImpl(OnboardingRankingBuildFileMediatorService onboardingRankingBuildFileMediatorService, InitiativeConfigService initiativeConfigService) {
        this.onboardingRankingBuildFileMediatorService = onboardingRankingBuildFileMediatorService;
        this.initiativeConfigService = initiativeConfigService;
    }

    @Override
    public void updateRankingInitiativeEnd(String initiativeId, LocalDate rankingEndDate) {
        initiativeConfigService.setInitiativeRankingEndDateAndStatusWaitingEnd(initiativeId, rankingEndDate);
    }

    @Override
    public List<InitiativeConfig> forceRankingFileBuildScheduling() {
        log.info("Forcing onboarding ranking build file");
        return onboardingRankingBuildFileMediatorService.execute();
    }
}
