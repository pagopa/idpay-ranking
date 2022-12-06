package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.csv.RankingCsvDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequests2RankingCsvDTOMapper;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.repository.InitiativeConfigRepository;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import it.gov.pagopa.ranking.service.csv.RankingCsvWriterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class RankingMaterializerServiceImpl implements RankingMaterializerService {

    private final OnboardingRankingRequestsRepository onboardingRankingRequestsRepository;
    private final int size;
    private final OnboardingRankingRequests2RankingCsvDTOMapper csvMapper;
    private final RankingCsvWriterService csvWriterService;

    public RankingMaterializerServiceImpl(
            OnboardingRankingRequestsRepository onboardingRankingRequestsRepository,
            @Value("${app.ranking.query-page-size}")  int size,
            OnboardingRankingRequests2RankingCsvDTOMapper csvMapper,
            RankingCsvWriterService csvWriterService
    ) {
        this.onboardingRankingRequestsRepository = onboardingRankingRequestsRepository;
        this.size = size;
        this.csvMapper = csvMapper;
        this.csvWriterService = csvWriterService;
    }

    @Override
    public String materialize(InitiativeConfig initiativeConfig) {

        try (FileWriter outputCsvWriter = new FileWriter("tmp")) { // TODO define fileName
            String initiativeId = initiativeConfig.getInitiativeId();
            long totalEligibleOk = initiativeConfig.getTotalEligibleOk();
            long totalEligibleKo = initiativeConfig.getTotalEligibleKo();

            int page = 0;
            int rank = 1;
            List<OnboardingRankingRequests> pageContent;
            while (!(pageContent = onboardingRankingRequestsRepository.findAllByInitiativeId(
                    initiativeId,
                    PageRequest.of(page++, size, getSorting(initiativeConfig))
            )).isEmpty()) {

                log.info("[RANKING_MATERIALIZER] Reading page number {} of initiative with id {}", page, initiativeId);
                for (OnboardingRankingRequests r : pageContent) {
                    int actualRank = rank++;

                    // updating the entity only if changed
                    if (r.getRank() != actualRank) {
                        r.setRank(actualRank);

                        if (r.getBeneficiaryRankingStatus() != BeneficiaryRankingStatus.ONBOARDING_KO) {
                            if (r.getRank() <= initiativeConfig.getSize()) {
                                r.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_OK);
                                ++totalEligibleOk;
                            } else {
                                r.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_KO);
                                ++totalEligibleKo;
                            }
                        }
                    }
                }
                onboardingRankingRequestsRepository.saveAll(pageContent);

                csvWriterService.write(buildCsvLines(pageContent), outputCsvWriter); // TODO this method should write directly into the file
            }

            // updating totalEligibleOk and totalEligibleKo fields of InitiativeConfig
            updateInitiativeStatusCounters(initiativeConfig, totalEligibleOk, totalEligibleKo);

            return null;

        } catch (IOException e) {
            throw new IllegalStateException("[RANKING_MATERIALIZER] Failed to create FileWriter", e);
        }

    }

    private Sort getSorting(InitiativeConfig initiativeConfig) {

        if (!initiativeConfig.getRankingFields().isEmpty()) {
            Sort.Direction direction;

            if (initiativeConfig.getRankingFields().get(0).getFieldCode() != null) {
                direction = initiativeConfig.getRankingFields().get(0).getDirection();
                return Sort.by(direction, "rankingValue");
            } else {
                throw new IllegalStateException("[RANKING] Cannot find field code in ranking fields of initiative %s".formatted(initiativeConfig.getInitiativeId()));
            }
        } else {
            throw new IllegalStateException("[RANKING] Ranking fields of initiative %s are not configured".formatted(initiativeConfig.getInitiativeId()));
        }
    }

    private void updateInitiativeStatusCounters(InitiativeConfig initiativeConfig, long totalEligibleOk, long totalEligibleKo) {
        initiativeConfig.setTotalEligibleOk(totalEligibleOk);
        initiativeConfig.setTotalEligibleKo(totalEligibleKo);
    }

    private List<RankingCsvDTO> buildCsvLines(List<OnboardingRankingRequests> requests) {
        return requests.stream().map(csvMapper).toList();
    }
}
