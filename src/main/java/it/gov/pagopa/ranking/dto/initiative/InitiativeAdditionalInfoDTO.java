package it.gov.pagopa.ranking.dto.initiative;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InitiativeAdditionalInfoDTO {
    @JsonProperty("logoFileName")
    private String logoFileName;

}
