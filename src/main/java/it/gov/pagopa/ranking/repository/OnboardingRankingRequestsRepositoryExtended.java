package it.gov.pagopa.ranking.repository;

import it.gov.pagopa.ranking.dto.controller.RankingRequestFilter;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OnboardingRankingRequestsRepositoryExtended {
    Page<OnboardingRankingRequests> findAllBy(String initiativeId, RankingRequestFilter filter, Pageable pageable);
}
