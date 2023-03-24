package it.gov.pagopa.ranking.dto.event;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private String initiativeName;
    private LocalDate initiativeEndDate;
    @NotEmpty
    private String organizationId;
    @NotNull
    private LocalDateTime admissibilityCheckDate;
    private LocalDateTime criteriaConsensusTimestamp;
    @NotEmpty
    private String status;
    @NotNull
    private List<OnboardingRejectionReason> onboardingRejectionReasons;
    private BigDecimal beneficiaryBudget;
}
