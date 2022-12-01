package it.gov.pagopa.ranking.service.initiative.ranking;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.azure.messaging.servicebus.administration.models.QueueRuntimeProperties;
import it.gov.pagopa.ranking.dto.initiative.filter.OnboardingRequestDTO;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.service.initiative.InitiativeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class InitiativeOnboardingRankingEndingServiceImpl implements InitiativeOnboardingRankingEndingService {
    private final InitiativeConfigService initiativeConfigService;
    private final long beforeDays;
    private final String connectionString;
    private final String queueName;
    private final ServiceBusAdministrationClient adminClient;

    public InitiativeOnboardingRankingEndingServiceImpl(InitiativeConfigService initiativeConfigService,
                                                        @Value("${app.ranking-build-file.retrieve-initiative.day-before}") long beforeDays,
                                                        @Value("${app.service-bus.onboarding-request-pending.string-connection}") String connectionString,
                                                        @Value("${app.service-bus.onboarding-request-pending.destination}") String queueName,
                                                        ServiceBusAdministrationClient adminClient) {
        this.initiativeConfigService = initiativeConfigService;
        this.beforeDays = beforeDays;
        this.connectionString = connectionString;
        this.queueName = queueName;
        this.adminClient = adminClient;
    }

    @Override
    public List<InitiativeConfig> retrieve(){
        return initiativeConfigService.findByRankingEndDateBeforeAndRankingStatus(LocalDate.now().minusDays(beforeDays), RankingStatus.WAITING_END)
                .stream().filter(this::isElaborateAllMessages).toList();
    }

    private boolean isElaborateAllMessages(InitiativeConfig initiativeConfig) {
        int messageInQueue = countMessageInQueue();

        ServiceBusReceiverClient receiverClient = getReceiverClient();
        ServiceBusReceivedMessage serviceBusReceivedMessage = receiverClient.peekMessage();

        int count = 1;
        try (receiverClient){
            while(serviceBusReceivedMessage != null && count<=messageInQueue){
                OnboardingRequestDTO body = serviceBusReceivedMessage.getBody().toObject(OnboardingRequestDTO.class);
                if(body.getInitiativeId() != null && body.getInitiativeId().equals(initiativeConfig.getInitiativeId())){
                    return false;
                } else{
                    serviceBusReceivedMessage = receiverClient.peekMessage();
                    count++;
                }
            }
        }
        return true;
    }

    private ServiceBusReceiverClient getReceiverClient(){
        return new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .receiver()
                .queueName(queueName)
                .disableAutoComplete()
                .buildClient();
    }

    private int countMessageInQueue(){
        QueueRuntimeProperties queueRuntimeProperties = adminClient.getQueueRuntimeProperties(queueName);
        return  queueRuntimeProperties.getActiveMessageCount() + queueRuntimeProperties.getScheduledMessageCount();
    }
}
