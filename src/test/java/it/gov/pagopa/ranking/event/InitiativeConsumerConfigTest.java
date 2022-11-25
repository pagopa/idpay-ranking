package it.gov.pagopa.ranking.event;

import com.mongodb.MongoException;
import it.gov.pagopa.ranking.BaseIntegrationTest;
import it.gov.pagopa.ranking.dto.initiative.InitiativeBuildDTO;
import it.gov.pagopa.ranking.repository.InitiativeConfigRepository;
import it.gov.pagopa.ranking.service.ErrorNotifierServiceImpl;
import it.gov.pagopa.ranking.test.fakers.Initiative2BuildDTOFaker;
import it.gov.pagopa.ranking.utils.TestUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;


@TestPropertySource(properties = {
        "logging.level.it.gov.pagopa.ranking.service.initiative.InitiativePersistenceMediatorImpl=WARN",
        "logging.level.it.gov.pagopa.ranking.service.initiative.InitiativeConfigServiceImpl=WARN",
        "logging.level.it.gov.pagopa.ranking.service.BaseKafkaConsumer=WARN",
})
class InitiativeConsumerConfigTest extends BaseIntegrationTest {

    @SpyBean
    private InitiativeConfigRepository initiativeConfigRepository;

    @AfterEach
    void cleanData(){
        initiativeConfigRepository.deleteAll();
    }

    @Test
    void initiativeConsumer() {
        int validInitiative = 1000;
        int notValidInitiative = errorUseCases.size();
        long maxWaitingMs = 30000;

        List<String> initiativePayloads = new ArrayList<>(buildValidPayloads(errorUseCases.size(), validInitiative / 2));
        initiativePayloads.addAll(IntStream.range(0, notValidInitiative).mapToObj(i -> errorUseCases.get(i).getFirst().get()).toList());
        initiativePayloads.addAll(buildValidPayloads(errorUseCases.size() + (validInitiative / 2), validInitiative / 2));

        long timeStart=System.currentTimeMillis();
        initiativePayloads.forEach(i->publishIntoEmbeddedKafka(topicInitiativeRanking, null, null, i));
        publishIntoEmbeddedKafka(topicInitiativeRanking, List.of(new RecordHeader(ErrorNotifierServiceImpl.ERROR_MSG_HEADER_APPLICATION_NAME, "OTHERAPPNAME".getBytes(StandardCharsets.UTF_8))), null, "OTHERAPPMESSAGE");
        long timePublishingEnd=System.currentTimeMillis();

        long countSaved = waitForInitiativeStored(validInitiative/2);
        long timeEnd=System.currentTimeMillis();

        Assertions.assertEquals(validInitiative/2, countSaved);

        checkErrorsPublished(notValidInitiative, maxWaitingMs, errorUseCases);

        System.out.printf("""
            ************************
            Time spent to send %d (%d + %d + 1) messages (from start): %d millis
            Time spent to assert ranking initiative stored count (from previous check): %d millis
            ************************
            Test Completed in %d millis
            ************************
            """,
                validInitiative + notValidInitiative + 1, // +1 due to other applicationName useCase
                validInitiative,
                notValidInitiative,
                timePublishingEnd-timeStart,
                timeEnd-timePublishingEnd,
                timeEnd-timeStart
        );

    }

    private List<String> buildValidPayloads(int bias, int n) {
        return IntStream.range(bias, bias + n)
                .mapToObj(i -> Initiative2BuildDTOFaker.mockInstanceBuilder(i)
                        .beneficiaryRanking(i%2 == 0)
                        .build())
                .map(TestUtils::jsonSerializer)
                .toList();
    }

    private long waitForInitiativeStored(int N) {
        long[] countSaved={0};
        //noinspection ConstantConditions
        waitFor(()->(countSaved[0]=initiativeConfigRepository.count()) >= N, ()->"Expected %d saved ranking initiative, read %d".formatted(N, countSaved[0]), 60, 1000);
        return countSaved[0];
    }

    //region not valid useCases
    // all use cases configured must have a unique id recognized by the regexp getErrorUseCaseIdPatternMatch
   private final List<Pair<Supplier<String>, Consumer<ConsumerRecord<String, String>>>> errorUseCases = new ArrayList<>();
    {
        String useCaseJsonNotExpected = "{\"initiativeId\":\"initiativeId_0\",unexpectedStructure:0}";
        errorUseCases.add(Pair.of(
                () -> useCaseJsonNotExpected,
                errorMessage -> checkErrorMessageHeaders(errorMessage, "[INITIATIVE_RANKING] Unexpected JSON", useCaseJsonNotExpected)
        ));

        String jsonNotValid = "{\"initiativeId\":\"initiativeId_1\",invalidJson";
        errorUseCases.add(Pair.of(
                () -> jsonNotValid,
                errorMessage -> checkErrorMessageHeaders(errorMessage, "[INITIATIVE_RANKING] Unexpected JSON", jsonNotValid)
        ));

        InitiativeBuildDTO initiativeReqestMongoException= Initiative2BuildDTOFaker.mockInstance(errorUseCases.size());
        errorUseCases.add(Pair.of(
                () -> {
                    Mockito.doThrow(MongoException.class).when(initiativeConfigRepository).save(Mockito.argThat(i -> i.getInitiativeId().equals(initiativeReqestMongoException.getInitiativeId())));
                    return TestUtils.jsonSerializer(initiativeReqestMongoException);
                },
                errorMessage -> checkErrorMessageHeaders(errorMessage,"[INITIATIVE_RANKING] An error occurred handling initiative ranking build", TestUtils.jsonSerializer(initiativeReqestMongoException))
        ));
    }
    private void checkErrorMessageHeaders(ConsumerRecord<String, String> errorMessage, String errorDescription, String expectedPayload) {
        checkErrorMessageHeaders(topicInitiativeRanking, groupIdInitiativeRanking, errorMessage, errorDescription, expectedPayload,true,true);
    }
    //enregion
}