package it.gov.pagopa.ranking.dto.initiative;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * InitiativeBeneficiaryRuleDTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class InitiativeBeneficiaryRuleDTO {
    @JsonProperty("automatedCriteria")
    private List<AutomatedCriteriaDTO> automatedCriteria;
}