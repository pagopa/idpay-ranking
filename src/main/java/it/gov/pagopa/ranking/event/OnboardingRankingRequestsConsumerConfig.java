package it.gov.pagopa.ranking.event;

import it.gov.pagopa.ranking.service.OnboardingRankingRequestsMediator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
public class OnboardingRankingRequestsConsumerConfig {

    @Bean
    public Consumer<Message<String>> onboardingRankingRequestsConsumer(OnboardingRankingRequestsMediator onboardingRankingRequestsMediator){
        return onboardingRankingRequestsMediator::execute;
    }
}
