package it.gov.pagopa.ranking.event.producer;

import it.gov.pagopa.ranking.dto.event.EvaluationRankingDTO;
import it.gov.pagopa.ranking.test.fakers.EvaluationRankingDTOFaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class OnboardingNotifierProducerImplTest {
    @Test
    void onboardingNotifierProducerConfigNotNull(){
        OnboardingNotifierProducerImpl.OnboardingNotifierProducerConfig onboardingNotifierProducerConfig = new OnboardingNotifierProducerImpl.OnboardingNotifierProducerConfig();

        Supplier<Flux<Message<EvaluationRankingDTO>>> result = onboardingNotifierProducerConfig.evaluationOnboardingRanking();

        Assertions.assertNotNull(result);
    }

    @Test
    void testNotify() {
        StreamBridge streamBridgeMock = Mockito.mock(StreamBridge.class);
        OnboardingNotifierProducerImpl producer = new OnboardingNotifierProducerImpl(streamBridgeMock);
        EvaluationRankingDTO evaluationRankingDTO = EvaluationRankingDTOFaker.mockInstance(1);

        Mockito.when(streamBridgeMock.send(Mockito.eq("evaluationOnboardingRanking-out-0"), Mockito.any()))
                .thenReturn(true);


        boolean result = producer.notify(evaluationRankingDTO);

        Assertions.assertTrue(result);
    }
}