package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequest2RankingRequestsApiDTOMapper;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import it.gov.pagopa.ranking.utils.RankingConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

            if (!initiative.getRankingStatus().equals(RankingConstants.INITIATIVE_RANKING_STATUS_WAITING_END)) {

                List<OnboardingRankingRequests> requests =
                        onboardingRankingRequestsRepository.findByInitiativeId(
                                initiativeId,
                                PageRequest.of(page, size, Sort.by("rank"))
                        );

                for (OnboardingRankingRequests r : requests) {
                    out.add(dtoMapper.apply(r, initiative.getOrganizationId()));
                }
            }

            return out;
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
}
