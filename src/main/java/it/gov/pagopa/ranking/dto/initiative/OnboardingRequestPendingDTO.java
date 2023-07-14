package it.gov.pagopa.ranking.dto.initiative;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
@Builder
public class OnboardingRequestPendingDTO {
    @NotEmpty
    private String userId;
    @NotEmpty
    private String initiativeId;
}
