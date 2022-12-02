package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.controller.RankingPageDTO;
import it.gov.pagopa.ranking.dto.controller.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageOnboardingRequests2RankingPageDTOMapper {

    public RankingPageDTO apply(
            Page<OnboardingRankingRequests> pageOnboardingRequests,
            List<RankingRequestsApiDTO> content,
            InitiativeConfig initiativeConfig) {
        RankingPageDTO out = new RankingPageDTO();

        out.setContent(content);
        out.setPageNumber(pageOnboardingRequests.getNumber());
        out.setPageSize(pageOnboardingRequests.getSize());
        out.setTotalElements(pageOnboardingRequests.getTotalElements());
        out.setTotalPages(pageOnboardingRequests.getTotalPages());
        out.setRankingStatus(initiativeConfig.getRankingStatus());
        out.setRankingPublishedTimeStamp(initiativeConfig.getRankingPublishedTimeStamp());
        out.setRankingGeneratedTimeStamp(initiativeConfig.getRankingGeneratedTimeStamp());
        out.setRankingFilePath(initiativeConfig.getRankingPathFile());
        out.setTotalEligibleOk(initiativeConfig.getTotalEligibleOk());
        out.setTotalEligibleKo(initiativeConfig.getTotalEligibleKo());
        out.setTotalOnboardingKo(initiativeConfig.getTotalOnboardingKo());

        return out;
    }
}
