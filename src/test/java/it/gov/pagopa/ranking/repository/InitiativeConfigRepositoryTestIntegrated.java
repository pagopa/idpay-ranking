package it.gov.pagopa.ranking.repository;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations = {
        "classpath:/mongodbEmbeddedDisabled.properties",
        "classpath:/secrets/mongodbConnectionString.properties"
})
class InitiativeConfigRepositoryTestIntegrated extends InitiativeConfigRepositoryTest{
}
