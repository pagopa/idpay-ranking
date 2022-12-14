package it.gov.pagopa.ranking.service.initiative.ranking;

import it.gov.pagopa.ranking.BaseIntegrationTest;
import it.gov.pagopa.ranking.connector.azure.servicebus.AzureServiceBusClient;
import it.gov.pagopa.ranking.connector.azure.storage.AzureBlobClient;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.repository.InitiativeConfigRepository;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import it.gov.pagopa.ranking.service.sign.P7mSignerService;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

@TestPropertySource(properties = {
        "app.ranking.csv.tmp-dir=target/tmp",
        "app.ranking.query-page-size=10",
        "app.ranking.savable-entities-size=13"
})
class OnboardingRankingBuildFileMediatorServiceImplIntegrationTest extends BaseIntegrationTest {

    private static final String INITIATIVE_ID = "INITIATIVE_ID";
    private static final String ORGANIZATION_ID = "ORGANIZATION_ID";
    private static final int RANKING_SIZE = 51; // must be a multiple of 3
    private static final String OTHER_INITIATIVE_ID = "OTHER_INITIATIVE_ID";
    private static final int N = 9; // must be a multiple of 3
    private static final String EXPECTED_FILE_PATH = "%s/%s/initiative-ranking.csv.p7m".formatted(ORGANIZATION_ID, INITIATIVE_ID);
    private static final Path LOCAL_CSV_PATH = Path.of("target/tmp/%s/%s/initiative-ranking.csv".formatted(ORGANIZATION_ID, INITIATIVE_ID));
    private static final Path LOCAL_P7M_PATH = Path.of("target/tmp/%s/%s/initiative-ranking.csv.p7m".formatted(ORGANIZATION_ID, INITIATIVE_ID));
    private static final String EXPECTED_CSV_HEADER = "\"userId\";\"criteriaConsensusTimestamp\";\"rankingValue\";\"ranking\";\"status\"";


    @Value("${app.ranking-build-file.retrieve-initiative.day-before}")
    private int dayBeforeEndingInitiative;

    @Autowired
    private OnboardingRankingRequestsRepository onboardingRankingRequestsRepository;
    @Autowired
    private InitiativeConfigRepository initiativeConfigRepository;

    @MockBean(answer = Answers.RETURNS_MOCKS)
    private AzureServiceBusClient azureServiceBusClientMock;

    @Autowired
    private OnboardingRankingBuildFileMediatorServiceImpl onboardingRankingBuildFileMediatorService;

    @Autowired
    private P7mSignerService p7mSignerService;

    private final List<OnboardingRankingRequests> testData = new ArrayList<>();

    @BeforeEach
    void storeTestData() {

        // INITIATIVES
        initiativeConfigRepository.save(InitiativeConfigFaker.mockInstanceBuilder(1)
                .initiativeId(INITIATIVE_ID)
                .organizationId(ORGANIZATION_ID)
                .rankingEndDate(LocalDate.now().minusDays(dayBeforeEndingInitiative-2))
                .size(RANKING_SIZE)
                .build());

        initiativeConfigRepository.save(InitiativeConfigFaker.mockInstanceBuilder(1)
                .initiativeId(OTHER_INITIATIVE_ID)
                .organizationId(ORGANIZATION_ID)
                .rankingEndDate(LocalDate.now())
                .size(RANKING_SIZE)
                .build());

        // RANKING REQUESTS
        buildTestData(RANKING_SIZE, INITIATIVE_ID, BeneficiaryRankingStatus.TO_NOTIFY);

        buildTestData(N, INITIATIVE_ID, BeneficiaryRankingStatus.TO_NOTIFY);

        buildTestData(N, INITIATIVE_ID, BeneficiaryRankingStatus.ONBOARDING_KO);

        buildTestData(RANKING_SIZE, OTHER_INITIATIVE_ID, BeneficiaryRankingStatus.TO_NOTIFY);


        // MOCKS
        Mockito.when(azureServiceBusClientMock.countMessageInOnboardingRequestQueue()).thenReturn(0);
        Mockito.when(azureServiceBusClientMock.getOnboardingRequestReceiverClient().peekMessage()).thenReturn(null);
    }

    private void buildTestData(int n, String initiativeId, BeneficiaryRankingStatus status) {
        testData.addAll(onboardingRankingRequestsRepository.saveAll(IntStream.range(testData.size(), testData.size()+ n).mapToObj(i -> OnboardingRankingRequestsFaker.mockInstanceBuilder(i)
                .initiativeId(initiativeId)
                .rank(1)
                .rankingValue(i%3==1? i+1 : i)
                .criteriaConsensusTimestamp(i%3==1? LocalDateTime.now().plusMinutes(1) : LocalDateTime.now())
                .beneficiaryRankingStatus(i==0? BeneficiaryRankingStatus.ELIGIBLE_OK : status)
                .build()
        ).toList()));
    }

    @AfterEach
    void clearTestData() {
        initiativeConfigRepository.deleteById(INITIATIVE_ID);
        initiativeConfigRepository.deleteById(OTHER_INITIATIVE_ID);
        onboardingRankingRequestsRepository.deleteAll(testData);
    }

    @Test
    void test() throws IOException {
        // When
        onboardingRankingBuildFileMediatorService.schedule();

        // materialize
        checkRankingMaterializeResult();

        // sign & upload
        checkUpdatedInitiative();

        checkResultCsvFile();

        Assertions.assertTrue(p7mSignerService.verifySign(LOCAL_P7M_PATH));

        // TODO verify upload Azure

    }

    private void checkRankingMaterializeResult() {
        int i=0;
        for(OnboardingRankingRequests r : testData) {
            Optional<OnboardingRankingRequests> optional = onboardingRankingRequestsRepository.findById(r.getId());
            Assertions.assertTrue(optional.isPresent());

            OnboardingRankingRequests entity = optional.get();
            if (entity.getInitiativeId().equals(INITIATIVE_ID)) {
                if (i%3==0) {
                    Assertions.assertEquals(i+1, entity.getRank());
                } else if (i%3==1) {
                    Assertions.assertEquals(i+2, entity.getRank());
                } else if (i%3==2) {
                    Assertions.assertEquals(i, entity.getRank());
                }
                if (i>=0 && i<RANKING_SIZE) {
                    Assertions.assertEquals(BeneficiaryRankingStatus.ELIGIBLE_OK, entity.getBeneficiaryRankingStatus());
                } else if (i>=RANKING_SIZE && i<RANKING_SIZE+N) {
                    Assertions.assertEquals(BeneficiaryRankingStatus.ELIGIBLE_KO, entity.getBeneficiaryRankingStatus());
                } else if (i>=RANKING_SIZE+N && i<RANKING_SIZE+(N*2)){
                    Assertions.assertEquals(BeneficiaryRankingStatus.ONBOARDING_KO, entity.getBeneficiaryRankingStatus());
                }
            } else {
                Assertions.assertEquals(1, entity.getRank());
                Assertions.assertEquals(BeneficiaryRankingStatus.TO_NOTIFY, entity.getBeneficiaryRankingStatus());
            }
            ++i;
        }
    }

    private void checkUpdatedInitiative() {
        Optional<InitiativeConfig> optional = initiativeConfigRepository.findById(INITIATIVE_ID);
        Assertions.assertTrue(optional.isPresent());

        InitiativeConfig resultInitiative = optional.get();
        Assertions.assertEquals(RANKING_SIZE, resultInitiative.getTotalEligibleOk());
        Assertions.assertEquals(N, resultInitiative.getTotalEligibleKo());
        Assertions.assertEquals(N, resultInitiative.getTotalOnboardingKo());
        Assertions.assertEquals(EXPECTED_FILE_PATH, resultInitiative.getRankingFilePath());
    }

    private void checkResultCsvFile() throws IOException {
        List<String> lines = Files.readAllLines(LOCAL_CSV_PATH);
        Assertions.assertEquals(70, lines.size());

        testData.sort(
                Comparator.comparing(OnboardingRankingRequests::getRankingValue)
                        .thenComparing(OnboardingRankingRequests::getCriteriaConsensusTimestamp)
        );
        for(int i=0; i<lines.size();i++) {
            String l = lines.get(i);
            if(i==0)
                Assertions.assertEquals(EXPECTED_CSV_HEADER, l);
            else {
                OnboardingRankingRequests entry = testData.get(i-1);
                if(entry.getInitiativeId().equals(INITIATIVE_ID)) {
                    Assertions.assertTrue(l.contains(entry.getUserId()));
                }
            }
        }
    }

}