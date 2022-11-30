package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.PageRankingDTO;
import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;

import java.util.List;

public interface RankingRequestsApiService {

    List<RankingRequestsApiDTO> findByInitiativeId(String organizationId, String initiativeId, int page, int size);

    PageRankingDTO<RankingRequestsApiDTO> findByInitiativeIdPaged(String organizationId, String initiativeId, int page, int size);
}
