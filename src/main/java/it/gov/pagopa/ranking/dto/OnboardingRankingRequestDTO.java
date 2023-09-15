package it.gov.pagopa.ranking.dto;

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
public class OnboardingRankingRequestDTO {
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
    private boolean onboardingKo;

    //region family
    private String familyId;
    private Set<String> memberIds;
    //endregion
}
