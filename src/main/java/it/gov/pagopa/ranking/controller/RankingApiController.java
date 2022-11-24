package it.gov.pagopa.ranking.controller;

import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.springframework.http.ResponseEntity;
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

    @GetMapping(value = "/initiative/{initiativeId}")
    ResponseEntity<List<OnboardingRankingRequests>> rankingRequests(
            @PathVariable(value = "initiativeId") String initiativeId,
            @RequestParam(value = "page", required = false , defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "2000") int size);
}
