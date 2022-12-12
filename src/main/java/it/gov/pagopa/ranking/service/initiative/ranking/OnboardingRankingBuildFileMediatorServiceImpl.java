package it.gov.pagopa.ranking.service.initiative.ranking;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.service.RankingMaterializerService;
import it.gov.pagopa.ranking.service.initiative.ranking.retrieve.OnboardingRankingRuleEndedRetrieverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
@Slf4j
public class OnboardingRankingBuildFileMediatorServiceImpl implements OnboardingRankingBuildFileMediatorService{
    private final OnboardingRankingRuleEndedRetrieverService onboardingRankingRuleEndedRetrieverService;
    private final RankingMaterializerService rankingMaterializerService;

    public OnboardingRankingBuildFileMediatorServiceImpl(OnboardingRankingRuleEndedRetrieverService onboardingRankingRuleEndedRetrieverService, RankingMaterializerService rankingMaterializerService) {
        this.onboardingRankingRuleEndedRetrieverService = onboardingRankingRuleEndedRetrieverService;
        this.rankingMaterializerService = rankingMaterializerService;
    }

    @Scheduled(cron = "${app.ranking-build-file.retrieve-initiative.schedule}")
    void schedule(){
        log.debug("[RANKING_BUILD_ONBOARDING_RANKING_FILE][SCHEDULE] Starting schedule to retrieve initiative");
        this.execute();
    }

    @Override
    public List<InitiativeConfig> execute() {
        List<InitiativeConfig> initiativeEndOnboardingDate = onboardingRankingRuleEndedRetrieverService.retrieve();

        if(!initiativeEndOnboardingDate.isEmpty()){
            for (InitiativeConfig initiativeConfig : initiativeEndOnboardingDate) {
                log.info("[RANKING_BUILD_ONBOARDING_RANKING_FILE] Starting build onboarding ranking files for initiative with id: {}", initiativeConfig.getInitiativeId());
                Path localRankingFilePath = rankingMaterializerService.materialize(initiativeConfig);
                log.info("[RANKING_BUILD_ONBOARDING_RANKING_FILE] Onboarding ranking files for initiative with id: {} stored at local path: {}",
                        initiativeConfig.getInitiativeId(),
                        localRankingFilePath);

                //TODO sign and upload ranking file
            }
        }else{
            log.debug("[RANKING_BUILD_ONBOARDING_RANKING_FILE] There aren't ended initiatives");
        }

        return initiativeEndOnboardingDate;
    }

}
