package it.gov.pagopa.ranking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import it.gov.pagopa.common.kafka.KafkaTestUtilitiesService;
import it.gov.pagopa.common.mongo.MongoTestUtilitiesService;
import it.gov.pagopa.common.utils.TestIntegrationUtils;
import it.gov.pagopa.ranking.connector.azure.servicebus.AzureServiceBusClient;
import it.gov.pagopa.ranking.connector.azure.storage.InitiativeRankingBlobClient;
import it.gov.pagopa.ranking.connector.rest.pdv.PdvErrorDecoderSpy;
import it.gov.pagopa.common.stream.StreamsHealthIndicator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.util.Pair;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import jakarta.annotation.PostConstruct;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

@SpringBootTest
@EmbeddedKafka(topics = {
        "${spring.cloud.stream.bindings.onboardingRankingRequestsConsumer-in-0.destination}",
        "${spring.cloud.stream.bindings.initiativeRankingConsumer-in-0.destination}",
        "${spring.cloud.stream.bindings.errors-out-0.destination}",
        "${spring.cloud.stream.bindings.evaluationOnboardingRanking-out-0.destination}"
}, controlledShutdown = true)
@TestPropertySource(
        properties = {
                //region common feature disabled
                "logging.level.it.gov.pagopa.ranking.service.RankingErrorNotifierServiceImpl=WARN",
                "app.ranking-build-file.retrieve-initiative.schedule=-",
                //endregion

                //region kafka brokers
                "logging.level.org.apache.zookeeper=WARN",
                "logging.level.org.apache.kafka=WARN",
                "logging.level.kafka=WARN",
                "logging.level.state.change.logger=WARN",
                "spring.cloud.stream.kafka.binder.configuration.security.protocol=PLAINTEXT",
                "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.cloud.stream.kafka.binder.zkNodes=${spring.embedded.zookeeper.connect}",
                "spring.cloud.stream.binders.kafka-onboarding-ranking-requests.environment.spring.cloud.stream.kafka.binder.brokers=${spring.embedded.kafka.brokers}",
                "spring.cloud.stream.binders.kafka-initiative-ranking.environment.spring.cloud.stream.kafka.binder.brokers=${spring.embedded.kafka.brokers}",
                "spring.cloud.stream.binders.kafka-errors.environment.spring.cloud.stream.kafka.binder.brokers=${spring.embedded.kafka.brokers}",
                "spring.cloud.stream.binders.kafka-evaluation-onboarding-ranking-outcome.environment.spring.cloud.stream.kafka.binder.brokers=${spring.embedded.kafka.brokers}",
                //endregion

                //region service bus mock
                "app.service-bus.namespace.string-connection=Endpoint=sb://ServiceBusEndpoint;SharedAccessKeyName=sharedAccessKeyName;SharedAccessKey=sharedAccessKey",
                "app.service-bus.onboarding-request-pending.string-connection=Endpoint=sb://ServiceBusEndpoint;SharedAccessKeyName=sharedAccessKeyName;SharedAccessKey=sharedAccessKey;EntityPath=entityPath",
                //endregion

                //region mongodb
                "logging.level.org.mongodb.driver=WARN",
                "logging.level.de.flapdoodle.embed.mongo.spring.autoconfigure=WARN",
                "de.flapdoodle.mongodb.embedded.version=4.0.21",
                //endregion

                //region wiremock
                "logging.level.WireMock=OFF",
                "app.pdv.base-url=http://localhost:${wiremock.server.port}",
                "app.pdv.headers.x-api-key=x_api_key",
                "feign.client.config.pdv.errorDecoder=it.gov.pagopa.ranking.connector.rest.pdv.PdvErrorDecoderSpy"
                //endregion
        })
@AutoConfigureDataMongo
@AutoConfigureMockMvc
@AutoConfigureWireMock(stubs = "classpath:/stub/pdv", port = 0)
public abstract class BaseIntegrationTest {

    @Autowired
    protected KafkaTestUtilitiesService kafkaTestUtilitiesService;
    @Autowired
    protected MongoTestUtilitiesService mongoTestUtilitiesService;

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    protected StreamsHealthIndicator streamsHealthIndicator;

    @MockBean(answer = Answers.RETURNS_MOCKS)
    private AzureServiceBusClient azureServiceBusClientMock;

    @MockBean
    private InitiativeRankingBlobClient initiativeRankingBlobClientMock;

    @Autowired
    protected ObjectMapper objectMapper;

    @Value("${spring.cloud.stream.bindings.onboardingRankingRequestsConsumer-in-0.destination}")
    protected String topicOnboardingRankingRequest;
    @Value("${spring.cloud.stream.bindings.initiativeRankingConsumer-in-0.destination}")
    protected String topicInitiativeRanking;
    @Value("${spring.cloud.stream.bindings.errors-out-0.destination}")
    protected String topicErrors;
    @Value("${spring.cloud.stream.bindings.evaluationOnboardingRanking-out-0.destination}")
    protected String topicEvaluationOnboardingRankingOutcome;

    @Value("${spring.cloud.stream.bindings.onboardingRankingRequestsConsumer-in-0.group}")
    protected String groupIdOnboardingRankingRequest;
    @Value("${spring.cloud.stream.bindings.initiativeRankingConsumer-in-0.group}")
    protected String groupIdInitiativeRanking;

    @BeforeAll
    public static void unregisterPreviouslyKafkaServers() throws MalformedObjectNameException, MBeanRegistrationException, InstanceNotFoundException {
        TestIntegrationUtils.setDefaultTimeZoneAndUnregisterCommonMBean();
    }

    @PostConstruct
    public void logEmbeddedServerConfig() {
        System.out.printf("""
                        ************************
                        Embedded mongo: %s
                        Embedded kafka: %s
                        Wiremock HTTP: http://localhost:%s
                        Wiremock HTTPS: %s
                        ************************
                        """,
                mongoTestUtilitiesService.getMongoUrl(),
                kafkaTestUtilitiesService.getKafkaUrls(),
                wireMockServer.getOptions().portNumber(),
                wireMockServer.baseUrl());
    }

    @BeforeEach
    void initMocks() {
        Mockito.lenient().when(azureServiceBusClientMock.countMessageInOnboardingRequestQueue()).thenReturn(0);
        Mockito.lenient().when(azureServiceBusClientMock.getOnboardingRequestReceiverClient().peekMessage()).thenReturn(null);

        Mockito.lenient()
                .doAnswer(i -> {
                    Path uploadingFile = i.getArgument(0);
                    Path destination = uploadingFile.getParent().resolve(uploadingFile.getFileName().toString().replaceAll("\\.([^.]+$)", ".uploaded.$1"));
                    try {
                        Files.copy(uploadingFile,
                                destination,
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException("Something gone wrong simulating upload of test file %s into %s".formatted(uploadingFile, destination), e);
                    }
                    return null;
                })
                .when(initiativeRankingBlobClientMock)
                .uploadFile(Mockito.<Path>any(), Mockito.any(), Mockito.any());

        // reset counter of Feign retries
        PdvErrorDecoderSpy.resetCounter();
    }

    @Test
    void testHealthIndicator(){
        Health health = streamsHealthIndicator.health();
        Assertions.assertEquals(Status.UP, health.getStatus());
    }

    protected final Pattern errorUseCaseIdPatternMatch = getErrorUseCaseIdPatternMatch();

    protected Pattern getErrorUseCaseIdPatternMatch() {
        return Pattern.compile("\"initiativeId\":\"initiativeId_([0-9]+)_?[^\"]*\"");
    }
    protected void checkErrorsPublished(int expectedErrorMessagesNumber, long maxWaitingMs, List<Pair<Supplier<String>, java.util.function.Consumer<ConsumerRecord<String, String>>>> errorUseCases) {
        kafkaTestUtilitiesService.checkErrorsPublished(topicErrors, errorUseCaseIdPatternMatch, expectedErrorMessagesNumber, maxWaitingMs, errorUseCases);
    }

    protected void checkErrorMessageHeaders(String srcTopic,String group, ConsumerRecord<String, String> errorMessage, String errorDescription, String expectedPayload, String expectedKey) {
        kafkaTestUtilitiesService.checkErrorMessageHeaders(srcTopic, group, errorMessage, errorDescription, expectedPayload, expectedKey, this::normalizePayload);
    }

    protected String normalizePayload(String payload){
        return payload;
    }
}
