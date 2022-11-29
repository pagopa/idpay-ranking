package it.gov.pagopa.ranking.controller;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/idpay")
public interface RankingController {
    @GetMapping("/ranking/build/file/start")
    ResponseEntity<List<InitiativeConfig>> forceRankingFileBuildScheduling();
}
