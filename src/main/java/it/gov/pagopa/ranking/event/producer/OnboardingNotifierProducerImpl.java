package it.gov.pagopa.ranking.event.producer;

import it.gov.pagopa.ranking.dto.event.EvaluationRankingDTO;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class OnboardingNotifierProducerImpl implements OnboardingNotifierProducer {

    private final StreamBridge streamBridge;

    public OnboardingNotifierProducerImpl(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
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
