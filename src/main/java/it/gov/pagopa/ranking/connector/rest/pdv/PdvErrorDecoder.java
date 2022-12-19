package it.gov.pagopa.ranking.connector.rest.pdv;

import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PdvErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException exception = feign.FeignException.errorStatus(methodKey, response);
        log.info("Feign Client Exception caught with Status [{}] during [{}] to [{}]", response.status(), response.request().httpMethod().name(), response.request().url());

        // Retry if status is 429
        if (response.status() == 429) {
            log.info("Retrying is about to start");
            return new RetryableException(
                    response.status(),
                    exception.getMessage(),
                    response.request().httpMethod(),
                    exception,
                    null,
                    response.request());
        }

        return exception;
    }
}
