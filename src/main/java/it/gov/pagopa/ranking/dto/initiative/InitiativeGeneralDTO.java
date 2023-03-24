package it.gov.pagopa.ranking.dto.initiative;

import com.fasterxml.jackson.annotation.JsonProperty;
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

}
