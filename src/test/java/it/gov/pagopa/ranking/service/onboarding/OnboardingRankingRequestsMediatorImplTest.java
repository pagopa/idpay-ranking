package it.gov.pagopa.ranking.service.onboarding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoException;
import it.gov.pagopa.ranking.dto.OnboardingRankingRequestDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequestsDTO2ModelMapper;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.repository.InitiativeConfigRepository;
import it.gov.pagopa.ranking.service.ErrorNotifierService;
import it.gov.pagopa.ranking.service.ErrorNotifierServiceImpl;
import it.gov.pagopa.ranking.service.OnboardingRankingRequestsService;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
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
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OnboardingRankingRequestsMediatorImplTest {

    @Mock
    private OnboardingRankingRequestsService onboardingRankingRequestsServiceMock;

    @Mock
    private InitiativeConfigRepository initiativeConfigRepositoryMock;

    @Mock
    private ErrorNotifierService errorNotifierServiceMock;

    @Mock
    private OnboardingRankingRequestsDTO2ModelMapper onboardingRankingRequestsDTO2ModelMapperMock;

    private OnboardingRankingRequestsMediator onboardingRankingRequestsMediator;

    @BeforeEach
    void setUp() {
        onboardingRankingRequestsMediator = new OnboardingRankingRequestsMediatorImpl("appName",onboardingRankingRequestsServiceMock, initiativeConfigRepositoryMock, errorNotifierServiceMock, onboardingRankingRequestsDTO2ModelMapperMock, TestUtils.objectMapper);
    }

    @Test
    void mediatorOk(){
        // Given
        OnboardingRankingRequestDTO onboarding = OnboardingRankingRequestsDTOFaker.mockInstance(1);

        OnboardingRankingRequests onboardingModel = OnboardingRankingRequestsFaker.mockInstance(1);

        InitiativeConfig initiative = InitiativeConfigFaker.mockInstance(0);
        Mockito.when(initiativeConfigRepositoryMock.findById(onboarding.getInitiativeId())).thenReturn(Optional.of(initiative));

        Mockito.when(onboardingRankingRequestsDTO2ModelMapperMock.apply(onboarding, initiative)).thenReturn(onboardingModel);

        Mockito.when(onboardingRankingRequestsServiceMock.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);

        Message<String> message = MessageBuilder.withPayload(TestUtils.jsonSerializer(onboarding)).build();
        // When
        onboardingRankingRequestsMediator.execute(message);

        // Then
        Mockito.verify(onboardingRankingRequestsDTO2ModelMapperMock).apply(Mockito.any(), Mockito.any());
        Mockito.verify(onboardingRankingRequestsServiceMock).save(Mockito.any());
        Mockito.verify(errorNotifierServiceMock, Mockito.never()).notifyOnboardingRankingRequest(Mockito.any(Message.class),Mockito.anyString(),Mockito.anyBoolean(),Mockito.any(Throwable.class));
    }

    @Test
    void noInitiative(){
        // Given
        OnboardingRankingRequestDTO onboarding = OnboardingRankingRequestsDTOFaker.mockInstance(1);

        Mockito.when(initiativeConfigRepositoryMock.findById(onboarding.getInitiativeId())).thenReturn(Optional.empty());

        Message<String> message = MessageBuilder.withPayload(TestUtils.jsonSerializer(onboarding)).build();
        // When
        onboardingRankingRequestsMediator.execute(message);

        // Then
        Mockito.verify(errorNotifierServiceMock).notifyOnboardingRankingRequest(Mockito.any(Message.class),Mockito.anyString(),Mockito.anyBoolean(),Mockito.any(Throwable.class));
    }

    @Test
    void mediatorAnotherApp(){
        // Given
        OnboardingRankingRequestDTO onboarding = OnboardingRankingRequestsDTOFaker.mockInstance(1);
        Message<String> messageFromAnotherApp = MessageBuilder
                .withPayload(TestUtils.jsonSerializer(onboarding))
                .setHeader(ErrorNotifierServiceImpl.ERROR_MSG_HEADER_APPLICATION_NAME, "OTHER_APP".getBytes(StandardCharsets.UTF_8))
                .build();
        // When
        onboardingRankingRequestsMediator.execute(messageFromAnotherApp);

        // Then
        Mockito.verify(onboardingRankingRequestsDTO2ModelMapperMock, Mockito.never()).apply(Mockito.any(), Mockito.any());
        Mockito.verify(onboardingRankingRequestsServiceMock, Mockito.never()).save(Mockito.any());
        Mockito.verify(errorNotifierServiceMock, Mockito.never()).notifyOnboardingRankingRequest(Mockito.any(Message.class),Mockito.anyString(),Mockito.anyBoolean(),Mockito.any(Throwable.class));
    }

    @Test
    void mediatorJsonNotValid(){
        // Given
        Message<String> jsonNotValidMessage = MessageBuilder
                .withPayload("JsonNotValid")
                .build();
        // When
        onboardingRankingRequestsMediator.execute(jsonNotValidMessage);

        // Then
        Mockito.verify(onboardingRankingRequestsDTO2ModelMapperMock, Mockito.never()).apply(Mockito.any(), Mockito.any());
        Mockito.verify(onboardingRankingRequestsServiceMock, Mockito.never()).save(Mockito.any());
        Mockito.verify(errorNotifierServiceMock).notifyOnboardingRankingRequest(Mockito.any(Message.class),Mockito.anyString(),Mockito.anyBoolean(),Mockito.any(JsonProcessingException.class));
    }

    @Test
    void mediatorMongoException(){
        // Given
        OnboardingRankingRequestDTO onboarding = OnboardingRankingRequestsDTOFaker.mockInstance(1);

        OnboardingRankingRequests onboardingModel = OnboardingRankingRequestsFaker.mockInstance(1);

        InitiativeConfig initiative = InitiativeConfigFaker.mockInstance(0);
        Mockito.when(initiativeConfigRepositoryMock.findById(onboarding.getInitiativeId())).thenReturn(Optional.of(initiative));

        Mockito.when(onboardingRankingRequestsDTO2ModelMapperMock.apply(onboarding, initiative)).thenReturn(onboardingModel);

        Mockito.when(onboardingRankingRequestsServiceMock.save(Mockito.any())).thenThrow(MongoException.class);

        Message<String> messageMongoException = MessageBuilder.withPayload(TestUtils.jsonSerializer(onboarding)).build();
        // When
        onboardingRankingRequestsMediator.execute(messageMongoException);

        // Then
        Mockito.verify(onboardingRankingRequestsDTO2ModelMapperMock).apply(Mockito.any(), Mockito.any());
        Mockito.verify(onboardingRankingRequestsServiceMock).save(Mockito.any());
        Mockito.verify(errorNotifierServiceMock).notifyOnboardingRankingRequest(Mockito.any(Message.class),Mockito.anyString(),Mockito.anyBoolean(),Mockito.any(MongoException.class));
    }
}