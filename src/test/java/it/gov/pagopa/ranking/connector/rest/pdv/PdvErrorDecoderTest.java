package it.gov.pagopa.ranking.connector.rest.pdv;

import feign.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PdvErrorDecoderTest {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void decode(boolean retryStatus) {
        //Given
        PdvErrorDecoder pdvErrorDecoder = new PdvErrorDecoder();
        Response response = Response.builder()
                .status(retryStatus ? 429 : 500)
                .reason("REASON")
                .request(Request.create(Request.HttpMethod.GET,"URL", Map.of(), "body".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8, new RequestTemplate()))
                .build();

        //When
        Exception result = pdvErrorDecoder.decode("methodKey", response);

        Assertions.assertNotNull(result);

        if (retryStatus){ //TODO refine
            Assertions.assertTrue(result instanceof RetryableException);
        }
        else {
            Assertions.assertTrue(result instanceof FeignException.InternalServerError);
        }

    }
}