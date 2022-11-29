package it.gov.pagopa.ranking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/idpay")
public interface RankingController {
    @GetMapping("/ranking/build/file/start")
    ResponseEntity<String> forceRankingFileBuildScheduling(); //TODO chance return type
}
