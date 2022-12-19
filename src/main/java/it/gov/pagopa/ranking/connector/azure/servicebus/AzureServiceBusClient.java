package it.gov.pagopa.ranking.connector.azure.servicebus;

import com.azure.messaging.servicebus.ServiceBusReceiverClient;

public interface AzureServiceBusClient {
    ServiceBusReceiverClient getOnboardingRequestReceiverClient();

    ServiceBusReceiverClient getReceiverClient(String queueName);

    int countMessageInOnboardingRequestQueue();

    int countMessageInQueue(String queueName);
}
