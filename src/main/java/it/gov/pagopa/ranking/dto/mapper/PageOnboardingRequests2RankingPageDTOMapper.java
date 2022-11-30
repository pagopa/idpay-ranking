package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.RankingPageDTO;
import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.RankingStatus;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PageOnboardingRequests2RankingPageDTOMapper {

    public RankingPageDTO apply(
            Page<List<OnboardingRankingRequests>> pageOnboardingRequests,
            List<RankingRequestsApiDTO> content,
            RankingStatus rankingStatus,
            LocalDateTime rankingPublishedTimeStamp,
            LocalDateTime rankingGeneratedTimeStamp) {
        RankingPageDTO out = new RankingPageDTO();

        out.setContent(content);
        out.setPageNumber(pageOnboardingRequests.getNumber());
        out.setPageSize(pageOnboardingRequests.getSize());
        out.setTotalElements(pageOnboardingRequests.getTotalElements());
        out.setTotalPages(pageOnboardingRequests.getTotalPages());
        out.setRankingStatus(rankingStatus);
        out.setRankingPublishedTimeStamp(rankingPublishedTimeStamp);
        out.setRankingGeneratedTimeStamp(rankingGeneratedTimeStamp);

        return out;
    }
}
