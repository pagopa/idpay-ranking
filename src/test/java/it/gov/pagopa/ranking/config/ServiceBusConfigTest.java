package it.gov.pagopa.ranking.config;

import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ServiceBusConfigTest {

    @Test
    void adminClientNotNul() {
        //Given
        ServiceBusConfig config = new ServiceBusConfig("Endpoint=sb://NameSpace/;SharedAccessKeyName=AccessKeyName;SharedAccessKey=SharedKey");

        //When
        ServiceBusAdministrationClient result = config.adminClient();

        //Then
        Assertions.assertNotNull(result);
    }
}