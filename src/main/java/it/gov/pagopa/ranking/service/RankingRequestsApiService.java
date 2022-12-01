package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.RankingPageDTO;
import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;

import java.util.List;

public interface RankingRequestsApiService {

    List<RankingRequestsApiDTO> findByInitiativeId(String organizationId, String initiativeId, int page, int size, BeneficiaryRankingStatus beneficiaryRankingStatus);

    RankingPageDTO findByInitiativeIdPaged(String organizationId, String initiativeId, int page, int size, BeneficiaryRankingStatus beneficiaryRankingStatus);
}
