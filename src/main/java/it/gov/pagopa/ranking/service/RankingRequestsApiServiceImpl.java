package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.PageRankingDTO;
import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequest2RankingRequestsApiDTOMapper;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RankingRequestsApiServiceImpl implements RankingRequestsApiService {

    private final OnboardingRankingRequestsRepository onboardingRankingRequestsRepository;
    private final OnboardingRankingRequest2RankingRequestsApiDTOMapper dtoMapper;
    private final RankingContextHolderService rankingContextHolderService;

    public RankingRequestsApiServiceImpl(OnboardingRankingRequestsRepository onboardingRankingRequestsRepository, OnboardingRankingRequest2RankingRequestsApiDTOMapper rankingRequestsApiDTOMapper, RankingContextHolderService rankingContextHolderService) {
        this.onboardingRankingRequestsRepository = onboardingRankingRequestsRepository;
        this.dtoMapper = rankingRequestsApiDTOMapper;
        this.rankingContextHolderService = rankingContextHolderService;
    }

    @Override
    public List<RankingRequestsApiDTO> findByInitiativeId(String organizationId, String initiativeId, int page, int size) {

        InitiativeConfig initiative = rankingContextHolderService.getInitiativeConfig(initiativeId, organizationId);
        if (initiative == null) {
            return null;
        } else {
            List<RankingRequestsApiDTO> out = new ArrayList<>();

            if (!initiative.getRankingStatus().equals(RankingStatus.WAITING_END)) {

                List<OnboardingRankingRequests> requests =
                        onboardingRankingRequestsRepository.findByInitiativeId(
                                initiativeId,
                                PageRequest.of(page, size, Sort.by("ranking"))
                        );

                for (OnboardingRankingRequests r : requests) {
                    out.add(dtoMapper.apply(r));
                }
            }

            return out;
        }
    }

    @Override
    public PageRankingDTO<RankingRequestsApiDTO> findByInitiativeIdPaged(String organizationId, String initiativeId, int page, int size) {

        InitiativeConfig initiative = rankingContextHolderService.getInitiativeConfig(initiativeId, organizationId);
        if (initiative == null) {
            return null;
        } else {
            List<RankingRequestsApiDTO> out = new ArrayList<>();

            if (!initiative.getRankingStatus().equals(RankingStatus.WAITING_END)) {

                List<OnboardingRankingRequests> requests =
                        onboardingRankingRequestsRepository.findByInitiativeId(
                                initiativeId,
                                PageRequest.of(page, size, Sort.by("ranking"))
                        );

                for (OnboardingRankingRequests r : requests) {
                    out.add(dtoMapper.apply(r));
                }
            }

            long total = onboardingRankingRequestsRepository.countByInitiativeId(initiativeId);

            return new PageRankingDTO<>(
                    out,
                    initiative.getRankingStatus().toString(),
                    initiative.getRankingPublishedTimeStamp(),
                    initiative.getRankingGeneratedTimeStamp(),
                    page,
                    size,
                    total
            );
        }

    }
}
