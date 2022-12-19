package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.OnboardingRankingRequestDTO;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsDTOFaker;
import it.gov.pagopa.ranking.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

class OnboardingRankingRequestsDTO2ModelMapperTest {

    private final OnboardingRankingRequestsDTO2ModelMapper mapper = new OnboardingRankingRequestsDTO2ModelMapper();

    @Test
    void test() {
        // Given
        OnboardingRankingRequestDTO requestDto = OnboardingRankingRequestsDTOFaker.mockInstance(1);

        InitiativeConfig initiative = InitiativeConfigFaker.mockInstance(0);

        // When
        OnboardingRankingRequests result = mapper.apply(requestDto, initiative);

        // Then
        commonChecks(requestDto, result, requestDto.getRankingValue());

        Assertions.assertEquals(BeneficiaryRankingStatus.TO_NOTIFY, result.getBeneficiaryRankingStatus());
    }

    @Test
    void testOnboardingKoASC() {
        // Given
        OnboardingRankingRequestDTO requestDto = OnboardingRankingRequestsDTOFaker.mockInstance(1);
        requestDto.setOnboardingKo(true);

        InitiativeConfig initiative = InitiativeConfigFaker.mockInstance(0);

        // When
        OnboardingRankingRequests result = mapper.apply(requestDto, initiative);

        // Then
        commonChecks(requestDto, result, Long.MAX_VALUE);

        Assertions.assertEquals(BeneficiaryRankingStatus.ONBOARDING_KO, result.getBeneficiaryRankingStatus());
    }

    @Test
    void testOnboardingKoDESC() {
        // Given
        OnboardingRankingRequestDTO requestDto = OnboardingRankingRequestsDTOFaker.mockInstance(1);
        requestDto.setOnboardingKo(true);

        InitiativeConfig initiative = InitiativeConfigFaker.mockInstance(0);
        initiative.getRankingFields().get(0).setDirection(Sort.Direction.DESC);

        // When
        OnboardingRankingRequests result = mapper.apply(requestDto, initiative);

        // Then
        commonChecks(requestDto, result, -1);

        Assertions.assertEquals(BeneficiaryRankingStatus.ONBOARDING_KO, result.getBeneficiaryRankingStatus());
    }

    private static void commonChecks(OnboardingRankingRequestDTO requestDto, OnboardingRankingRequests result, long expectedRankingValue) {
        Assertions.assertNotNull(result);

        Assertions.assertEquals(requestDto.getUserId().concat(requestDto.getInitiativeId()), result.getId());
        Assertions.assertEquals(requestDto.getUserId(), result.getUserId());
        Assertions.assertEquals(requestDto.getInitiativeId(), result.getInitiativeId());
        Assertions.assertEquals(requestDto.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(requestDto.getAdmissibilityCheckDate(), result.getAdmissibilityCheckDate());
        Assertions.assertEquals(requestDto.getCriteriaConsensusTimestamp(), result.getCriteriaConsensusTimestamp());
        Assertions.assertEquals(requestDto.getRankingValue(), result.getRankingValue2Show());
        Assertions.assertEquals(expectedRankingValue, result.getRankingValue());
        Assertions.assertEquals(expectedRankingValue, result.getRankingValueOriginal());
        TestUtils.checkNotNullFields(result);
    }

}