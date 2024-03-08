package it.gov.pagopa.ranking.event;

import it.gov.pagopa.ranking.service.initiative.InitiativePersistenceMediator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

class InitiativeConsumerConfigTest {

    @Test
    void initiativeRankingConsumerNotNull() {
        //Given
        InitiativeConsumerConfig initiativeConsumerConfig = new InitiativeConsumerConfig();
        InitiativePersistenceMediator mediatorMock = Mockito.mock(InitiativePersistenceMediator.class);

        //When
        Consumer<Message<String>> result = initiativeConsumerConfig.initiativeRankingConsumer(mediatorMock);

        //Then
        Assertions.assertNotNull(result);
    }
}