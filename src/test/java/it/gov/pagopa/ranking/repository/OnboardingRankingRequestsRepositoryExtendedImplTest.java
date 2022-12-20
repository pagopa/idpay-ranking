package it.gov.pagopa.ranking.repository;

import it.gov.pagopa.ranking.BaseIntegrationTest;
import it.gov.pagopa.ranking.dto.controller.RankingRequestFilter;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

class OnboardingRankingRequestsRepositoryExtendedImplTest extends BaseIntegrationTest {

    public static final String TEST_INITIATIVE_ID = "TEST_INITIATIVE";
    public static final String TEST_ORGANIZATION_ID = "TEST_ORGANIZATION";
    public static final String USER_ID1 = "userId1";
    public static final String USER_ID2 = "userId2";
    public static final String USER_ID3 = "userId3";

    private static final OnboardingRankingRequests testOnboardingRankingRequests1 = OnboardingRankingRequests.builder()
            .initiativeId(TEST_INITIATIVE_ID)
            .organizationId(TEST_ORGANIZATION_ID)
            .userId(USER_ID1)
            .admissibilityCheckDate(LocalDateTime.of(2022,11,22,12,30,30))
            .criteriaConsensusTimestamp(LocalDateTime.of(2022, 11,22, 12,30, 30))
            .rankingValue(10)
            .rank(1)
            .beneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_OK)
            .build();

    private static final OnboardingRankingRequests testOnboardingRankingRequests2 = OnboardingRankingRequests.builder()
            .initiativeId(TEST_INITIATIVE_ID)
            .organizationId(TEST_ORGANIZATION_ID)
            .userId(USER_ID2)
            .admissibilityCheckDate(LocalDateTime.of(2022,11,22,12,30,30))
            .criteriaConsensusTimestamp(LocalDateTime.of(2022, 11,22, 12,30, 30))
            .rankingValue(100)
            .rank(2)
            .beneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_KO)
            .build();

    private static final OnboardingRankingRequests testOnboardingRankingRequests3 = OnboardingRankingRequests.builder()
            .initiativeId(TEST_INITIATIVE_ID)
            .organizationId(TEST_ORGANIZATION_ID)
            .userId(USER_ID3)
            .admissibilityCheckDate(LocalDateTime.of(2022,11,22,12,30,30))
            .criteriaConsensusTimestamp(LocalDateTime.of(2022, 11,22, 12,30, 30))
            .rankingValue(1000)
            .rank(3)
            .beneficiaryRankingStatus(BeneficiaryRankingStatus.ONBOARDING_KO)
            .build();

    List<OnboardingRankingRequests> testData = List.of(testOnboardingRankingRequests1,testOnboardingRankingRequests2,testOnboardingRankingRequests3);

    @Autowired
    private OnboardingRankingRequestsRepository repository;
    @Autowired
    private OnboardingRankingRequestsRepositoryExtendedImpl extendedRepository;

    @BeforeEach
    void prepareTestData() {
        repository.saveAll(testData);
    }

    @AfterEach
    void cleanData() {
        repository.deleteAll(testData);
    }

    @Test
    void testFindAllBy() {
        Page<OnboardingRankingRequests> result = repository.findAllBy(TEST_INITIATIVE_ID, null, null);


        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.getTotalElements());
        checkFindAllResult(result);
    }

    @Test
    void testFindAllByWithFilters() {
        // all filters
        RankingRequestFilter filter = RankingRequestFilter.builder()
                .beneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_OK)
                .userId(USER_ID1)
                .build();

        Pageable pageable = PageRequest.of(0,10, Sort.by(OnboardingRankingRequests.Fields.rank));

        Page<OnboardingRankingRequests> result = repository.findAllBy(TEST_INITIATIVE_ID, filter, pageable);

        // result 1 OK
        Assertions.assertNotNull(result);
        checkFindWithFiltersResult(result);
    }

    private void checkFindAllResult(Page<OnboardingRankingRequests> result) {
        for(OnboardingRankingRequests r : result) {
            Assertions.assertNotNull(r);
        }
    }

    private static void checkFindWithFiltersResult(Page<OnboardingRankingRequests> resultList) {
        Assertions.assertEquals(1, resultList.getTotalElements());

        OnboardingRankingRequests result = resultList.getContent().get(0);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testOnboardingRankingRequests1.getInitiativeId(), result.getInitiativeId());
    }
}