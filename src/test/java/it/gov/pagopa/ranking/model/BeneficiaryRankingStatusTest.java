package it.gov.pagopa.ranking.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BeneficiaryRankingStatusTest {
    private static final String TO_NOTIFY_STR = "TO_NOTIFY";

    @Test
    void testToString() {
        //When
        String result = BeneficiaryRankingStatus.TO_NOTIFY.toString();

        //Then
        Assertions.assertEquals(TO_NOTIFY_STR, result);
    }

    @Test
    void fromValueExist() {
        //When
        BeneficiaryRankingStatus result = BeneficiaryRankingStatus.fromValue(TO_NOTIFY_STR);

        //Then
        Assertions.assertEquals(BeneficiaryRankingStatus.TO_NOTIFY, result);
    }

    @Test
    void fromValueNotExist() {
        //When
        BeneficiaryRankingStatus result = BeneficiaryRankingStatus.fromValue("DUMMY_STATUS");

        //Then
        Assertions.assertNull(result);
    }
}