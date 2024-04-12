package it.gov.pagopa.ranking.event.consumer;

import com.mongodb.assertions.Assertions;
import it.gov.pagopa.ranking.dto.event.QueueCommandOperationDTO;
import it.gov.pagopa.ranking.service.initiative.InitiativePersistenceMediator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;

class CommandsConsumerTest {

    @Test
    void consumerCommandsNotNull() {
        CommandsConsumer commandsConsumer = new CommandsConsumer();
        InitiativePersistenceMediator initiativePersistenceMediatorMock = Mockito.mock(InitiativePersistenceMediator.class);

        Consumer<QueueCommandOperationDTO> result = commandsConsumer.consumerCommands(initiativePersistenceMediatorMock);

        Assertions.assertNotNull(result);
    }
}