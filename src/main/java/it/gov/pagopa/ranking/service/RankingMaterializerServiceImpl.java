package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.csv.RankingCsvDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequests2RankingCsvDTOMapper;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import it.gov.pagopa.ranking.service.csv.RankingCsvWriterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
            OnboardingRankingRequests2RankingCsvDTOMapper csvMapper, RankingCsvWriterService csvWriterService) {
        this.onboardingRankingRequestsRepository = onboardingRankingRequestsRepository;
        this.size = size;
        this.csvMapper = csvMapper;
        this.csvWriterService = csvWriterService;
    }

    @Override
    public String materialize(String initiativeId) {

        Page<OnboardingRankingRequests> startingRequests = onboardingRankingRequestsRepository.findByInitiativeId(
                initiativeId,
                PageRequest.of(0, size)
        );
        String csv = csvWriterService.write(buildCsvLines(startingRequests));

        for(int i = 1; i < startingRequests.getTotalPages(); i++) {
            Page<OnboardingRankingRequests> requests = onboardingRankingRequestsRepository.findByInitiativeId(
                    initiativeId,
                    PageRequest.of(i, size)
            );
            csv = csv.concat(csvWriterService.write(buildCsvLines(requests)));
        }

        return csv;
    }

    private List<RankingCsvDTO> buildCsvLines(Page<OnboardingRankingRequests> requests) {
        return requests.getContent().stream().map(csvMapper).toList();
    }
}
