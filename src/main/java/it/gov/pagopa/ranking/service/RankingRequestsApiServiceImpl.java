package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.RankingPageDTO;
import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequest2RankingRequestsApiDTOMapper;
import it.gov.pagopa.ranking.dto.mapper.PageOnboardingRequests2RankingPageDTOMapper;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class RankingRequestsApiServiceImpl implements RankingRequestsApiService {

    private final OnboardingRankingRequestsRepository onboardingRankingRequestsRepository;
    private final OnboardingRankingRequest2RankingRequestsApiDTOMapper dtoMapper;
    private final PageOnboardingRequests2RankingPageDTOMapper pageDtoMapper;
    private final RankingContextHolderService rankingContextHolderService;

    public RankingRequestsApiServiceImpl(OnboardingRankingRequestsRepository onboardingRankingRequestsRepository, OnboardingRankingRequest2RankingRequestsApiDTOMapper rankingRequestsApiDTOMapper, PageOnboardingRequests2RankingPageDTOMapper pageDtoMapper, RankingContextHolderService rankingContextHolderService) {
        this.onboardingRankingRequestsRepository = onboardingRankingRequestsRepository;
        this.dtoMapper = rankingRequestsApiDTOMapper;
        this.pageDtoMapper = pageDtoMapper;
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
                                PageRequest.of(page, size, Sort.by(OnboardingRankingRequests.Fields.rank))
                        );

                for (OnboardingRankingRequests r : requests) {
                    out.add(dtoMapper.apply(r));
                }
            }

            return out;
        }
    }

    @Override
    public RankingPageDTO findByInitiativeIdPaged(String organizationId, String initiativeId, int page, int size) {

        InitiativeConfig initiative = rankingContextHolderService.getInitiativeConfig(initiativeId, organizationId);
        if (initiative == null) {
            return null;
        } else {
            List<RankingRequestsApiDTO> rankingDtoList = new ArrayList<>();
            Page<List<OnboardingRankingRequests>> pageRequests = new PageImpl<>(Collections.emptyList());

            if (!initiative.getRankingStatus().equals(RankingStatus.WAITING_END)) {

                pageRequests = onboardingRankingRequestsRepository.findByInitiativeIdPaged(
                                initiativeId,
                                PageRequest.of(page, size, Sort.by(OnboardingRankingRequests.Fields.rank))
                        );

                for (OnboardingRankingRequests r : pageRequests.getContent().get(0)) {
                    rankingDtoList.add(dtoMapper.apply(r));
                }
            }

            return pageDtoMapper.apply(
                    pageRequests,
                    rankingDtoList,
                    initiative.getRankingStatus(),
                    initiative.getRankingPublishedTimeStamp(),
                    initiative.getRankingGeneratedTimeStamp());
        }
    }
}
