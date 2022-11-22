package it.gov.pagopa.idpay.ranking.repository;

import it.gov.pagopa.idpay.ranking.model.OnboardingRankingRequests;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OnboardingRankingRequestsRepository extends MongoRepository<OnboardingRankingRequests,String> {
}
