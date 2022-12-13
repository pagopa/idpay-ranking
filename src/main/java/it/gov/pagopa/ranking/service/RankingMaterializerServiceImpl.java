package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.csv.RankingCsvDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequests2RankingCsvDTOMapper;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import it.gov.pagopa.ranking.service.csv.RankingCsvWriterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RankingMaterializerServiceImpl implements RankingMaterializerService {

    /** Max size of the list that will be passed to the repository saveAll method */
    private final int savableEntitiesMaxSize;

    private final OnboardingRankingRequestsRepository onboardingRankingRequestsRepository;
    private final int size;
    private final String tmpDir;
    private final OnboardingRankingRequests2RankingCsvDTOMapper csvMapper;
    private final RankingCsvWriterService csvWriterService;

    public RankingMaterializerServiceImpl(
            @Value("${app.ranking.savable-entities-size}") int savableEntitiesMaxSize,
            OnboardingRankingRequestsRepository onboardingRankingRequestsRepository,
            @Value("${app.ranking.query-page-size}")  int size,
            @Value("${app.ranking.csv.tmp-dir}") String tmpDir,
            OnboardingRankingRequests2RankingCsvDTOMapper csvMapper,
            RankingCsvWriterService csvWriterService) {
        this.savableEntitiesMaxSize = savableEntitiesMaxSize;
        this.onboardingRankingRequestsRepository = onboardingRankingRequestsRepository;
        this.size = size;
        this.tmpDir = tmpDir;
        this.csvMapper = csvMapper;
        this.csvWriterService = csvWriterService;
    }

    @Override
    public Path materialize(InitiativeConfig initiativeConfig) {
        initiativeConfig.setRankingFilePath(buildRankingFilePath(initiativeConfig).replace(tmpDir+"/",""));

        String localFileName = "%s/%s".formatted(tmpDir, initiativeConfig.getRankingFilePath());
        createDirectoryIfNotExists(localFileName);

        try (FileWriter outputCsvWriter = new FileWriter(localFileName)) {
            String initiativeId = initiativeConfig.getInitiativeId();
            initiativeConfig.setTotalEligibleOk(0);
            initiativeConfig.setTotalEligibleKo(0);
            initiativeConfig.setTotalOnboardingKo(0);

            int page = 0;
            int rank = 1;
            List<OnboardingRankingRequests> pageContent;
            Sort sorting = getSorting(initiativeConfig);
            while (!(pageContent = onboardingRankingRequestsRepository.findAllByInitiativeId(
                    initiativeId,
                    PageRequest.of(page++, size, sorting))
            ).isEmpty()) {
                log.info("[RANKING_MATERIALIZER] Reading page number {} of initiative with id {}", page, initiativeId);

                List<OnboardingRankingRequests> requestsToWrite = new ArrayList<>();
                List<OnboardingRankingRequests> requestsToSave = new ArrayList<>();
                for (OnboardingRankingRequests r : pageContent) {
                    requestsToWrite.add(r);

                    int actualRank = rank++;

                    // updating the entity only if changed
                    updateRankingAndStatus(initiativeConfig, requestsToSave, r, actualRank);
                    updateInitiativeCounters(initiativeConfig, r);

                    if (requestsToSave.size() == savableEntitiesMaxSize) {
                        saveRequestsAndClearList(requestsToSave);
                        csvWriterService.write(buildCsvLines(requestsToWrite), outputCsvWriter, page == 1);
                    }
                }

                if (!requestsToSave.isEmpty())
                    saveRequestsAndClearList(requestsToSave);

                if (!requestsToWrite.isEmpty())
                    csvWriterService.write(buildCsvLines(requestsToWrite), outputCsvWriter, page == 1);
            }
        } catch (IOException e) {
            throw new IllegalStateException("[RANKING_MATERIALIZER] Failed to create FileWriter", e);
        }

        return Path.of(localFileName);
    }


    private void updateInitiativeCounters(InitiativeConfig initiativeConfig, OnboardingRankingRequests r) {
        if (!r.getBeneficiaryRankingStatus().equals(BeneficiaryRankingStatus.ONBOARDING_KO)) {
            if (r.getRank() <= initiativeConfig.getSize()) {
                initiativeConfig.setTotalEligibleOk(initiativeConfig.getTotalEligibleOk()+1);
            } else {
                initiativeConfig.setTotalEligibleKo(initiativeConfig.getTotalEligibleKo()+1);
            }
        } else {
            initiativeConfig.setTotalOnboardingKo(initiativeConfig.getTotalOnboardingKo()+1);
        }
    }

    private String buildRankingFilePath(InitiativeConfig initiativeConfig) {
        return "%s/%s/%s-ranking.csv".formatted(
                initiativeConfig.getOrganizationId(),
                initiativeConfig.getInitiativeId(),
                escapeRuleName(initiativeConfig.getInitiativeName()));
    }

    private void updateRankingAndStatus(InitiativeConfig initiativeConfig, List<OnboardingRankingRequests> requestsToSave, OnboardingRankingRequests r, int actualRank) {
        if (r.getRank() != actualRank) {
            r.setRank(actualRank);
            requestsToSave.add(r);

            if (!r.getBeneficiaryRankingStatus().equals(BeneficiaryRankingStatus.ONBOARDING_KO)) {
                if (r.getRank() <= initiativeConfig.getSize()) {
                    r.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_OK);
                } else {
                    r.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_KO);
                }
            }
        }
    }

    private static void createDirectoryIfNotExists(String localFileName) {
        Path directory = Paths.get(localFileName).getParent();
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new IllegalStateException("[REWARD_NOTIFICATION_EXPORT_CSV] Cannot create directory to store csv %s".formatted(localFileName), e);
            }
        }
    }

    private String escapeRuleName(String initiativeName) {
        return StringUtils.left(initiativeName.replaceAll("\\W", ""), 10);
    }

    private Sort getSorting(InitiativeConfig initiativeConfig) {
        if (!initiativeConfig.getRankingFields().isEmpty()) {
            Sort.Direction direction;

            if (initiativeConfig.getRankingFields().get(0).getFieldCode() != null) {
                direction = initiativeConfig.getRankingFields().get(0).getDirection();
                List<Sort.Order> orders = List.of(
                        new Sort.Order(direction, OnboardingRankingRequests.Fields.rankingValue),
                        new Sort.Order(Sort.Direction.ASC, OnboardingRankingRequests.Fields.criteriaConsensusTimestamp)
                );
                return Sort.by(orders);
            } else {
                throw new IllegalStateException("[RANKING] Cannot find field code in ranking fields of initiative %s".formatted(initiativeConfig.getInitiativeId()));
            }
        } else {
            throw new IllegalStateException("[RANKING] Ranking fields of initiative %s are not configured".formatted(initiativeConfig.getInitiativeId()));
        }
    }

    private void saveRequestsAndClearList(List<OnboardingRankingRequests> requestsToSave) {
        onboardingRankingRequestsRepository.saveAll(requestsToSave);
        requestsToSave.clear();
    }

    private List<RankingCsvDTO> buildCsvLines(List<OnboardingRankingRequests> requests) {
        return requests.stream().map(csvMapper).toList();
    }
}
