package it.gov.pagopa.ranking.controller;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.service.evaluate.OnboardingRankingBuildFileMediatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class RankingControllerImpl implements RankingController{
    private final OnboardingRankingBuildFileMediatorService onboardingRankingBuildFileMediatorService;

    public RankingControllerImpl(OnboardingRankingBuildFileMediatorService onboardingRankingBuildFileMediatorService) {
        this.onboardingRankingBuildFileMediatorService = onboardingRankingBuildFileMediatorService;
    }

    @Override
    public List<InitiativeConfig> forceRankingFileBuildScheduling() {
        log.info("Forcing onboarding ranking build file");
        return onboardingRankingBuildFileMediatorService.execute();
    }
}
