package it.gov.pagopa.ranking.dto;

import lombok.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class PageRankingDTO<T> extends PageImpl<T> {
    private static final String RANKING_FIELD = "ranking";

    private String rankingStatus;
    private LocalDateTime rankingPublishedTimeStamp;
    private LocalDateTime rankingGeneratedTimeStamp;

    public PageRankingDTO(
            List<T> content,
            String rankingStatus,
            LocalDateTime rankingPublishedTimeStamp,
            LocalDateTime rankingGeneratedTimeStamp,
            int page,
            int size,
            long total
    ) {
        super(content, PageRequest.of(page, size, Sort.by(RANKING_FIELD)), total);
        this.rankingStatus = rankingStatus;
        this.rankingPublishedTimeStamp = rankingPublishedTimeStamp;
        this.rankingGeneratedTimeStamp = rankingGeneratedTimeStamp;
    }

    public PageRankingDTO() {
        super(new ArrayList<>());
    }

    public PageRankingDTO(List<T> content) {
        super(content);
    }

    public PageRankingDTO(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }
}
