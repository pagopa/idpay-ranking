package it.gov.pagopa.ranking.controller;

import it.gov.pagopa.ranking.dto.controller.RankingPageDTO;
import it.gov.pagopa.ranking.dto.controller.RankingRequestFilter;
import it.gov.pagopa.ranking.dto.controller.RankingRequestsApiDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Component that exposes APIs
 * */
@RequestMapping("/idpay/ranking")
public interface RankingApiController {

    @GetMapping(value = "/organization/{organizationId}/initiative/{initiativeId}")
    List<RankingRequestsApiDTO> rankingRequests(
            @PathVariable(value = "organizationId") String organizationId,
            @PathVariable(value = "initiativeId") String initiativeId,
            @RequestParam(value = "page", required = false , defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            RankingRequestFilter filter);

    @GetMapping(value = "/organization/{organizationId}/initiative/{initiativeId}/paged")
    RankingPageDTO rankingRequestsPaged(
            @PathVariable(value = "organizationId") String organizationId,
            @PathVariable(value = "initiativeId") String initiativeId,
            @RequestParam(value = "page", required = false , defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            RankingRequestFilter filter);

    @PutMapping(value = "/organization/{organizationId}/initiative/{initiativeId}/notified")
    void notifyCitizenRankings(
            @PathVariable(value = "organizationId") String organizationId,
            @PathVariable(value = "initiativeId") String initiativeId);

}
