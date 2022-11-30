package it.gov.pagopa.ranking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "onboarding_ranking_rule")
public class InitiativeConfig {
    @Id
    private String initiativeId;
    private String initiativeName;
    private String organizationId;
    private String initiativeStatus;
    private LocalDate rankingStartDate;
    private LocalDate rankingEndDate;
    private BigDecimal initiativeBudget;
    private BigDecimal beneficiaryInitiativeBudget;
    private RankingStatus rankingStatus;
    private long size;
    private List<Order> rankingFields;
    private String rankingPathFile;
    private LocalDateTime rankingGeneratedTimeStamp;
    private LocalDateTime rankingPublishedTimeStamp;
}
