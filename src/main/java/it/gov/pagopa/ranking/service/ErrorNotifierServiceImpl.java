package it.gov.pagopa.ranking.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class ErrorNotifierServiceImpl implements ErrorNotifierService {

    public static final String ERROR_MSG_HEADER_APPLICATION_NAME = "applicationName";
    public static final String ERROR_MSG_HEADER_GROUP = "group";
    public static final String ERROR_MSG_HEADER_SRC_TYPE = "srcType";
    public static final String ERROR_MSG_HEADER_SRC_SERVER = "srcServer";
    public static final String ERROR_MSG_HEADER_SRC_TOPIC = "srcTopic";
    public static final String ERROR_MSG_HEADER_DESCRIPTION = "description";
    public static final String ERROR_MSG_HEADER_RETRYABLE = "retryable";
    public static final String ERROR_MSG_HEADER_STACKTRACE = "stacktrace";

    private final StreamBridge streamBridge;
    private final String applicationName;

    private final String onboardingRankingRequestMessagingServiceType;
    private final String onboardingRankingRequestServer;
    private final String onboardingRankingRequestTopic;
    private final String onboardingRankingRequestGroup;

    private final String initiativeRankingMessagingServiceType;
    private final String initiativeRankingServer;
    private final String initiativeRankingTopic;
    private final String initiativeRankingdGroup;


    @SuppressWarnings("squid:S00107") // suppressing too many parameters constructor alert
    public ErrorNotifierServiceImpl(StreamBridge streamBridge,
                                    @Value("${spring.application.name}") String applicationName,

                                    @Value("${spring.cloud.stream.binders.kafka-onboarding-ranking-requests.type}") String onboardingRankingRequestMessagingServiceType,
                                    @Value("${spring.cloud.stream.binders.kafka-onboarding-ranking-requests.environment.spring.cloud.stream.kafka.binder.brokers}") String onboardingRankingRequestServer,
                                    @Value("${spring.cloud.stream.bindings.onboardingRankingRequestsConsumer-in-0.destination}") String onboardingRankingRequestTopic,
                                    @Value("${spring.cloud.stream.bindings.onboardingRankingRequestsConsumer-in-0.group}") String onboardingRankingRequestGroup,

                                    @Value("${spring.cloud.stream.binders.kafka-initiative-ranking.type}") String initiativeBuildServiceType,
                                    @Value("${spring.cloud.stream.binders.kafka-initiative-ranking.environment.spring.cloud.stream.kafka.binder.brokers}") String initiativeRankingServer,
                                    @Value("${spring.cloud.stream.bindings.initiativeRankingConsumer-in-0.destination}") String initiativeRankingTopic,
                                    @Value("${spring.cloud.stream.bindings.initiativeRankingConsumer-in-0.group}") String initiativeRankingdGroup){
        this.streamBridge = streamBridge;
        this.applicationName = applicationName;

        this.onboardingRankingRequestMessagingServiceType = onboardingRankingRequestMessagingServiceType;
        this.onboardingRankingRequestServer = onboardingRankingRequestServer;
        this.onboardingRankingRequestTopic = onboardingRankingRequestTopic;
        this.onboardingRankingRequestGroup = onboardingRankingRequestGroup;
        this.initiativeRankingMessagingServiceType = initiativeBuildServiceType;
        this.initiativeRankingServer = initiativeRankingServer;
        this.initiativeRankingTopic = initiativeRankingTopic;
        this.initiativeRankingdGroup = initiativeRankingdGroup;
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
    public void notify(String srcType, String srcServer, String srcTopic, String group, Message<?> message, String description, boolean retryable, boolean resendApplication, Throwable exception) {
        log.info("[ERROR_NOTIFIER] notifying error: {}", description, exception);
        final MessageBuilder<?> errorMessage = MessageBuilder.fromMessage(message)
                .setHeader(ERROR_MSG_HEADER_SRC_TYPE, srcType)
                .setHeader(ERROR_MSG_HEADER_SRC_SERVER, srcServer)
                .setHeader(ERROR_MSG_HEADER_SRC_TOPIC, srcTopic)
                .setHeader(ERROR_MSG_HEADER_DESCRIPTION, description)
                .setHeader(ERROR_MSG_HEADER_RETRYABLE, retryable)
                .setHeader(ERROR_MSG_HEADER_STACKTRACE, ExceptionUtils.getStackTrace(exception));

        addExceptionInfo(errorMessage, "rootCause", ExceptionUtils.getRootCause(exception));
        addExceptionInfo(errorMessage, "cause", exception.getCause());

        byte[] receivedKey = message.getHeaders().get(KafkaHeaders.RECEIVED_MESSAGE_KEY, byte[].class);
        if(receivedKey!=null){
            errorMessage.setHeader(KafkaHeaders.MESSAGE_KEY, new String(receivedKey, StandardCharsets.UTF_8));
        }
        if(resendApplication){
            errorMessage.setHeader(ERROR_MSG_HEADER_APPLICATION_NAME, applicationName);
            errorMessage.setHeader(ERROR_MSG_HEADER_GROUP, group);
        }

        if (!streamBridge.send("errors-out-0", errorMessage.build())) {
            log.error("[ERROR_NOTIFIER] Something gone wrong while notifying error");
        }
    }

    private void addExceptionInfo(MessageBuilder<?> errorMessage, String exceptionHeaderPrefix, Throwable rootCause) {
        errorMessage
                .setHeader("%sClass".formatted(exceptionHeaderPrefix), rootCause != null ? rootCause.getClass().getName() : null)
                .setHeader("%sMessage".formatted(exceptionHeaderPrefix), rootCause != null ? rootCause.getMessage() : null);
    }
}
