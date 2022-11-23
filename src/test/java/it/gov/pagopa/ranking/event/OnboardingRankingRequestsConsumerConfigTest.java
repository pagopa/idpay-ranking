package it.gov.pagopa.ranking.event;

import com.mongodb.MongoException;
import it.gov.pagopa.ranking.BaseIntegrationTest;
import it.gov.pagopa.ranking.dto.OnboardingRankingRequestDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequestsDTO2ModelMapper;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import it.gov.pagopa.ranking.service.ErrorNotifierServiceImpl;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsDTOFaker;
import it.gov.pagopa.ranking.utils.TestUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.util.Pair;
import org.springframework.test.context.TestPropertySource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@TestPropertySource(properties = {
        "logging.level.it.gov.pagopa.ranking.service.OnboardingRankingRequestsMediatorImpl=WARN",
        "logging.level.it.gov.pagopa.ranking.service.OnboardingRankingRequestsServiceImpl=WARN",
        "logging.level.it.gov.pagopa.ranking.service.ErrorNotifierServiceImpl=WARN",
        "logging.level.it.gov.pagopa.ranking.service.BaseKafkaConsumer=WARN",
})
class OnboardingRankingRequestsConsumerConfigTest extends BaseIntegrationTest {

    @SpyBean
    private OnboardingRankingRequestsRepository onboardingRankingRequestsRepository;

    @AfterEach
    void cleanData(){onboardingRankingRequestsRepository.deleteAll();}

    @Test
    void onboardingRankingRequestsConsumer() {
        int validOnboardings = 1000;
        int notValidOnboardings = errorUseCases.size();
        long maxWaitingMs = 30000;

        List<String> onboardingPayloads = new ArrayList<>(buildValidPayloads(errorUseCases.size(), validOnboardings / 2));
        onboardingPayloads.addAll(IntStream.range(0, notValidOnboardings).mapToObj(i -> errorUseCases.get(i).getFirst().get()).toList());
        onboardingPayloads.addAll(buildValidPayloads(errorUseCases.size() + (validOnboardings / 2), validOnboardings / 2));

        long timeStart=System.currentTimeMillis();
        onboardingPayloads.forEach(i->publishIntoEmbeddedKafka(topicOnboardingRankingRequest, null, null, i));
        publishIntoEmbeddedKafka(topicOnboardingRankingRequest, List.of(new RecordHeader(ErrorNotifierServiceImpl.ERROR_MSG_HEADER_APPLICATION_NAME, "OTHERAPPNAME".getBytes(StandardCharsets.UTF_8))), null, "OTHERAPPMESSAGE");
        long timePublishingEnd=System.currentTimeMillis();

        long countSaved = waitForOnboardingStored(validOnboardings);
        long timeEnd=System.currentTimeMillis();

        Assertions.assertEquals(validOnboardings, countSaved);

        checkErrorsPublished(notValidOnboardings, maxWaitingMs, errorUseCases);

        System.out.printf("""
            ************************
            Time spent to send %d (%d + %d + 1) messages (from start): %d millis
            Time spent to assert trx stored count (from previous check): %d millis
            ************************
            Test Completed in %d millis
            ************************
            """,
                validOnboardings + notValidOnboardings + 1, // +1 due to other applicationName useCase
                validOnboardings,
                notValidOnboardings,
                timePublishingEnd-timeStart,
                timeEnd-timePublishingEnd,
                timeEnd-timeStart
        );

        long timeCommitCheckStart = System.currentTimeMillis();
        final Map<TopicPartition, OffsetAndMetadata> srcCommitOffsets = checkCommittedOffsets(topicOnboardingRankingRequest, groupIdOnboardingRankingRequest,onboardingPayloads.size()+1); // +1 due to other applicationName useCase
        long timeCommitCheckEnd = System.currentTimeMillis();
        System.out.printf("""
                        ************************
                        Time occurred to check committed offset: %d millis
                        ************************
                        Source Topic Committed Offsets: %s
                        ************************
                        """,
                timeCommitCheckEnd - timeCommitCheckStart,
                srcCommitOffsets
        );
    }

    private List<String> buildValidPayloads(int bias, int n) {
        return IntStream.range(bias, bias + n)
                .mapToObj(OnboardingRankingRequestsDTOFaker::mockInstance)
                .map(TestUtils::jsonSerializer)
                .toList();
    }

    private long waitForOnboardingStored(int N) {
        long[] countSaved={0};
        //noinspection ConstantConditions
        waitFor(()->(countSaved[0]=onboardingRankingRequestsRepository.count()) >= N, ()->"Expected %d saved rules, read %d".formatted(N, countSaved[0]), 60, 1000);
        return countSaved[0];
    }

    //region not valid useCases
    // all use cases configured must have a unique id recognized by the regexp getErrorUseCaseIdPatternMatch
    protected Pattern getErrorUseCaseIdPatternMatch() {
        return Pattern.compile("\"userId\":\"userId_([0-9]+)\"");
    }
    private final List<Pair<Supplier<String>, Consumer<ConsumerRecord<String, String>>>> errorUseCases = new ArrayList<>();
    {
        String useCaseJsonNotExpected = "{\"userId\":\"userId_0\",unexpectedStructure:0}";
        errorUseCases.add(Pair.of(
                () -> useCaseJsonNotExpected,
                errorMessage -> checkErrorMessageHeaders(errorMessage, "[ONBOARDING_RANKING_REQUEST] Unexpected JSON", useCaseJsonNotExpected)
        ));

        String jsonNotValid = "{\"userId\":\"userId_1\",invalidJson";
        errorUseCases.add(Pair.of(
                () -> jsonNotValid,
                errorMessage -> checkErrorMessageHeaders(errorMessage, "[ONBOARDING_RANKING_REQUEST] Unexpected JSON", jsonNotValid)
        ));

        OnboardingRankingRequestDTO onboardingMongoException= OnboardingRankingRequestsDTOFaker.mockInstance(errorUseCases.size());
        String onboardingExceptionId = OnboardingRankingRequestsDTO2ModelMapper.buildId(onboardingMongoException);
        errorUseCases.add(Pair.of(
                () -> {
                    Mockito.doThrow(MongoException.class).when(onboardingRankingRequestsRepository).save(Mockito.argThat(i -> i.getId().equals(onboardingExceptionId)));
                    return TestUtils.jsonSerializer(onboardingMongoException);
                    },
                errorMessage -> checkErrorMessageHeaders(errorMessage,"[ONBOARDING_RANKING_REQUEST] An error occurred handling onboarding ranking request", TestUtils.jsonSerializer(onboardingMongoException))
        ));
    }

    private void checkErrorMessageHeaders(ConsumerRecord<String, String> errorMessage, String errorDescription, String expectedPayload) {
        checkErrorMessageHeaders(topicOnboardingRankingRequest, groupIdOnboardingRankingRequest, errorMessage, errorDescription, expectedPayload,true,true);
    }
    //endregion
}