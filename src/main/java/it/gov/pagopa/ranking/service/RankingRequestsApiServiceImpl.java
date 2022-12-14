package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.controller.RankingPageDTO;
import it.gov.pagopa.ranking.dto.controller.RankingRequestFilter;
import it.gov.pagopa.ranking.dto.controller.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequest2RankingRequestsApiDTOMapper;
import it.gov.pagopa.ranking.dto.mapper.PageOnboardingRequests2RankingPageDTOMapper;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import it.gov.pagopa.ranking.service.initiative.InitiativeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final InitiativeConfigService initiativeConfigService;

    public RankingRequestsApiServiceImpl(
            OnboardingRankingRequestsRepository onboardingRankingRequestsRepository,
            OnboardingRankingRequest2RankingRequestsApiDTOMapper rankingRequestsApiDTOMapper,
            PageOnboardingRequests2RankingPageDTOMapper pageDtoMapper,
            RankingContextHolderService rankingContextHolderService,
            OnboardingNotifierService onboardingNotifierService,
            InitiativeConfigService initiativeConfigService) {
        this.onboardingRankingRequestsRepository = onboardingRankingRequestsRepository;
        this.dtoMapper = rankingRequestsApiDTOMapper;
        this.pageDtoMapper = pageDtoMapper;
        this.rankingContextHolderService = rankingContextHolderService;
        this.onboardingNotifierService = onboardingNotifierService;
        this.initiativeConfigService = initiativeConfigService;
    }

    @Override
    public List<RankingRequestsApiDTO> findByInitiativeId(String organizationId, String initiativeId, int page, int size, RankingRequestFilter filter) {

        InitiativeConfig initiative = rankingContextHolderService.getInitiativeConfig(initiativeId, organizationId);
        if (initiative == null) {
            return null;
        } else {
            if (!initiative.getRankingStatus().equals(RankingStatus.WAITING_END)) {
                return onboardingRankingRequestsRepository.findAllBy(
                                initiativeId,
                                filter,
                                PageRequest.of(page, size, Sort.by(OnboardingRankingRequests.Fields.rank))
                        ).getContent()
                        .stream()
                        .map(dtoMapper::apply)
                        .toList();
            } else {
                return Collections.emptyList();
            }
        }
    }

    @Override
    public RankingPageDTO findByInitiativeIdPaged(String organizationId, String initiativeId, int page, int size, RankingRequestFilter filter) {

        InitiativeConfig initiative = rankingContextHolderService.getInitiativeConfig(initiativeId, organizationId);
        if (initiative == null) {
            return null;
        } else {

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
    }

    @Override
    @Async
    public void notifyCitizenRankings(String organizationId, String initiativeId) {
        String genericExceptionMessage;
        InitiativeConfig initiative = initiativeConfigService.findByIdOptional(initiativeId).orElseThrow(
                () -> {
                    String exceptionMessage = "Initiative not found";
                    log.error(exceptionMessage);
                    return new IllegalStateException("[NOTIFY_CITIZEN]-[ENTITY-DOCUMENT]-[Error] - " + exceptionMessage);
                });
        if(initiative.getRankingStatus().equals(RankingStatus.READY)){
            List<OnboardingRankingRequests> onboardingRankingRequests = onboardingRankingRequestsRepository.findAllByOrganizationIdAndInitiativeId(organizationId, initiativeId);
            if(!onboardingRankingRequests.isEmpty()) {
                initiative.setRankingStatus(RankingStatus.PUBLISHING);
                initiativeConfigService.save(initiative);
                log.info("[NOTIFY_CITIZEN] - Sending citizen into outbound outcome Topic is about to begin...");
                onboardingRankingRequests.forEach(onboardingNotifierService::callOnboardingNotifier); //Check if parallel is needed and calculate Numb of Threads necessary
            }
            initiative.setRankingPublishedTimestamp(LocalDateTime.now());
            initiative.setRankingStatus(RankingStatus.COMPLETED);
            initiativeConfigService.save(initiative); //TODO need to send the modification to someone?
        }else {
            genericExceptionMessage = String.format("Initiative ranking state [%s] not valid", initiative.getRankingStatus());
            log.error(genericExceptionMessage);
            throw new IllegalStateException("[NOTIFY_CITIZEN]-[Error] - " + genericExceptionMessage);
        }
    }

}
