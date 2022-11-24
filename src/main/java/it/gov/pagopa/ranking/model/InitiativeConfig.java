package it.gov.pagopa.ranking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InitiativeConfig {
    private String initiativeId;
    private String initiativeName;
    private String organizationId;
    private String status;
//    private LocalDate startDate;
//    private LocalDate endDate;
    private LocalDate rankingStartDate;
    private LocalDate rankingEndDate;
//    private String pdndToken;
//    private List<String> automatedCriteriaCodes;
    private BigDecimal initiativeBudget;
    private BigDecimal beneficiaryInitiativeBudget;
    private String rankingStatus;
    private long size;
    private List<Order> rankingFields;
}
