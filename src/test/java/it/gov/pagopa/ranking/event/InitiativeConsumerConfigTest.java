package it.gov.pagopa.ranking.event;

import com.mongodb.MongoException;
import it.gov.pagopa.ranking.BaseIntegrationTest;
import it.gov.pagopa.ranking.dto.initiative.InitiativeBuildDTO;
import it.gov.pagopa.ranking.dto.initiative.InitiativeGeneralDTO;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.Order;
import it.gov.pagopa.ranking.model.RankingStatus;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;


@TestPropertySource(properties = {
        "logging.level.it.gov.pagopa.ranking.service.initiative.InitiativePersistenceMediatorImpl=OFF",
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


        waitForInitiativeStored((validInitiative/useCases.size()*5));
        long timeEnd=System.currentTimeMillis();

        checkResponse();
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
                .mapToObj(this::mockInstance)
                .map(TestUtils::jsonSerializer)
                .toList();
    }

    private long waitForInitiativeStored(int N) {
        long[] countSaved={0};
        waitFor(()->(countSaved[0]=initiativeConfigRepository.count()) >= N, ()->"Expected %d saved ranking initiative, read %d".formatted(N, countSaved[0]), 60, 1000);
        return countSaved[0];
    }

    private InitiativeBuildDTO mockInstance(int bias) {
        return useCases.get(bias % useCases.size()).getFirst().apply(bias);
    }

    //region valid useCases
    private final List<Pair<Function<Integer, InitiativeBuildDTO>, Consumer<InitiativeConfig>>> useCases = List.of(
            //useCase 0: initiative not present into DB
            Pair.of(
                    Initiative2BuildDTOFaker::mockInstance,
                    initiativeConfig -> {
                        InitiativeConfig initiativeConfigRetrieved = initiativeConfigRepository.findById(initiativeConfig.getInitiativeId()).orElse(null);
                        Assertions.assertNotNull(initiativeConfigRetrieved);
                        Assertions.assertEquals(RankingStatus.WAITING_END, initiativeConfigRetrieved.getRankingStatus());
                    }
            ),

            //useCase 1: initiative present into DB with status WAITING_END
            Pair.of(
                    i -> {
                        InitiativeBuildDTO initiativeAlredyInDbStatusWaitingEnding = Initiative2BuildDTOFaker.mockInstance(i);
                        InitiativeConfig initiativeInDb = getInitiativeForDB(i);
                        initiativeInDb.setRankingStatus(RankingStatus.WAITING_END);
                        initiativeConfigRepository.save(initiativeInDb);
                        return  initiativeAlredyInDbStatusWaitingEnding;

                    },
                    initiativeConfig -> {
                        Assertions.assertNotNull(initiativeConfig);
                        checkFields(initiativeConfig, RankingStatus.WAITING_END);
                    }
            ),

            //useCase 2: initiative present into DB with status READY
            Pair.of(
                    i -> {
                        InitiativeBuildDTO initiativeAlredyInDbStatusReady = Initiative2BuildDTOFaker.mockInstance(i);
                        InitiativeConfig initiativeInDb = getInitiativeForDB(i);
                        initiativeInDb.setRankingStatus(RankingStatus.READY);
                        initiativeConfigRepository.save(initiativeInDb);
                        return  initiativeAlredyInDbStatusReady;

                    },
                    initiativeConfig -> assertInitiativeNotChanged(initiativeConfig, RankingStatus.READY)
            ),

            //useCase 3: initiative present into DB with status PUBLISHING
            Pair.of(
                    i -> {
                        InitiativeBuildDTO initiativeAlredyInDbStatusPublishing = Initiative2BuildDTOFaker.mockInstance(i);
                        InitiativeConfig initiativeInDb = getInitiativeForDB(i);
                        initiativeInDb.setRankingStatus(RankingStatus.PUBLISHING);
                        initiativeConfigRepository.save(initiativeInDb);
                        return  initiativeAlredyInDbStatusPublishing;
                    },
                    initiativeConfig -> assertInitiativeNotChanged(initiativeConfig, RankingStatus.PUBLISHING)
            ),

            //useCase 4: initiative present into DB with status COMPLETED
            Pair.of(
                    i -> {
                        InitiativeBuildDTO initiativeAlredyInDbStatusCompleted = Initiative2BuildDTOFaker.mockInstance(i);
                        InitiativeConfig initiativeInDb = getInitiativeForDB(i);
                        initiativeInDb.setRankingStatus(RankingStatus.COMPLETED);
                        initiativeConfigRepository.save(initiativeInDb);
                        return  initiativeAlredyInDbStatusCompleted;
                    },
                    initiativeConfig -> assertInitiativeNotChanged(initiativeConfig, RankingStatus.COMPLETED)
            ),

            //useCase 5: not ranking initiative
            Pair.of(
                    i -> {
                        InitiativeBuildDTO initiativeBuildDTO = Initiative2BuildDTOFaker.mockInstanceBuilder(i)
                                .build();
                        InitiativeGeneralDTO general = initiativeBuildDTO.getGeneral();
                        general.setRankingEnabled(false);
                        initiativeBuildDTO.setGeneral(general);
                        return initiativeBuildDTO;
                    },
                    initiativeConfig -> Assertions.assertTrue("INITIATIVE_NOT_RANKING".contains(initiativeConfig.getInitiativeId()), "Initiative not ranking type")
            )
    );

    private InitiativeConfig getInitiativeForDB(Integer i) {
        LocalDate nowDate = LocalDate.now();
        return InitiativeConfig.builder()
                .initiativeId("initiativeId_%d".formatted(i))
                .initiativeName("old_initiative_name_%d".formatted(i))
                .organizationId("old_organization_id_%d".formatted(i))
                .organizationName("old_organization_name_%d".formatted(i))
                .initiativeStatus("old_initiative_status_%d".formatted(i))
                .rankingStartDate(nowDate.plusMonths(1L))
                .rankingEndDate(nowDate.plusMonths(8L))
                .initiativeEndDate(nowDate.plusMonths(8L))
                .initiativeBudget(BigDecimal.TEN)
                .beneficiaryInitiativeBudget(BigDecimal.ONE)
                .rankingFields(List.of(
                        Order.builder().fieldCode("ISEE").direction(Sort.Direction.ASC).build()
                ))
                .initiativeRewardType("REFUND")
                .build();
    }

    private void checkResponse() {
        initiativeConfigRepository.findAll().forEach(i -> {
            int biasRetrieve = Integer.parseInt(i.getInitiativeId().substring(13));
            if(biasRetrieve >= errorUseCases.size()){
                useCases.get(biasRetrieve % useCases.size()).getSecond().accept(i);
            }else {
                Assertions.assertTrue("NOT_EXIST_INITIATIVE_ID".contains(i.getInitiativeId()), "Invalid initiativeConfig: " + i);
            }
        });
    }

    private void assertInitiativeNotChanged(InitiativeConfig initiativeConfig, RankingStatus rankingStatus){
        Assertions.assertNotNull(initiativeConfig);
        TestUtils.checkNotNullFields(initiativeConfig, "rankingFilePath", "rankingPublishedTimestamp", "rankingGeneratedTimestamp");

        int biasRetrieved = Integer.parseInt(initiativeConfig.getInitiativeId().substring(13));
        InitiativeConfig initiativeExpected = getInitiativeForDB(biasRetrieved);
        initiativeExpected.setRankingStatus(rankingStatus);
        Assertions.assertEquals(initiativeExpected, initiativeConfig);
    }

    private void checkFields(InitiativeConfig initiativeConfig, RankingStatus rankingStatus){
        int biasRetrieve = Integer.parseInt(initiativeConfig.getInitiativeId().substring(13));
        InitiativeBuildDTO initiativeBuildDTO = Initiative2BuildDTOFaker.mockInstance(biasRetrieve);

        TestUtils.checkNotNullFields(initiativeConfig, "rankingFilePath", "rankingPublishedTimestamp", "rankingGeneratedTimestamp");
        Assertions.assertEquals(initiativeBuildDTO.getInitiativeId(), initiativeConfig.getInitiativeId());
        Assertions.assertEquals(initiativeBuildDTO.getInitiativeName(), initiativeConfig.getInitiativeName());
        Assertions.assertEquals(initiativeBuildDTO.getOrganizationId(),initiativeConfig.getOrganizationId());
        Assertions.assertEquals(initiativeBuildDTO.getStatus(), initiativeConfig.getInitiativeStatus());
        Assertions.assertEquals(initiativeBuildDTO.getGeneral().getRankingStartDate(), initiativeConfig.getRankingStartDate());
        Assertions.assertEquals(initiativeBuildDTO.getGeneral().getRankingEndDate(), initiativeConfig.getRankingEndDate());
        Assertions.assertEquals(initiativeBuildDTO.getGeneral().getEndDate(), initiativeConfig.getInitiativeEndDate());
        Assertions.assertEquals(rankingStatus, initiativeConfig.getRankingStatus());
    }

    //endregion


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