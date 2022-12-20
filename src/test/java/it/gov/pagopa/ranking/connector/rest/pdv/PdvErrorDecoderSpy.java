package it.gov.pagopa.ranking.connector.rest.pdv;

import feign.Response;

import java.util.concurrent.atomic.AtomicInteger;

public class PdvErrorDecoderSpy extends PdvErrorDecoder{

    private static AtomicInteger invocationCount = new AtomicInteger();

    @Override
    public Exception decode(String methodKey, Response response) {
        invocationCount.incrementAndGet();
        return super.decode(methodKey, response);
    }

    public static int getInvocationCount() {
        return invocationCount.intValue();
    }

    public static void resetCounter() {
        invocationCount.set(0);
    }
}
