package it.gov.pagopa.ranking.controller;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/idpay")
public interface RankingController {


    /**
     * Used only for testing purposes. Change the ranking end date and the status of an initiative.
     */
    @PutMapping("/initiative/{initiativeId}/reset-status-set-ranking-end-date")
    void updateRankingInitiativeEnd(
            @PathVariable String initiativeId,
            @RequestParam LocalDate rankingEndDate
    );

    @GetMapping("/ranking/build/file/start")
    List<InitiativeConfig> forceRankingFileBuildScheduling();
}
