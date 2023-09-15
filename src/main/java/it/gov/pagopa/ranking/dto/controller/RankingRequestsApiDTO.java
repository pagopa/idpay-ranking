package it.gov.pagopa.ranking.dto.controller;

import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RankingRequestsApiDTO {

    @NotEmpty
    private String userId;
    @NotEmpty
    private String initiativeId;
    @NotEmpty
    private String organizationId;
    @NotNull
    private LocalDateTime admissibilityCheckDate;
    private LocalDateTime criteriaConsensusTimestamp;
    private long rankingValue;
    private long ranking;
    private BeneficiaryRankingStatus beneficiaryRankingStatus;

    //region family
    private String familyId;
    private Set<String> memberIds;
    //endregion
}
