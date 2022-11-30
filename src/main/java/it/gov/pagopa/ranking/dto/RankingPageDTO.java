package it.gov.pagopa.ranking.dto;

import it.gov.pagopa.ranking.model.RankingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RankingPageDTO {

    private List<RankingRequestsApiDTO> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private long totalPages;
    private RankingStatus rankingStatus;
    private LocalDateTime rankingPublishedTimeStamp;
    private LocalDateTime rankingGeneratedTimeStamp;
    @Builder.Default
    private long totalOnboardingOk = 0;
    @Builder.Default
    private long totalOnboardingKo = 0;

}

