package it.gov.pagopa.ranking.service;

import it.gov.pagopa.common.kafka.service.ErrorNotifierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class RankingErrorNotifierServiceImplTest {
    private static final String SERVER_TYPE = "SERVER_TYPE";
    private static final String BROKER = "BROKER";
    private static final String DUMMY_MESSAGE="DUMMY MESSAGE";
    private static final Message<String> dummyMessage = MessageBuilder.withPayload(DUMMY_MESSAGE).build();
    @Mock
    private ErrorNotifierService errorNotifierServiceMock;
    private RankingErrorNotifierService rankingErrorNotifierService;

    @BeforeEach
    void setUp() {
        rankingErrorNotifierService = new RankingErrorNotifierServiceImpl(errorNotifierServiceMock,
                SERVER_TYPE, BROKER, "idpay-onboarding-ranking-request-topic", "idpay-onboarding-ranking-request-group",
                SERVER_TYPE, BROKER, "idpay-rule-update-topic", "idpay-rule-update-group",
                SERVER_TYPE, BROKER, "idpay-onboarding-outcome");
    }

    @Test
    void notifyOnboardingRankingRequest() {
        errorNotifyMock("idpay-onboarding-ranking-request-topic", "idpay-onboarding-ranking-request-group", true, true );

        rankingErrorNotifierService.notifyOnboardingRankingRequest(dummyMessage, DUMMY_MESSAGE, true, new Throwable(DUMMY_MESSAGE));

        Mockito.verifyNoMoreInteractions(errorNotifierServiceMock);
    }

    @Test
    void notifyInitiativeBuild() {
        errorNotifyMock("idpay-rule-update-topic", "idpay-rule-update-group", true, true );

        rankingErrorNotifierService.notifyInitiativeBuild(dummyMessage, DUMMY_MESSAGE, true, new Throwable(DUMMY_MESSAGE));

        Mockito.verifyNoMoreInteractions(errorNotifierServiceMock);

    }

    @Test
    void notifyRankingOnboardingOutcome() {
        errorNotifyMock("idpay-onboarding-outcome", null, true, false );

        rankingErrorNotifierService.notifyRankingOnboardingOutcome(dummyMessage, DUMMY_MESSAGE, true, new Throwable(DUMMY_MESSAGE));

        Mockito.verifyNoMoreInteractions(errorNotifierServiceMock);
    }

    private void errorNotifyMock(String topic, String group, boolean retryable, boolean resendApplication ) {
        Mockito.when(errorNotifierServiceMock.notify(eq(SERVER_TYPE), eq(BROKER),
                        eq(topic), eq(group), eq(dummyMessage), eq(DUMMY_MESSAGE), eq(retryable), eq(resendApplication), any()))
                .thenReturn(true);
    }
}