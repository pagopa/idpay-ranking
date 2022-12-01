package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.RankingPageDTO;
import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequest2RankingRequestsApiDTOMapper;
import it.gov.pagopa.ranking.dto.mapper.PageOnboardingRequests2RankingPageDTOMapper;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
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
    public List<RankingRequestsApiDTO> findByInitiativeId(String organizationId, String initiativeId, int page, int size, BeneficiaryRankingStatus beneficiaryRankingStatus) {

        InitiativeConfig initiative = rankingContextHolderService.getInitiativeConfig(initiativeId, organizationId);
        if (initiative == null) {
            return null;
        } else {
            if (!initiative.getRankingStatus().equals(RankingStatus.WAITING_END)) {
                if (beneficiaryRankingStatus != null) {
                    return onboardingRankingRequestsRepository.findByInitiativeIdAndBeneficiaryRankingStatus(
                                    initiativeId,
                                    beneficiaryRankingStatus,
                                    PageRequest.of(page, size, Sort.by(OnboardingRankingRequests.Fields.rank))
                            ).getContent()
                            .stream()
                            .map(dtoMapper::apply)
                            .toList();
                } else {
                    return onboardingRankingRequestsRepository.findByInitiativeId(
                                    initiativeId,
                                    PageRequest.of(page, size, Sort.by(OnboardingRankingRequests.Fields.rank))
                            ).getContent()
                            .stream()
                            .map(dtoMapper::apply)
                            .toList();
                }
            } else {
                return Collections.emptyList();
            }
        }
    }

    @Override
    public RankingPageDTO findByInitiativeIdPaged(String organizationId, String initiativeId, int page, int size, BeneficiaryRankingStatus beneficiaryRankingStatus) {

        InitiativeConfig initiative = rankingContextHolderService.getInitiativeConfig(initiativeId, organizationId);
        if (initiative == null) {
            return null;
        } else {

            Page<OnboardingRankingRequests> pageRequests = new PageImpl<>(Collections.emptyList());
            if (!initiative.getRankingStatus().equals(RankingStatus.WAITING_END)) {
                if(beneficiaryRankingStatus != null) {
                    pageRequests = onboardingRankingRequestsRepository.findByInitiativeIdAndBeneficiaryRankingStatus(
                            initiativeId,
                            beneficiaryRankingStatus,
                            PageRequest.of(page, size, Sort.by(OnboardingRankingRequests.Fields.rank))
                    );
                } else {
                    pageRequests = onboardingRankingRequestsRepository.findByInitiativeId(
                            initiativeId,
                            PageRequest.of(page, size, Sort.by(OnboardingRankingRequests.Fields.rank))
                    );
                }

                return pageDtoMapper.apply(
                        pageRequests,
                        pageRequests.getContent().stream().map(dtoMapper::apply).toList(),
                        initiative
                );
            } else {
               return pageDtoMapper.apply(
                       pageRequests,
                       Collections.emptyList(),
                       initiative
               );
            }
        }
    }
}
