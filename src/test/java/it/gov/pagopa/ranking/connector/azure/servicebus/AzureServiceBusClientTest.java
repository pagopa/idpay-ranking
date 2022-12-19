package it.gov.pagopa.ranking.connector.azure.servicebus;

import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.azure.messaging.servicebus.administration.models.QueueRuntimeProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AzureServiceBusClientTest {

    @Mock(answer = Answers.RETURNS_MOCKS)
    private ServiceBusAdministrationClient serviceBusAdminClientMock;

    private AzureServiceBusClient service;

    @BeforeEach
    void init(){
        service = new AzureServiceBusClientImpl(
                "Endpoint=sb://ServiceBusEndpoint;SharedAccessKeyName=sharedAccessKeyName;SharedAccessKey=sharedAccessKey;EntityPath=entityPath",
                "entityPath",
                serviceBusAdminClientMock
        );
    }

    @Test
    void testCountMessage(){
        QueueRuntimeProperties queueRuntimePropertiesMock = Mockito.mock(QueueRuntimeProperties.class);
        Mockito.when(queueRuntimePropertiesMock.getActiveMessageCount()).thenReturn(3);
        Mockito.when(queueRuntimePropertiesMock.getScheduledMessageCount()).thenReturn(2);

        Mockito.when(serviceBusAdminClientMock.getQueueRuntimeProperties("entityPath")).thenReturn(queueRuntimePropertiesMock);

        int result = service.countMessageInOnboardingRequestQueue();

        Assertions.assertEquals(5, result);
    }

    @Test
    void testGetReceiverClient(){
        ServiceBusReceiverClient result = service.getOnboardingRequestReceiverClient();
        Assertions.assertNotNull(result);
        Assertions.assertEquals("entityPath", result.getEntityPath());
        Assertions.assertEquals("servicebusendpoint", result.getFullyQualifiedNamespace());
    }
}
