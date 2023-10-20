package it.gov.pagopa.ranking.controller;

import it.gov.pagopa.ranking.dto.controller.RankingPageDTO;
import it.gov.pagopa.ranking.dto.controller.RankingRequestFilter;
import it.gov.pagopa.ranking.dto.controller.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.service.RankingRequestsApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class RankingApiControllerImpl implements RankingApiController{

    private final RankingRequestsApiService rankingRequestsApiService;

    public RankingApiControllerImpl(RankingRequestsApiService rankingRequestsApiService) {
        this.rankingRequestsApiService = rankingRequestsApiService;
    }

    @Override
    public List<RankingRequestsApiDTO> rankingRequests(String organizationId, String initiativeId, int page, int size, RankingRequestFilter filter) {
        log.info("[RANKING_LIST] Requesting ranking list of organizationId {} and initiativeId {}, with page {} and size {}",
                organizationId, initiativeId, page, size);

        return rankingRequestsApiService.findByInitiativeId(organizationId, initiativeId, page, size, filter);

    }

    @Override
    public RankingPageDTO rankingRequestsPaged(String organizationId, String initiativeId, int page, int size, RankingRequestFilter filter) {
        log.info("[RANKING_LIST] Requesting ranking list of organizationId {} and initiativeId {}, with page {} and size {}",
                organizationId, initiativeId, page, size);

        return rankingRequestsApiService.findByInitiativeIdPaged(organizationId, initiativeId, page, size, filter);
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void notifyCitizenRankings(String organizationId, String initiativeId) {
        log.info("[NOTIFY_CITIZEN] - Request to notify Citizen on Ranking List for initiativeId: {}", initiativeId);
        rankingRequestsApiService.notifyCitizenRankings(organizationId, initiativeId);
    }

}
