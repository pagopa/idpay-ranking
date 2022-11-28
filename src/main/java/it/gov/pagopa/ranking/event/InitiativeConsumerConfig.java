package it.gov.pagopa.ranking.event;

import it.gov.pagopa.ranking.service.initiative.InitiativePersistenceMediator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
public class InitiativeConsumerConfig {

    @Bean
    public Consumer<Message<String>> initiativeRankingConsumer(InitiativePersistenceMediator initiativePersistenceMediator){
        return initiativePersistenceMediator::execute;
    }
}
