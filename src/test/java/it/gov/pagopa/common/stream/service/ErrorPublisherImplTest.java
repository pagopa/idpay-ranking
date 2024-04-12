package it.gov.pagopa.common.stream.service;

import it.gov.pagopa.common.kafka.service.ErrorPublisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
class ErrorPublisherImplTest {

    @Mock
    private StreamBridge streamBridgeMock;

    @Test
    void errorNotifierProducerConfigNotNull(){
        //Given
        ErrorPublisherImpl.ErrorNotifierProducerConfig producerConfig = new ErrorPublisherImpl.ErrorNotifierProducerConfig();

        //When
        Supplier<Flux<Message<Object>>> result = producerConfig.errors();

        //Then
        Assertions.assertNotNull(result);
    }

    @Test
    void sendTest(){
        //Given
        ErrorPublisher errorPublisher = new ErrorPublisherImpl(streamBridgeMock);
        Message<String> message = MessageBuilder.withPayload("DUMMY_MESSAGE").build();
        Mockito.when(streamBridgeMock.send("errors-out-0", message)).thenReturn(true);

        //When
        boolean result = errorPublisher.send(message);

        //Then
        Assertions.assertTrue(result);

    }

}