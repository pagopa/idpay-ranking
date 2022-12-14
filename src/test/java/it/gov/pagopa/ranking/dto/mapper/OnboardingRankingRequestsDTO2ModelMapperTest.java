package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.OnboardingRankingRequestDTO;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsDTOFaker;
import it.gov.pagopa.ranking.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OnboardingRankingRequestsDTO2ModelMapperTest {

    private final OnboardingRankingRequestsDTO2ModelMapper mapper = new OnboardingRankingRequestsDTO2ModelMapper();

    @Test
    void test() {
        // Given
        OnboardingRankingRequestDTO requestDto = OnboardingRankingRequestsDTOFaker.mockInstance(1);

        // When
        OnboardingRankingRequests result = mapper.apply(requestDto);

        // Then
        commonChecks(requestDto, result);

        Assertions.assertEquals(BeneficiaryRankingStatus.TO_NOTIFY, result.getBeneficiaryRankingStatus());
    }

    @Test
    void testOnbaordingKo() {
        // Given
        OnboardingRankingRequestDTO requestDto = OnboardingRankingRequestsDTOFaker.mockInstance(1);
        requestDto.setOnboardingKo(true);

        // When
        OnboardingRankingRequests result = mapper.apply(requestDto);

        // Then
        commonChecks(requestDto, result);

        Assertions.assertEquals(BeneficiaryRankingStatus.ONBOARDING_KO, result.getBeneficiaryRankingStatus());
    }

    private static void commonChecks(OnboardingRankingRequestDTO requestDto, OnboardingRankingRequests result) {
        Assertions.assertNotNull(result);

        Assertions.assertEquals(requestDto.getUserId().concat(requestDto.getInitiativeId()), result.getId());
        Assertions.assertEquals(requestDto.getUserId(), result.getUserId());
        Assertions.assertEquals(requestDto.getInitiativeId(), result.getInitiativeId());
        Assertions.assertEquals(requestDto.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(requestDto.getAdmissibilityCheckDate(), result.getAdmissibilityCheckDate());
        Assertions.assertEquals(requestDto.getCriteriaConsensusTimestamp(), result.getCriteriaConsensusTimestamp());
        Assertions.assertEquals(requestDto.getRankingValue(), result.getRankingValue());
        Assertions.assertEquals(requestDto.getRankingValue(), result.getRankingValueOriginal());
        TestUtils.checkNotNullFields(result);
    }

}