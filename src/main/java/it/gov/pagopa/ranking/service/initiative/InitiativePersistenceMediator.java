package it.gov.pagopa.ranking.service.initiative;

import org.springframework.messaging.Message;

public interface InitiativePersistenceMediator {
    void execute(Message<String> message);
}
