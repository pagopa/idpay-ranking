package it.gov.pagopa.common.mongo.repository;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.micrometer.core.instrument.binder.mongodb.MongoMetricsCommandListener;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import it.gov.pagopa.common.mongo.DummySpringRepository;
import it.gov.pagopa.common.mongo.MongoTest;
import it.gov.pagopa.common.mongo.MongoTestUtilitiesService;
import it.gov.pagopa.common.mongo.config.MongoConfig;
import it.gov.pagopa.common.mongo.singleinstance.AutoConfigureSingleInstanceMongodb;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

@MongoTest
class BaseMongoRepositoryIntegrationTest {

    static {
        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.INFO);
    }

    @Autowired
    private DummySpringRepository repository;

    private static final List<String> ID_TEST_ENTITIES = List.of("ID", "ID2");

    @BeforeEach
    void initTestData(){
        ID_TEST_ENTITIES.forEach(this::storeTestData);
    }

    private void storeTestData(String idTestEntity) {
        DummySpringRepository.DummyMongoCollection testData = new DummySpringRepository.DummyMongoCollection();
        testData.setId(idTestEntity);
        repository.save(testData);
    }

    @AfterEach
    void clearTestData(){
        repository.deleteAllById(ID_TEST_ENTITIES);
    }

    @Test
    void testFindById() {
        MongoTestUtilitiesService.startMongoCommandListener();

        DummySpringRepository.DummyMongoCollection result = repository.findById(ID_TEST_ENTITIES.get(0)).orElse(null);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(ID_TEST_ENTITIES.get(0), result.getId());

        Assertions.assertTrue(repository.findById("DUMMYID").isEmpty());

        List<Map.Entry<MongoTestUtilitiesService.MongoCommand, Long>> commands = MongoTestUtilitiesService.stopAndGetMongoCommands();
        Assertions.assertEquals(1, commands.size());
        Assertions.assertEquals("{\"find\": \"beneficiary_rule\", \"filter\": {\"_id\": \"VALUE\"}, \"$db\": \"idpay\"}", commands.get(0).getKey().getCommand());
        Assertions.assertEquals(2L, commands.get(0).getValue());
    }
}
