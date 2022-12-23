package it.gov.pagopa.ranking.service.evaluate.retrieve;

import com.azure.core.util.BinaryData;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import it.gov.pagopa.ranking.connector.azure.servicebus.AzureServiceBusClient;
import it.gov.pagopa.ranking.dto.initiative.OnboardingRequestPendingDTO;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.service.initiative.InitiativeConfigService;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OnboardingRankingRuleEndedRetrieverServiceTest {

    @Mock
    private InitiativeConfigService initiativeConfigServiceMock;
    @Mock
    private AzureServiceBusClient azureServiceBusClientMock;

    private OnboardingRankingRuleEndedRetrieverService service;

    @BeforeEach
    void init(){
        service = new OnboardingRankingRuleEndedRetrieverServiceImpl(initiativeConfigServiceMock, 7, azureServiceBusClientMock);
    }

    @Test
    void testNoEndedInitiatives(){
        // Given
        LocalDate now = LocalDate.now();
        Mockito.when(initiativeConfigServiceMock.findByRankingStatusRankingEndDateBetween(RankingStatus.WAITING_END, now.minusDays(8), now))
                .thenReturn(Collections.emptyList());

        // When
        List<InitiativeConfig> result = service.retrieve();

        // Then
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @Test
    void testEndedInitiativesWithoutPendingData_emptyQueue(){
        // Given
        LocalDate now = LocalDate.now();
        InitiativeConfig endedInitiative = InitiativeConfigFaker.mockInstance(1);

        Mockito.when(initiativeConfigServiceMock.findByRankingStatusRankingEndDateBetween(RankingStatus.WAITING_END, now.minusDays(8), now))
                .thenReturn(List.of(endedInitiative));

        Mockito.when(azureServiceBusClientMock.countMessageInOnboardingRequestQueue()).thenReturn(0);

        // When
        List<InitiativeConfig> result = service.retrieve();

        // Then
        Assertions.assertEquals(List.of(endedInitiative), result);
    }

    @Test
    void testEndedInitiativesWithoutPendingData_initiativeNotInQueue(){
        // Given
        LocalDate now = LocalDate.now();
        InitiativeConfig endedInitiative = InitiativeConfigFaker.mockInstance(1);

        Mockito.when(initiativeConfigServiceMock.findByRankingStatusRankingEndDateBetween(RankingStatus.WAITING_END, now.minusDays(8), now))
                .thenReturn(List.of(endedInitiative));

        Mockito.when(azureServiceBusClientMock.countMessageInOnboardingRequestQueue()).thenReturn(1);

        ServiceBusReceiverClient serviceBusReceiverClientMock = Mockito.mock(ServiceBusReceiverClient.class);
        Mockito.when(azureServiceBusClientMock.getOnboardingRequestReceiverClient()).thenReturn(serviceBusReceiverClientMock);

        ServiceBusReceivedMessage receivedMessageMock = Mockito.mock(ServiceBusReceivedMessage.class);
        Mockito.when(serviceBusReceiverClientMock.peekMessage()).thenReturn(receivedMessageMock);

        BinaryData binaryDataMock = Mockito.mock(BinaryData.class);
        Mockito.when(receivedMessageMock.getBody()).thenReturn(binaryDataMock);

        Mockito.when(binaryDataMock.toObject(OnboardingRequestPendingDTO.class))
                .thenReturn(OnboardingRequestPendingDTO.builder()
                        .initiativeId("OTHERINITIATIVE")
                        .build());

        // When
        List<InitiativeConfig> result = service.retrieve();

        // Then
        Assertions.assertEquals(List.of(endedInitiative), result);
    }

    @Test
    void testEndedInitiativesWithPendingData(){
        // Given
        LocalDate now = LocalDate.now();
        InitiativeConfig endedInitiative = InitiativeConfigFaker.mockInstance(1);

        Mockito.when(initiativeConfigServiceMock.findByRankingStatusRankingEndDateBetween(RankingStatus.WAITING_END, now.minusDays(8), now))
                .thenReturn(List.of(endedInitiative));

        Mockito.when(azureServiceBusClientMock.countMessageInOnboardingRequestQueue()).thenReturn(1);

        ServiceBusReceiverClient serviceBusReceiverClientMock = Mockito.mock(ServiceBusReceiverClient.class);
        Mockito.when(azureServiceBusClientMock.getOnboardingRequestReceiverClient()).thenReturn(serviceBusReceiverClientMock);

        ServiceBusReceivedMessage receivedMessageMock = Mockito.mock(ServiceBusReceivedMessage.class);
        Mockito.when(serviceBusReceiverClientMock.peekMessage()).thenReturn(receivedMessageMock);

        BinaryData binaryDataMock = Mockito.mock(BinaryData.class);
        Mockito.when(receivedMessageMock.getBody()).thenReturn(binaryDataMock);

        Mockito.when(binaryDataMock.toObject(OnboardingRequestPendingDTO.class))
                .thenReturn(OnboardingRequestPendingDTO.builder()
                        .initiativeId(endedInitiative.getInitiativeId())
                        .build());

        // When
        List<InitiativeConfig> result = service.retrieve();

        // Then
        Assertions.assertEquals(Collections.emptyList(), result);
    }
}
