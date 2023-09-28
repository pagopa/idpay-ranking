package it.gov.pagopa.ranking.service.initiative;

import it.gov.pagopa.ranking.dto.event.QueueCommandOperationDTO;
import org.springframework.messaging.Message;

public interface InitiativePersistenceMediator {
    void execute(Message<String> message);
    void processCommand(QueueCommandOperationDTO queueCommandOperationDTO);
}
