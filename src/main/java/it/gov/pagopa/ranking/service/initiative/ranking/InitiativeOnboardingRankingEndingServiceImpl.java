package it.gov.pagopa.ranking.service.initiative.ranking;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import it.gov.pagopa.ranking.dto.OnboardingRankingRequestDTO;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.service.initiative.InitiativeConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InitiativeOnboardingRankingEndingServiceImpl implements InitiativeOnboardingRankingEndingService {
    private final InitiativeConfigService initiativeConfigService;
    private final long beforeDays;
    private final String connectionString;
    private final String queueName;

    public InitiativeOnboardingRankingEndingServiceImpl(InitiativeConfigService initiativeConfigService,
                                                        @Value("${app.ranking-build-file.retrieve-initiative.day-before}") long beforeDays,
                                                        @Value("${app.service-bus.onboarding-request-pending.string-connection}") String connectionString,
                                                        @Value("${app.service-bus.onboarding-request-pending.destination}") String queueName) {
        this.initiativeConfigService = initiativeConfigService;
        this.beforeDays = beforeDays;
        this.connectionString = connectionString;
        this.queueName = queueName;
    }

    @Override
    public List<InitiativeConfig> retrieve(){
        return initiativeConfigService.findByRankingEndDateBeforeAndRankingStatus(LocalDate.now().minusDays(beforeDays), RankingStatus.WAITING_END)
                .stream().filter(this::isElaborateAllMessages).toList();
    }

    private boolean isElaborateAllMessages(InitiativeConfig initiativeConfig){
        ServiceBusReceiverClient consumer = getConsumer();
        ServiceBusReceivedMessage serviceBusReceivedMessage = consumer.peekMessage();
        while(serviceBusReceivedMessage != null){
            OnboardingRankingRequestDTO body = serviceBusReceivedMessage.getBody().toObject(OnboardingRankingRequestDTO.class);
            if(body.getInitiativeId().equals(initiativeConfig.getInitiativeId())){
                consumer.close();
                return false;
            } else{
                serviceBusReceivedMessage = consumer.peekMessage();
            }
        }
        consumer.close();
        return true;
    }

    private ServiceBusReceiverClient getConsumer(){
        return new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .receiver()
                .queueName(queueName)
                .disableAutoComplete()
                .buildClient();
    }
}
