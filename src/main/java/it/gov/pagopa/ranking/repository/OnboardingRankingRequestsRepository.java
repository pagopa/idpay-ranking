package it.gov.pagopa.ranking.repository;

import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OnboardingRankingRequestsRepository extends MongoRepository<OnboardingRankingRequests,String> {
}
