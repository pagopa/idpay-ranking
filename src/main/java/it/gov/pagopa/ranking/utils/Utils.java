package it.gov.pagopa.ranking.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.messaging.Message;

import java.math.BigDecimal;
import java.util.function.Consumer;

public final class Utils {
    private Utils(){}

    /** It will try to deserialize a message, eventually notifying the error  */
    public static <T> T deserializeMessage(Message<String> message, ObjectReader objectReader, Consumer<Throwable> onError) {
        try {
            return objectReader.readValue(message.getPayload());
        } catch (JsonProcessingException e) {
            onError.accept(e);
            return null;
        }
    }

    public static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    public static Long euro2Cents(BigDecimal euro){
        return euro == null? null : euro.multiply(ONE_HUNDRED).longValue();
    }
}
