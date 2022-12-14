package it.gov.pagopa.ranking.repository;

import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Meta;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OnboardingRankingRequestsRepository extends MongoRepository<OnboardingRankingRequests,String>, OnboardingRankingRequestsRepositoryExtended {

    Page<OnboardingRankingRequests> findByInitiativeId(String initiativeId, Pageable pageable);

    @Meta(cursorBatchSize = 10_000)
    List<OnboardingRankingRequests> findAllByOrganizationIdAndInitiativeId(String organizationId, String initiativeId);

    List<OnboardingRankingRequests> findAllByInitiativeId(String initiativeId, Pageable pageable);
}
