package it.gov.pagopa.ranking.dto.controller;

import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingRequestFilter {
    private BeneficiaryRankingStatus beneficiaryRankingStatus;
    private String userId;
}
