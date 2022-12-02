package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.controller.RankingPageDTO;
import it.gov.pagopa.ranking.dto.controller.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import it.gov.pagopa.ranking.test.fakers.RankingRequestsApiDTOFaker;
import it.gov.pagopa.ranking.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.util.List;

class PageOnboardingRequests2RankingPageDTOMapperTest {

    @Test
    void test() {
        // Given
        Pageable pageable = PageRequest.of(0,10,Sort.by(Sort.Direction.ASC,OnboardingRankingRequests.Fields.rank));
        Page<OnboardingRankingRequests> page = new PageImpl<>(
                List.of(OnboardingRankingRequestsFaker.mockInstance(1)),
                pageable,
                1);
        List<RankingRequestsApiDTO> dtoList = List.of(RankingRequestsApiDTOFaker.mockInstance(1));
        InitiativeConfig initiative = InitiativeConfigFaker.mockInstance(1);
        initiative.setRankingStatus(RankingStatus.COMPLETED);
        initiative.setRankingFilePath("test.zip");

        RankingPageDTO expected = RankingPageDTO.builder()
                .content(dtoList)
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .rankingStatus(RankingStatus.COMPLETED)
                .rankingPublishedTimestamp(initiative.getRankingPublishedTimestamp())
                .rankingGeneratedTimestamp(initiative.getRankingGeneratedTimestamp())
                .totalEligibleOk(0)
                .totalEligibleKo(0)
                .totalOnboardingKo(0)
                .rankingFilePath("test.zip")
                .build();

        PageOnboardingRequests2RankingPageDTOMapper mapper = new PageOnboardingRequests2RankingPageDTOMapper();

        // When
        RankingPageDTO result = mapper.apply(
                page,
                dtoList,
                initiative);

        // Then
        Assertions.assertNotNull(result);
        TestUtils.checkNotNullFields(result);
        Assertions.assertEquals(expected.getContent(), result.getContent());
        Assertions.assertEquals(expected.getPageNumber(), result.getPageNumber());
        Assertions.assertEquals(expected.getPageSize(), result.getPageSize());
        Assertions.assertEquals(expected.getTotalElements(), result.getTotalElements());
        Assertions.assertEquals(expected.getTotalPages(), result.getTotalPages());
        Assertions.assertEquals(expected.getRankingStatus(), result.getRankingStatus());
        Assertions.assertEquals(expected.getRankingPublishedTimestamp(), result.getRankingPublishedTimestamp());
        Assertions.assertEquals(expected.getRankingGeneratedTimestamp(), result.getRankingGeneratedTimestamp());
        Assertions.assertEquals(expected.getTotalEligibleOk(), result.getTotalEligibleOk());
        Assertions.assertEquals(expected.getTotalEligibleKo(), result.getTotalEligibleKo());
        Assertions.assertEquals(expected.getTotalOnboardingKo(), result.getTotalOnboardingKo());
        Assertions.assertEquals(expected.getRankingFilePath(), result.getRankingFilePath());
    }

}