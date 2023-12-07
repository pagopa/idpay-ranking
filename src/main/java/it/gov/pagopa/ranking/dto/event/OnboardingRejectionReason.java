package it.gov.pagopa.ranking.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingRejectionReason {
    @NotNull
    private OnboardingRejectionReasonType type;
    @NotNull
    private String code;
    private String authority;
    private String authorityLabel;
    private String detail;

    public enum OnboardingRejectionReasonType {
        TECHNICAL_ERROR,
        CONSENSUS_MISSED,
        INVALID_REQUEST,
        BUDGET_EXHAUSTED,
        AUTOMATED_CRITERIA_FAIL,
        OUT_OF_RANKING,
        FAMILY_CRITERIA_KO
    }
}
