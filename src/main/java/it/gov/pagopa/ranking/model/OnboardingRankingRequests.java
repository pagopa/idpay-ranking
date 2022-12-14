package it.gov.pagopa.ranking.model;

import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@FieldNameConstants
@Document(collection = "onboarding_ranking_requests")
public class OnboardingRankingRequests {
    @Id
    private String id;
    @NotEmpty
    private String userId;
    @NotEmpty
    private String initiativeId;
    @NotEmpty
    private String organizationId;
    @NotNull
    private LocalDateTime admissibilityCheckDate;
    private LocalDateTime criteriaConsensusTimestamp;
    /** the value used to sort initiative data (the direction is configured inside the {@link InitiativeConfig}. When {@link #beneficiaryRankingStatus} is {@link BeneficiaryRankingStatus#ONBOARDING_KO} it will contain a dummy value in order to give to it the lowest precedence */
    private long rankingValue;
    /** the original rankingValue provided together with {@link it.gov.pagopa.ranking.dto.OnboardingRankingRequestDTO}. Backup value stored in order to trace it in the use case where the rankingValue could be manually updated */
    private long rankingValueOriginal;
    /** the ranking value to show or to return for display purpose: only when {@link #beneficiaryRankingStatus} is {@link BeneficiaryRankingStatus#ONBOARDING_KO} it will differ from {@link #rankingValue} */
    private long rankingValue2Show;
    private long rank;
    private BeneficiaryRankingStatus beneficiaryRankingStatus;
}
