package it.gov.pagopa.ranking.config;

import it.gov.pagopa.common.web.dto.ErrorDTO;
import it.gov.pagopa.ranking.constants.RankingConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RankingErrorManagerConfigTest {

    @Test
    void defaultErrorDTO() {
        //Given
        RankingErrorManagerConfig rankingErrorManagerConfig = new RankingErrorManagerConfig();

        //When
        ErrorDTO result = rankingErrorManagerConfig.defaultErrorDTO();

        //Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(RankingConstants.ExceptionCode.GENERIC_ERROR, result.getCode());
        Assertions.assertEquals("A generic error occurred", result.getMessage());
    }

    @Test
    void tooManyRequestsErrorDTO() {
        //Given
        RankingErrorManagerConfig rankingErrorManagerConfig = new RankingErrorManagerConfig();

        //When
        ErrorDTO result = rankingErrorManagerConfig.tooManyRequestsErrorDTO();

        //Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(RankingConstants.ExceptionCode.TOO_MANY_REQUESTS, result.getCode());
        Assertions.assertEquals("Too Many Requests", result.getMessage());
    }

    @Test
    void templateValidationErrorDTO() {
        //Given
        RankingErrorManagerConfig rankingErrorManagerConfig = new RankingErrorManagerConfig();

        //When
        ErrorDTO result = rankingErrorManagerConfig.templateValidationErrorDTO();

        //Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(RankingConstants.ExceptionCode.INVALID_REQUEST, result.getCode());
        Assertions.assertNull(result.getMessage());
    }
}