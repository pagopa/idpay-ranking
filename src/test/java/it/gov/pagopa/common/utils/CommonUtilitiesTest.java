package it.gov.pagopa.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.support.MessageBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;

@Slf4j
class CommonUtilitiesTest {

    @Test
    void testCentsToEuro(){
        Assertions.assertEquals(
                BigDecimal.valueOf(5).setScale(2, RoundingMode.UNNECESSARY),
                CommonUtilities.centsToEuro(5_00L)
        );
    }

    @Test
    void testEuroToCents(){
        Assertions.assertNull(CommonUtilities.euroToCents(null));
        Assertions.assertEquals(100L, CommonUtilities.euroToCents(BigDecimal.ONE));
        Assertions.assertEquals(325L, CommonUtilities.euroToCents(BigDecimal.valueOf(3.25)));

        Assertions.assertEquals(
                5_00L,
                CommonUtilities.euroToCents(TestUtils.bigDecimalValue(5))
        );
    }

    @Test
    void readMessagePayloadArrayByteMessage(){
        //Given
        String stringIntoMessage = "DUMMY_MESSAGE";

        //When
        String result = CommonUtilities.readMessagePayload(MessageBuilder.withPayload(stringIntoMessage.getBytes(StandardCharsets.UTF_8)).build());

        //Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(stringIntoMessage, result);
    }

}
