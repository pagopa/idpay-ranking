package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.controller.RankingPageDTO;
import it.gov.pagopa.ranking.dto.controller.RankingRequestFilter;
import it.gov.pagopa.ranking.dto.controller.RankingRequestsApiDTO;

import java.util.List;

public interface RankingRequestsApiService {

    List<RankingRequestsApiDTO> findByInitiativeId(String organizationId, String initiativeId, int page, int size, RankingRequestFilter filter);

    RankingPageDTO findByInitiativeIdPaged(String organizationId, String initiativeId, int page, int size, RankingRequestFilter filter);

    void notifyCitizenRankings(String organizationId, String initiativeId);
}
