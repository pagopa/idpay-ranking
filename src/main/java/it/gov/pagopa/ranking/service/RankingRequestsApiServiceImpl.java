package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.controller.RankingPageDTO;
import it.gov.pagopa.ranking.dto.controller.RankingRequestFilter;
import it.gov.pagopa.ranking.dto.controller.RankingRequestsApiDTO;
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
    private final OnboardingNotifierService onboardingNotifierService;

    public RankingRequestsApiServiceImpl(
            OnboardingRankingRequestsRepository onboardingRankingRequestsRepository,
            OnboardingRankingRequest2RankingRequestsApiDTOMapper rankingRequestsApiDTOMapper,
            PageOnboardingRequests2RankingPageDTOMapper pageDtoMapper,
            RankingContextHolderService rankingContextHolderService,
            OnboardingNotifierService onboardingNotifierService
    ) {
        this.onboardingRankingRequestsRepository = onboardingRankingRequestsRepository;
        this.dtoMapper = rankingRequestsApiDTOMapper;
        this.pageDtoMapper = pageDtoMapper;
        this.rankingContextHolderService = rankingContextHolderService;
        this.onboardingNotifierService = onboardingNotifierService;
    }

    @Override
    public List<RankingRequestsApiDTO> findByInitiativeId(String organizationId, String initiativeId, int page, int size, RankingRequestFilter filter) {

        InitiativeConfig initiative = rankingContextHolderService.getInitiativeConfig(initiativeId, organizationId);
        if (!initiative.getRankingStatus().equals(RankingStatus.WAITING_END)) {
            return onboardingRankingRequestsRepository.findAllBy(
                            initiativeId,
                            filter,
                            PageRequest.of(page, size, Sort.by(OnboardingRankingRequests.Fields.initiativeId, OnboardingRankingRequests.Fields.rank))
                    ).getContent()
                    .stream()
                    .map(dtoMapper::apply)
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public RankingPageDTO findByInitiativeIdPaged(String organizationId, String initiativeId, int page, int size, RankingRequestFilter filter) {

        InitiativeConfig initiative = rankingContextHolderService.getInitiativeConfig(initiativeId, organizationId);

        Page<OnboardingRankingRequests> pageRequests = new PageImpl<>(Collections.emptyList());
        if (!initiative.getRankingStatus().equals(RankingStatus.WAITING_END)) {
            pageRequests = onboardingRankingRequestsRepository.findAllBy(
                    initiativeId,
                    filter,
                    PageRequest.of(page, size, Sort.by(OnboardingRankingRequests.Fields.rank))
            );

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

    @Override
    public void notifyCitizenRankings(String organizationId, String initiativeId) {
        String genericExceptionMessage;
        InitiativeConfig initiativeConfig = rankingContextHolderService.getInitiativeConfig(initiativeId, organizationId);
        if(initiativeConfig.getRankingStatus().equals(RankingStatus.READY)){
            List<OnboardingRankingRequests> onboardingRankingRequests = onboardingRankingRequestsRepository.findAllByOrganizationIdAndInitiativeId(organizationId, initiativeId);
            onboardingRankingRequests = onboardingRankingRequests.stream()
                    .filter(onboardingRankingRequest ->
                            onboardingRankingRequest.getBeneficiaryRankingStatus().equals(BeneficiaryRankingStatus.ELIGIBLE_KO) ||
                            onboardingRankingRequest.getBeneficiaryRankingStatus().equals(BeneficiaryRankingStatus.ELIGIBLE_OK)
                    )
                    .toList();
            if(!onboardingRankingRequests.isEmpty()) {
                log.info("[NOTIFY_CITIZEN] - Sending No. of {} citizen into outbound outcome Topic is about to begin...", onboardingRankingRequests.size());
                onboardingNotifierService.callOnboardingNotifier(initiativeConfig, onboardingRankingRequests);
            }
            else {
                log.info("[NOTIFY_CITIZEN] - No citizen to be notified...");
            }
        }else {
            genericExceptionMessage = String.format("Initiative ranking state [%s] not valid", initiativeConfig.getRankingStatus());
            log.error(genericExceptionMessage);
            throw new IllegalStateException("[NOTIFY_CITIZEN]-[Error] - " + genericExceptionMessage);
        }
    }

}
