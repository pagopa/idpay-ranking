package it.gov.pagopa.ranking.dto.initiative.filter;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class OnboardingRequestDTO {
    @NotEmpty
    private String userId;
    @NotEmpty
    private String initiativeId;
}
