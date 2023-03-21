package it.gov.pagopa.ranking.controller;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/idpay")
public interface RankingController {


    /**
     * Used only for testing purposes. Change the ranking end date and the status of an initiative.
     */
    @GetMapping("/initiative/{initiativeId}/ranking/force-end")
    void forceRankingInitiativeEnd(
            @PathVariable String initiativeId
    );

    @GetMapping("/ranking/build/file/start")
    List<InitiativeConfig> forceRankingFileBuildScheduling();
}
