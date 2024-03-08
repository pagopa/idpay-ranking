package it.gov.pagopa.ranking.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RankingStatusTest {

    private static final String WAITING_END_STR = "WAITING_END";

    @Test
    void testToString() {
        //When
        String result = RankingStatus.WAITING_END.toString();

        //Then
        Assertions.assertEquals(WAITING_END_STR, result);
    }

    @Test
    void fromValueExist() {
        //When
        RankingStatus result = RankingStatus.fromValue(WAITING_END_STR);

        //Then
        Assertions.assertEquals(RankingStatus.WAITING_END, result);
    }

    @Test
    void fromValueNotExist() {
        //When
        RankingStatus result = RankingStatus.fromValue("DUMMY_STATUS");

        //Then
        Assertions.assertNull(result);
    }
}