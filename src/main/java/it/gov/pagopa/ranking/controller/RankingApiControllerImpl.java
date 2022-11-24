package it.gov.pagopa.ranking.controller;

import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.service.RankingRequestsApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<RankingRequestsApiDTO>> rankingRequests(String organizationId, String initiativeId, int page, int size) {
        return ResponseEntity.ok(rankingRequestsApiService.findByInitiativeId(organizationId, initiativeId, page, size));
    }

    // TODO 404
}
