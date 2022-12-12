package it.gov.pagopa.ranking.service.initiative.ranking.retrieve;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.azure.messaging.servicebus.administration.models.QueueRuntimeProperties;
import it.gov.pagopa.ranking.connector.azure.servicebus.AzureServiceBusClient;
import it.gov.pagopa.ranking.dto.initiative.OnboardingRequestPendingDTO;
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
    private final AzureServiceBusClient azureServiceBusClient;

    public OnboardingRankingRuleEndedRetrieverServiceImpl(InitiativeConfigService initiativeConfigService,
                                                          @Value("${app.ranking-build-file.retrieve-initiative.day-before}") long beforeDays,
                                                          AzureServiceBusClient azureServiceBusClient) {
        this.initiativeConfigService = initiativeConfigService;
        this.beforeDays = beforeDays;
        this.azureServiceBusClient = azureServiceBusClient;
    }

    @Override
    public List<InitiativeConfig> retrieve(){
        LocalDate now = LocalDate.now();
        return initiativeConfigService.findByRankingStatusRankingEndDateBetween(RankingStatus.WAITING_END, now.minusDays(beforeDays+1), now)
                .stream().filter(i -> this.checkPendingOnboardingRequests(i.getInitiativeId())).toList();
    }

    private boolean checkPendingOnboardingRequests(String initiativeId) {
        log.info("[INITIATIVE_RETRIEVE_CHECK_PENDING_ONBOARDING] Start check if there are any pending onboarding request for ended initiative: {}", initiativeId);
        int messageInQueue = azureServiceBusClient.countMessageInOnboardingRequestQueue();
        try (ServiceBusReceiverClient receiverClient = azureServiceBusClient.getOnboardingRequestReceiverClient()){
            ServiceBusReceivedMessage serviceBusReceivedMessage;
            for(int count=1; (serviceBusReceivedMessage=receiverClient.peekMessage()) != null && count<=messageInQueue; count++){
                OnboardingRequestPendingDTO body = serviceBusReceivedMessage.getBody().toObject(OnboardingRequestPendingDTO.class);
                if(body.getInitiativeId() != null && body.getInitiativeId().equals(initiativeId)){
                    log.info("[INITIATIVE_RETRIEVE_CHECK_PENDING_ONBOARDING] Found at least one user ({}) having a pending onboarding request onto initiative {}", body.getUserId(), body.getInitiativeId());
                    return false;
                }
            }
        }
        return true;
    }
}
