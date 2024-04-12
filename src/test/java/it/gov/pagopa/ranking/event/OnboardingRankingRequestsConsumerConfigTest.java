package it.gov.pagopa.ranking.event;

import it.gov.pagopa.ranking.service.onboarding.OnboardingRankingRequestsMediator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

class OnboardingRankingRequestsConsumerConfigTest {

    @Test
    void onboardingRankingRequestsConsumerNotNull() {
        //Given
        OnboardingRankingRequestsConsumerConfig config = new OnboardingRankingRequestsConsumerConfig();
        OnboardingRankingRequestsMediator mediatorMock = Mockito.mock(OnboardingRankingRequestsMediator.class);

        //When
        Consumer<Message<String>> result = config.onboardingRankingRequestsConsumer(mediatorMock);

        //Then
        Assertions.assertNotNull(result);
    }
}