package it.gov.pagopa.ranking.service.initiative.ranking.retrieve;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.azure.messaging.servicebus.administration.models.QueueRuntimeProperties;
import it.gov.pagopa.ranking.dto.initiative.filter.OnboardingRequestPendingDTO;
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
public class OnboardingRankingRuleEndedRetrieverServiceImpl implements OnboardingRankingRuleEndedRetrieverService {
    private final InitiativeConfigService initiativeConfigService;
    private final long beforeDays;
    private final String connectionString;
    private final String queueName;
    private final ServiceBusAdministrationClient adminClient;

    public OnboardingRankingRuleEndedRetrieverServiceImpl(InitiativeConfigService initiativeConfigService,
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
        LocalDate now = LocalDate.now();
        return initiativeConfigService.findByRankingStatusRankingEndDateBetween(RankingStatus.WAITING_END, now.minusDays(beforeDays+1), now)
                .stream().filter(i -> this.checkPendingOnboardingRequests(i.getInitiativeId())).toList();
    }

    private boolean checkPendingOnboardingRequests(String initiativeId) {
        log.info("[INITIATIVE_RETRIEVE_CHECK_PENDING_ONBOARDING] Start check if there any pending onboarding request for initiative: {}", initiativeId);
        int messageInQueue = countMessageInQueue();
        try (ServiceBusReceiverClient receiverClient = getReceiverClient()){
            ServiceBusReceivedMessage serviceBusReceivedMessage;
            for(int count=1; (serviceBusReceivedMessage=receiverClient.peekMessage()) != null && count<=messageInQueue; count++){
                OnboardingRequestPendingDTO body = serviceBusReceivedMessage.getBody().toObject(OnboardingRequestPendingDTO.class);
                if(body.getInitiativeId() != null && body.getInitiativeId().equals(initiativeId)){
                    log.info("[INITIATIVE_RETRIEVE_CHECK_PENDING_ONBOARDING] The user {} waiting for onboarding in initiative {}", body.getUserId(), body.getInitiativeId());
                    return false;
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
