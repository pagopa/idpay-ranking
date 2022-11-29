package it.gov.pagopa.ranking.controller;

import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
            @RequestParam(value = "size", required = false, defaultValue = "10") int size);
}
