package it.gov.pagopa.ranking.repository;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InitiativeConfigRepository extends MongoRepository<InitiativeConfig, String> {
}
