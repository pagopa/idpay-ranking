package it.gov.pagopa.ranking.event.producer;

import it.gov.pagopa.ranking.dto.event.EvaluationRankingDTO;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

@Service
public class OnboardingNotifierProducerImpl implements OnboardingNotifierProducer {

    private final StreamBridge streamBridge;

    public OnboardingNotifierProducerImpl(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    /** Declared just to let know Spring to connect the producer at startup */
    @Configuration
    static class OnboardingNotifierProducerConfig {
        @Bean
        public Supplier<Flux<Message<EvaluationRankingDTO>>> evaluationOnboardingRanking() {
            return Flux::empty;
        }
    }

    @Override
    public boolean notify(EvaluationRankingDTO evaluationDTO) {
        return streamBridge.send("evaluationOnboardingRanking-out-0",
                buildMessage(evaluationDTO));
    }

    public static Message<EvaluationRankingDTO> buildMessage(EvaluationRankingDTO evaluationDTO){
        return MessageBuilder.withPayload(evaluationDTO).build();
    }
}
