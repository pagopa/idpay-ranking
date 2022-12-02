package it.gov.pagopa.ranking.config;

import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceBusConfig {
    private final String namespaceConnectionString;

    public ServiceBusConfig(@Value("${app.service-bus.namespace.string-connection}") String namespaceConnectionString) {
        this.namespaceConnectionString = namespaceConnectionString;
    }

    @Bean
    public ServiceBusAdministrationClient adminClient(){
        return new ServiceBusAdministrationClientBuilder()
                .connectionString(namespaceConnectionString)
                .buildClient();
    }
}
