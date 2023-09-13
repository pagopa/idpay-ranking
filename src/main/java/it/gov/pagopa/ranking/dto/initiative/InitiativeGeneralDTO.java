package it.gov.pagopa.ranking.dto.initiative;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InitiativeGeneralDTO {
    @JsonProperty("budget")
    private BigDecimal budget;

    @JsonProperty("beneficiaryType")
    private BeneficiaryTypeEnum beneficiaryType;

    @JsonProperty("beneficiaryBudget")
    private BigDecimal beneficiaryBudget;

    /**
     * Start of period of participation/adhesion in an initiative
     */
    @JsonProperty("rankingStartDate")
    private LocalDate rankingStartDate;

    /**
     * End of period of participation/adhesion in an initiative
     */
    @JsonProperty("rankingEndDate")
    private LocalDate rankingEndDate;

    @JsonProperty("endDate")
    private LocalDate endDate;


    @JsonProperty("rankingEnabled")
    private boolean rankingEnabled;

    public enum BeneficiaryTypeEnum {
        /** Individual (Persona Fisica) */
        PF("PF"),
        /** Legal Person (Persona Giuridica) */
        PG("PG"),
        /** Family (Nucleo Familiare) */
        NF("NF");

        private String value;

        BeneficiaryTypeEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static BeneficiaryTypeEnum fromValue(String text) {
            for (BeneficiaryTypeEnum b : BeneficiaryTypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

}
