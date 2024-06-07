package it.gov.pagopa.ranking.repository;

import it.gov.pagopa.common.mongo.MongoTest;
import it.gov.pagopa.ranking.dto.controller.RankingRequestFilter;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
@MongoTest
class OnboardingRankingRequestsRepositoryExtendedImplTest {

    public static final String TEST_INITIATIVE_ID = "TEST_INITIATIVE";
    public static final String USER_ID1 = "userId1";
    public static final String USER_ID2 = "userId2";
    public static final String USER_ID3 = "userId3";
    public static final String USER_ID4 = "userId4";
    public static final String USER_ID5 = "userId5";
    public static final String USER_ID6 = "userId6";
    public static final String USER_ID7 = "userId7";
    public static final String USER_ID8 = "userId8";
    public static final String USER_ID9 = "userId9";
    public static final String USER_ID10 = "userId10";
    public static final String USER_ID11 = "userId11";


    private static final OnboardingRankingRequests testOnboardingRankingRequests1=
            OnboardingRankingRequestsFaker.mockInstanceBuilder(1)
            .userId(USER_ID1)
            .beneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_OK)
            .initiativeId(TEST_INITIATIVE_ID)
            .build();

  private static final OnboardingRankingRequests testOnboardingRankingRequests2=
          OnboardingRankingRequestsFaker.mockInstanceBuilder(2)
          .userId(USER_ID2)
          .initiativeId(TEST_INITIATIVE_ID)
          .beneficiaryRankingStatus(BeneficiaryRankingStatus.ELIGIBLE_KO)
          .build();

    private static final OnboardingRankingRequests testOnboardingRankingRequests3=
            OnboardingRankingRequestsFaker.mockInstanceBuilder(3)
            .userId(USER_ID3)
            .initiativeId(TEST_INITIATIVE_ID)
            .beneficiaryRankingStatus(BeneficiaryRankingStatus.ONBOARDING_KO)
            .build();
    private static final OnboardingRankingRequests testOnboardingRankingRequests4=
            OnboardingRankingRequestsFaker.mockInstanceBuilder(4)
            .userId(USER_ID4)
            .initiativeId(TEST_INITIATIVE_ID)
            .beneficiaryRankingStatus(BeneficiaryRankingStatus.ONBOARDING_KO)
            .build();
    private static final OnboardingRankingRequests testOnboardingRankingRequests5=
            OnboardingRankingRequestsFaker.mockInstanceBuilder(5)
            .userId(USER_ID5)
            .initiativeId(TEST_INITIATIVE_ID)
            .beneficiaryRankingStatus(BeneficiaryRankingStatus.ONBOARDING_KO)
            .build();
    private static final OnboardingRankingRequests testOnboardingRankingRequests6=
            OnboardingRankingRequestsFaker.mockInstanceBuilder(6)
            .userId(USER_ID6)
            .initiativeId(TEST_INITIATIVE_ID)
            .beneficiaryRankingStatus(BeneficiaryRankingStatus.ONBOARDING_KO)
            .build();
    private static final OnboardingRankingRequests testOnboardingRankingRequests7=
            OnboardingRankingRequestsFaker.mockInstanceBuilder(7)
            .userId(USER_ID7)
            .initiativeId(TEST_INITIATIVE_ID)
            .beneficiaryRankingStatus(BeneficiaryRankingStatus.ONBOARDING_KO)
            .build();
    private static final OnboardingRankingRequests testOnboardingRankingRequests8=
            OnboardingRankingRequestsFaker.mockInstanceBuilder(8)
            .userId(USER_ID8)
            .initiativeId(TEST_INITIATIVE_ID)
            .beneficiaryRankingStatus(BeneficiaryRankingStatus.ONBOARDING_KO)
            .build();
    private static final OnboardingRankingRequests testOnboardingRankingRequests9=
            OnboardingRankingRequestsFaker.mockInstanceBuilder(9)
            .userId(USER_ID9)
            .initiativeId(TEST_INITIATIVE_ID)
            .beneficiaryRankingStatus(BeneficiaryRankingStatus.ONBOARDING_KO)
            .build();
    private static final OnboardingRankingRequests testOnboardingRankingRequests10=
            OnboardingRankingRequestsFaker.mockInstanceBuilder(10)
            .userId(USER_ID10)
            .initiativeId(TEST_INITIATIVE_ID)
            .beneficiaryRankingStatus(BeneficiaryRankingStatus.ONBOARDING_KO)
            .build();
    private static final OnboardingRankingRequests testOnboardingRankingRequests11=
            OnboardingRankingRequestsFaker.mockInstanceBuilder(11)
            .userId(USER_ID11)
            .initiativeId(TEST_INITIATIVE_ID)
            .beneficiaryRankingStatus(BeneficiaryRankingStatus.ONBOARDING_KO)
            .build();

    List<OnboardingRankingRequests> testData = List.of(testOnboardingRankingRequests1,testOnboardingRankingRequests2,
            testOnboardingRankingRequests3,testOnboardingRankingRequests4,testOnboardingRankingRequests5,testOnboardingRankingRequests6,
            testOnboardingRankingRequests7,testOnboardingRankingRequests8,testOnboardingRankingRequests9,testOnboardingRankingRequests10,
            testOnboardingRankingRequests11);

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
        Assertions.assertEquals(11, result.getTotalElements());
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

    @Test
    void testFindAllByPageable(){
        RankingRequestFilter filter = RankingRequestFilter.builder()
                .beneficiaryRankingStatus(BeneficiaryRankingStatus.ONBOARDING_KO)
                .build();

        Pageable pageable = PageRequest.of(0,5, Sort.by(OnboardingRankingRequests.Fields.rank));
        List<OnboardingRankingRequests> listOfUserExpected = List.of(
                testOnboardingRankingRequests3,testOnboardingRankingRequests4,testOnboardingRankingRequests5,testOnboardingRankingRequests6,
                testOnboardingRankingRequests7);
        Page<OnboardingRankingRequests> result = repository.findAllBy(TEST_INITIATIVE_ID, filter, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2,result.getTotalPages());
        Assertions.assertEquals(9, result.getTotalElements());
        Assertions.assertEquals(5, result.getNumberOfElements());
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(listOfUserExpected, result.getContent());
    }

    @Test
    void deletePaged (){
        // Given
        int pageSize = 100;

        // When
        List<OnboardingRankingRequests> result = repository.deletePaged(TEST_INITIATIVE_ID, pageSize);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(11, result.size());
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
