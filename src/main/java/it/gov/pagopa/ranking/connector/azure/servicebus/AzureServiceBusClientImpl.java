package it.gov.pagopa.ranking.connector.azure.servicebus;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.azure.messaging.servicebus.administration.models.QueueRuntimeProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AzureServiceBusClientImpl implements AzureServiceBusClient {

    private final String connectionString;
    private final String onboardingRequestQueueName;
    private final ServiceBusAdministrationClient adminClient;

    public AzureServiceBusClientImpl(
            @Value("${app.service-bus.onboarding-request-pending.string-connection}") String connectionString,
            @Value("${app.service-bus.onboarding-request-pending.destination}") String onboardingRequestQueueName,
            ServiceBusAdministrationClient adminClient) {
        this.connectionString = connectionString;
        this.onboardingRequestQueueName = onboardingRequestQueueName;
        this.adminClient = adminClient;
    }

    @Override
    public ServiceBusReceiverClient getOnboardingRequestReceiverClient() {
        return getReceiverClient(onboardingRequestQueueName);
    }

    @Override
    public ServiceBusReceiverClient getReceiverClient(String queueName){
        return new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .receiver()
                .queueName(queueName)
                .disableAutoComplete()
                .buildClient();
    }

    @Override
    public int countMessageInOnboardingRequestQueue(){
        return countMessageInQueue(onboardingRequestQueueName);
    }

    @Override
    public int countMessageInQueue(String queueName){
        QueueRuntimeProperties queueRuntimeProperties = adminClient.getQueueRuntimeProperties(queueName);
        return  queueRuntimeProperties.getActiveMessageCount() + queueRuntimeProperties.getScheduledMessageCount();
    }
}
