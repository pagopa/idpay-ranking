package it.gov.pagopa.ranking.service.initiative.ranking;

import it.gov.pagopa.ranking.connector.azure.storage.InitiativeRankingBlobClient;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.repository.InitiativeConfigRepository;
import it.gov.pagopa.ranking.service.RankingMaterializerService;
import it.gov.pagopa.ranking.service.initiative.ranking.retrieve.OnboardingRankingRuleEndedRetrieverService;
import it.gov.pagopa.ranking.service.sign.P7mSignerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@Slf4j
public class OnboardingRankingBuildFileMediatorServiceImpl implements OnboardingRankingBuildFileMediatorService{
    private final OnboardingRankingRuleEndedRetrieverService onboardingRankingRuleEndedRetrieverService;
    private final RankingMaterializerService rankingMaterializerService;
    private final P7mSignerService p7mSignerService;
    private final InitiativeRankingBlobClient rankingBlobClient;
    private final InitiativeConfigRepository initiativeConfigRepository;

    public OnboardingRankingBuildFileMediatorServiceImpl(OnboardingRankingRuleEndedRetrieverService onboardingRankingRuleEndedRetrieverService, RankingMaterializerService rankingMaterializerService, P7mSignerService p7mSignerService, InitiativeRankingBlobClient rankingBlobClient, InitiativeConfigRepository initiativeConfigRepository) {
        this.onboardingRankingRuleEndedRetrieverService = onboardingRankingRuleEndedRetrieverService;
        this.rankingMaterializerService = rankingMaterializerService;
        this.p7mSignerService = p7mSignerService;
        this.rankingBlobClient = rankingBlobClient;
        this.initiativeConfigRepository = initiativeConfigRepository;
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

                Path signedFilePath = sign(localRankingFilePath, initiativeConfig);

                uploadFileAndSaveUpdatedInitiative(signedFilePath, initiativeConfig);
            }
        }else{
            log.debug("[RANKING_BUILD_ONBOARDING_RANKING_FILE] There aren't ended initiatives");
        }

        return initiativeEndOnboardingDate;
    }

    private Path sign(Path localRankingFilePath, InitiativeConfig initiativeConfig) {
        Path signedFilePath = p7mSignerService.sign(Path.of(initiativeConfig.getRankingFilePath()));
        initiativeConfig.setRankingFilePath(signedFilePath.toString());

        // TODO delete csv

        return signedFilePath;
    }

    private void uploadFileAndSaveUpdatedInitiative(Path signedFilePath, InitiativeConfig initiativeConfig) {

        rankingBlobClient.uploadFile(
                signedFilePath,
                initiativeConfig.getRankingFilePath(),
                "application/pkcs7-mime");

        // TODO delete local p7m

        initiativeConfig.setRankingStatus(RankingStatus.READY);
        initiativeConfigRepository.save(initiativeConfig);

    }



}
