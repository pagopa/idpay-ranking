package it.gov.pagopa.ranking.connector.rest.pdv;

import feign.Response;

public class PdvErrorDecoderExt extends PdvErrorDecoder{

    private static int invocationCount;

    @Override
    public Exception decode(String methodKey, Response response) {
        invocationCount++;
        return super.decode(methodKey, response);
    }

    public int getInvocationCount() {
        return invocationCount;
    }

    public void resetCounter() {
        invocationCount = 0;
    }
}
