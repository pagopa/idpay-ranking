package it.gov.pagopa.ranking.config;

import it.gov.pagopa.ranking.connector.rest.pdv.UserRestClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {
        UserRestClient.class
})
public class RestConnectorConfig {
}
