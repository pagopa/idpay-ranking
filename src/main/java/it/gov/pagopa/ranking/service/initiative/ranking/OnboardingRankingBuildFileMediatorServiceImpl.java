package it.gov.pagopa.ranking.service.initiative.ranking;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OnboardingRankingBuildFileMediatorServiceImpl implements OnboardingRankingBuildFileMediatorService{
    private final OnboardingRankingRuleEndedRetrieverService onboardingRankingRuleEndedRetrieverService;

    public OnboardingRankingBuildFileMediatorServiceImpl(OnboardingRankingRuleEndedRetrieverService onboardingRankingRuleEndedRetrieverService) {
        this.onboardingRankingRuleEndedRetrieverService = onboardingRankingRuleEndedRetrieverService;
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
            log.info("[RANKING_BUILD_ONBOARDING_RANKING_FILE] Starting build onboarding ranking files");
            //TODO generate ranking file for each element into initiativeEndOnboardingDateList
        }else{
            log.debug("[RANKING_BUILD_ONBOARDING_RANKING_FILE] There aren't ended initiatives");
        }

        return initiativeEndOnboardingDate;
    }

}
