package it.gov.pagopa.ranking.controller;

import it.gov.pagopa.ranking.service.initiative.ranking.OnboardingRankingBuildFileMediatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class RankingControllerImpl implements RankingController{
    private final OnboardingRankingBuildFileMediatorService onboardingRankingBuildFileMediatorService;

    public RankingControllerImpl(OnboardingRankingBuildFileMediatorService onboardingRankingBuildFileMediatorService) {
        this.onboardingRankingBuildFileMediatorService = onboardingRankingBuildFileMediatorService;
    }

    @Override
    public ResponseEntity<String> forceRankingFileBuildScheduling() {
        log.info("Forcing onboarding ranking build file");
        onboardingRankingBuildFileMediatorService.execute();

        return ResponseEntity.ok("Something tu return");
    }
}
