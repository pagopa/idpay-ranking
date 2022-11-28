package it.gov.pagopa.ranking.controller;

import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.exception.ClientExceptionNoBody;
import it.gov.pagopa.ranking.service.RankingRequestsApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
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
    public List<RankingRequestsApiDTO> rankingRequests(String organizationId, String initiativeId, int page, int size) {
        log.info("[RANKING_LIST] Requesting ranking list of initiativeId {} and organizationId {}, with page {} and size {}",
                initiativeId, organizationId, page, size);

       List<RankingRequestsApiDTO> result = rankingRequestsApiService.findByInitiativeId(organizationId, initiativeId, page, size);

       if (result == null) {
           throw new ClientExceptionNoBody(HttpStatus.NOT_FOUND);
       } else {
           return result;
       }
    }

}
