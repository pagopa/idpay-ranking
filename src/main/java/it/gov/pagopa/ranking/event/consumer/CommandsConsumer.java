package it.gov.pagopa.ranking.event.consumer;

import it.gov.pagopa.ranking.dto.event.QueueCommandOperationDTO;
import it.gov.pagopa.ranking.service.initiative.InitiativePersistenceMediator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class CommandsConsumer {

    @Bean
    public Consumer<QueueCommandOperationDTO> consumerCommands(InitiativePersistenceMediator initiativePersistenceMediator) {
        return initiativePersistenceMediator::processCommand;
    }

}
