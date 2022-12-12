package it.gov.pagopa.ranking.service.initiative.ranking;

import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import it.gov.pagopa.ranking.BaseIntegrationTest;
import it.gov.pagopa.ranking.connector.azure.servicebus.AzureServiceBusClient;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.repository.InitiativeConfigRepository;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@TestPropertySource(properties = {
        "app.ranking.csv.tmp-dir=target/tmp",
        "app.ranking.query-page-size=10"
})
class OnboardingRankingBuildFileMediatorServiceImplIntegrationTest extends BaseIntegrationTest {

    private static final String INITIATIVE_ID = "INITIATIVE_ID";
    private static final String ORGANIZATION_ID = "ORGANIZATION_ID";
    private static final int RANKING_SIZE = 50;
    public static final String OTHER_INITIATIVE_ID = "OTHER_INITIATIVE_ID";

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

    private List<OnboardingRankingRequests> testData = new ArrayList<>();

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

        buildTestData(10, INITIATIVE_ID, BeneficiaryRankingStatus.TO_NOTIFY);

        buildTestData(10, INITIATIVE_ID, BeneficiaryRankingStatus.ONBOARDING_KO);

        buildTestData(RANKING_SIZE, OTHER_INITIATIVE_ID, BeneficiaryRankingStatus.TO_NOTIFY);


        // MOCKS
        Mockito.when(azureServiceBusClientMock.countMessageInOnboardingRequestQueue()).thenReturn(0);
        Mockito.when(azureServiceBusClientMock.getOnboardingRequestReceiverClient().peekMessage()).thenReturn(null);
    }

    private void buildTestData(int n, String initiativeId, BeneficiaryRankingStatus status) {
        testData.addAll(onboardingRankingRequestsRepository.saveAll(IntStream.range(testData.size(), testData.size()+ n).mapToObj(i -> OnboardingRankingRequestsFaker.mockInstanceBuilder(i)
                .initiativeId(initiativeId)
                .rankingValue(i)
                .beneficiaryRankingStatus(status)
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
    void test() {
        onboardingRankingBuildFileMediatorService.schedule();

        // TODO verify initiative status and counters
        // TODO verify other initiative not changed
        // TODO verify uploaded file
    }

}