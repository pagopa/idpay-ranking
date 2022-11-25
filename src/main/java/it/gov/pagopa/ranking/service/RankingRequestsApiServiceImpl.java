package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequest2RankingRequestsApiDTOMapper;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.Order;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static it.gov.pagopa.ranking.utils.RankingConstants.CRITERIA_CODE_ISEE;

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
    public List<RankingRequestsApiDTO>  findByInitiativeId(String organizationId, String initiativeId, int page, int size) {
        LocalDate today = LocalDate.now();

        InitiativeConfig initiative = rankingContextHolderService.getInitiativeConfig(initiativeId, organizationId);
        if (initiative == null) {
            return null;
        } else {
            List<RankingRequestsApiDTO> out = new ArrayList<>();

            if (!initiative.getRankingEndDate().isBefore(today)) {

                List<OnboardingRankingRequests> requests =
                        onboardingRankingRequestsRepository.findByInitiativeId(
                                initiativeId,
                                PageRequest.of(page, size),
                                getSorting(initiative)
                        );

                int i = 0;
                for (OnboardingRankingRequests r : requests) {

                    RankingRequestsApiDTO dto = dtoMapper.apply(r);
                    dto.setRank(calculateRank(i, page, size));
                    out.add(dto);
                    i++;
                }
            }

            return out;
        }
    }

    private Sort getSorting(InitiativeConfig initiativeConfig) {
        Sort.Direction direction;

        if (initiativeConfig.getRankingFields().get(0).getFieldCode().equals(CRITERIA_CODE_ISEE)) {
            direction = initiativeConfig.getRankingFields().get(0).getDirection();
            return Sort.by(direction, "rankingValue");
        } else {
            return Sort.unsorted();
        }
    }

    private long calculateRank(int requestIndex, int page, int size) {
        return ((long) (page - 1) * size) + (requestIndex + 1);
    }
}
