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
public class InitiativeBuildDTO {
    @JsonProperty("initiativeId")
    private String initiativeId;

    @JsonProperty("initiativeName")
    private String initiativeName;

    @JsonProperty("organizationId")
    private String organizationId;

    @JsonProperty("organizationName")
    private String organizationName;

    @JsonProperty("status")
    private String status;

    @JsonProperty("general")
    private InitiativeGeneralDTO general;
    @JsonProperty("additionalInfo")
    private InitiativeAdditionalInfoDTO additionalInfo;
    @JsonProperty("beneficiaryRule")
    private InitiativeBeneficiaryRuleDTO beneficiaryRule;
    @JsonProperty("initiativeRewardType")
    private String initiativeRewardType;

}
