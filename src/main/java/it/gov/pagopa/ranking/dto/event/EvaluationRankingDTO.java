package it.gov.pagopa.ranking.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class EvaluationRankingDTO {
    @NotEmpty
    private String userId;
    @NotEmpty
    private String initiativeId;
    @NotEmpty
    private String organizationId;
    @NotNull
    private LocalDateTime admissibilityCheckDate;
    private LocalDateTime criteriaConsensusTimestamp;
    @NotEmpty
    private String status;
    private Long rankingValue;
}
