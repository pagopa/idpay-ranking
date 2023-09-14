package it.gov.pagopa.ranking.service;

import it.gov.pagopa.common.kafka.service.ErrorNotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RankingErrorNotifierServiceImpl implements RankingErrorNotifierService {

    private final ErrorNotifierService errorNotifierService;

    private final String onboardingRankingRequestMessagingServiceType;
    private final String onboardingRankingRequestServer;
    private final String onboardingRankingRequestTopic;
    private final String onboardingRankingRequestGroup;

    private final String initiativeRankingMessagingServiceType;
    private final String initiativeRankingServer;
    private final String initiativeRankingTopic;
    private final String initiativeRankingdGroup;

    private final String rankingOnboardingOutcomeServiceType;
    private final String rankingOnboardingOutcomeServer;
    private final String rankingOnboardingOutcomeTopic;


    @SuppressWarnings("squid:S00107") // suppressing too many parameters constructor alert
    public RankingErrorNotifierServiceImpl(ErrorNotifierService errorNotifierService,

                                           @Value("${spring.cloud.stream.binders.kafka-onboarding-ranking-requests.type}") String onboardingRankingRequestMessagingServiceType,
                                           @Value("${spring.cloud.stream.binders.kafka-onboarding-ranking-requests.environment.spring.cloud.stream.kafka.binder.brokers}") String onboardingRankingRequestServer,
                                           @Value("${spring.cloud.stream.bindings.onboardingRankingRequestsConsumer-in-0.destination}") String onboardingRankingRequestTopic,
                                           @Value("${spring.cloud.stream.bindings.onboardingRankingRequestsConsumer-in-0.group}") String onboardingRankingRequestGroup,

                                           @Value("${spring.cloud.stream.binders.kafka-initiative-ranking.type}") String initiativeBuildServiceType,
                                           @Value("${spring.cloud.stream.binders.kafka-initiative-ranking.environment.spring.cloud.stream.kafka.binder.brokers}") String initiativeRankingServer,
                                           @Value("${spring.cloud.stream.bindings.initiativeRankingConsumer-in-0.destination}") String initiativeRankingTopic,
                                           @Value("${spring.cloud.stream.bindings.initiativeRankingConsumer-in-0.group}") String initiativeRankingdGroup,

                                           @Value("${spring.cloud.stream.binders.kafka-evaluation-onboarding-ranking-outcome.type}") String rankingOnboardingOutcomeServiceType,
                                           @Value("${spring.cloud.stream.binders.kafka-evaluation-onboarding-ranking-outcome.environment.spring.cloud.stream.kafka.binder.brokers}") String rankingOnboardingOutcomeServer,
                                           @Value("${spring.cloud.stream.bindings.evaluationOnboardingRanking-out-0.destination}") String rankingOnboardingOutcomeTopic){
        this.errorNotifierService = errorNotifierService;

        this.onboardingRankingRequestMessagingServiceType = onboardingRankingRequestMessagingServiceType;
        this.onboardingRankingRequestServer = onboardingRankingRequestServer;
        this.onboardingRankingRequestTopic = onboardingRankingRequestTopic;
        this.onboardingRankingRequestGroup = onboardingRankingRequestGroup;
        this.initiativeRankingMessagingServiceType = initiativeBuildServiceType;
        this.initiativeRankingServer = initiativeRankingServer;
        this.initiativeRankingTopic = initiativeRankingTopic;
        this.initiativeRankingdGroup = initiativeRankingdGroup;
        this.rankingOnboardingOutcomeServiceType = rankingOnboardingOutcomeServiceType;
        this.rankingOnboardingOutcomeServer = rankingOnboardingOutcomeServer;
        this.rankingOnboardingOutcomeTopic = rankingOnboardingOutcomeTopic;
    }

    @Override
    public void notifyOnboardingRankingRequest(Message<?> message, String description, boolean retryable, Throwable exception) {
        notify(onboardingRankingRequestMessagingServiceType, onboardingRankingRequestServer, onboardingRankingRequestTopic, onboardingRankingRequestGroup, message, description, retryable, true, exception);
    }

    @Override
    public void notifyInitiativeBuild(Message<?> message, String description, boolean retryable, Throwable exception) {
        notify(initiativeRankingMessagingServiceType, initiativeRankingServer, initiativeRankingTopic, initiativeRankingdGroup, message, description, retryable, true, exception);
    }

    @Override
    public void notifyRankingOnboardingOutcome(Message<?> message, String description, boolean retryable, Throwable exception) {
        notify(rankingOnboardingOutcomeServiceType, rankingOnboardingOutcomeServer, rankingOnboardingOutcomeTopic,null, message, description, retryable, false, exception);

    }

    @Override
    public void notify(String srcType, String srcServer, String srcTopic, String group, Message<?> message, String description, boolean retryable,boolean resendApplication, Throwable exception) {
        errorNotifierService.notify(srcType, srcServer, srcTopic, group, message, description, retryable,resendApplication, exception);
    }
}
