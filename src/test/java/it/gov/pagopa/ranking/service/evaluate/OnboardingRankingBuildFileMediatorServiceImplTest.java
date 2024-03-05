package it.gov.pagopa.ranking.service.evaluate;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import it.gov.pagopa.common.utils.MemoryAppender;
import it.gov.pagopa.ranking.connector.azure.storage.InitiativeRankingBlobClient;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.repository.InitiativeConfigRepository;
import it.gov.pagopa.ranking.service.evaluate.retrieve.OnboardingRankingRuleEndedRetrieverService;
import it.gov.pagopa.ranking.service.sign.P7mSignerService;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@TestPropertySource(
        properties = {
                "app.ranking.csv.tmp-dir=DUMMYDIRECTORYFORTEST"
        })
@ContextConfiguration(classes = {OnboardingRankingBuildFileMediatorServiceImpl.class})
class OnboardingRankingBuildFileMediatorServiceImplTest {
    private static final String RANKING_FILEPATH= "DUMMYRANKINGFILEPATH";

    @MockBean
    private OnboardingRankingRuleEndedRetrieverService onboardingRankingRuleEndedRetrieverServiceMock;
    @MockBean
    private RankingMaterializerService rankingMaterializerServiceMock;
    @MockBean
    private P7mSignerService p7mSignerServiceMock;
    @MockBean
    private InitiativeRankingBlobClient rankingBlobClientMock;
    @MockBean
    private InitiativeConfigRepository initiativeConfigRepositoryMock;

    @Value("${app.ranking.csv.tmp-dir}")
    private String dummyDirNameForTest;
    @Autowired
    private OnboardingRankingBuildFileMediatorService onboardingRankingBuildFileMediatorService;

    @Test
    void executeInitiativeEndedEmpty() {
        //Given
        Mockito.when(onboardingRankingRuleEndedRetrieverServiceMock.retrieve()).thenReturn(Collections.emptyList());

        //When
        List<InitiativeConfig> result = onboardingRankingBuildFileMediatorService.execute();

        //Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void executeEndedInitiatives() throws IOException {
        //Given
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("it.gov.pagopa.ranking.service.evaluate.OnboardingRankingBuildFileMediatorServiceImpl");
        MemoryAppender memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.WARN);
        logger.addAppender(memoryAppender);
        memoryAppender.start();

        Path path = Paths.get("%s/%s".formatted(dummyDirNameForTest, RANKING_FILEPATH));
        Path signedPath = Paths.get("%s/%s.p7m".formatted(dummyDirNameForTest, RANKING_FILEPATH));

        Files.createDirectories(path.getParent());
        Files.createFile(path);

        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setRankingFilePath(RANKING_FILEPATH);
        Mockito.when(onboardingRankingRuleEndedRetrieverServiceMock.retrieve()).thenReturn(List.of(initiativeConfig));

        Mockito.when(rankingMaterializerServiceMock.materialize(initiativeConfig)).thenReturn(path);

        Mockito.when(p7mSignerServiceMock.sign(path)).thenReturn(signedPath);

        Mockito.when(initiativeConfigRepositoryMock.save(initiativeConfig)).thenReturn(initiativeConfig);

        //When
        List<InitiativeConfig> result = onboardingRankingBuildFileMediatorService.execute();

        //Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(List.of(initiativeConfig), result);

        System.out.println("-->" + memoryAppender.getLoggedEvents().get(0).getFormattedMessage());
        Assertions.assertEquals("[RANKING_BUILD_ONBOARDING_RANKING_FILE] Cannot delete file %s\\%s.p7m from container".formatted(dummyDirNameForTest, RANKING_FILEPATH), memoryAppender.getLoggedEvents().get(0).getFormattedMessage());

        //clean temporally directory for test
        FileUtils.deleteDirectory(new File(dummyDirNameForTest));
    }
}