package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.OnboardingRankingRequestDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequestsDTO2ModelMapper;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsDTOFaker;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import it.gov.pagopa.ranking.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.nio.charset.StandardCharsets;

@ExtendWith(MockitoExtension.class)
class OnboardingRankingRequestsMediatorImplTest {

    @Mock
    private OnboardingRankingRequestsService onboardingRankingRequestsServiceMock;

    @Mock
    private ErrorNotifierService errorNotifierServiceMock;

    @Mock
    private OnboardingRankingRequestsDTO2ModelMapper onboardingRankingRequestsDTO2ModelMapperMock;

    private OnboardingRankingRequestsMediator onboardingRankingRequestsMediator;

    @BeforeEach
    void setUp() {
        onboardingRankingRequestsMediator = new OnboardingRankingRequestsMediatorImpl("appName",onboardingRankingRequestsServiceMock, errorNotifierServiceMock, onboardingRankingRequestsDTO2ModelMapperMock, TestUtils.objectMapper);
    }

    @Test
    void mediatorOk(){
        // Given
        OnboardingRankingRequestDTO onboarding = OnboardingRankingRequestsDTOFaker.mockInstance(1);

        OnboardingRankingRequests onboardingModel = OnboardingRankingRequestsFaker.mockInstance(1);
        Mockito.when(onboardingRankingRequestsDTO2ModelMapperMock.apply(onboarding)).thenReturn(onboardingModel);

        Mockito.when(onboardingRankingRequestsServiceMock.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);

        // When
        onboardingRankingRequestsMediator.execute(MessageBuilder.withPayload(TestUtils.jsonSerializer(onboarding)).build());

        // Then
        Mockito.verify(onboardingRankingRequestsDTO2ModelMapperMock).apply(Mockito.any());
        Mockito.verify(onboardingRankingRequestsServiceMock).save(Mockito.any());
        Mockito.verify(errorNotifierServiceMock, Mockito.never()).notifyRanking(Mockito.any(Message.class),Mockito.anyString(),Mockito.anyBoolean(),Mockito.any(Throwable.class));
    }

    @Test
    void mediatorAnotherApp(){
        // Given
        OnboardingRankingRequestDTO onboarding = OnboardingRankingRequestsDTOFaker.mockInstance(1);

        // When
        onboardingRankingRequestsMediator.execute(MessageBuilder
                .withPayload(TestUtils.jsonSerializer(onboarding))
                .setHeader(ErrorNotifierServiceImpl.ERROR_MSG_HEADER_APPLICATION_NAME, "OTHER_APP".getBytes(StandardCharsets.UTF_8))
                .build());

        // Then
        Mockito.verify(onboardingRankingRequestsDTO2ModelMapperMock, Mockito.never()).apply(Mockito.any());
        Mockito.verify(onboardingRankingRequestsServiceMock, Mockito.never()).save(Mockito.any());
        Mockito.verify(errorNotifierServiceMock, Mockito.never()).notifyRanking(Mockito.any(Message.class),Mockito.anyString(),Mockito.anyBoolean(),Mockito.any(Throwable.class));
    }
}