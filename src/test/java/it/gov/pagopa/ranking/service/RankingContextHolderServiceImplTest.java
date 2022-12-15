package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.exception.ClientExceptionNoBody;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.service.initiative.InitiativeConfigService;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.ranking.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RankingContextHolderServiceImplTest {

    @Mock
    private InitiativeConfigService initiativeConfigServiceMock;

    private RankingContextHolderService rankingContextHolderService;

    @BeforeEach
    void setUp() {
        rankingContextHolderService = new RankingContextHolderServiceImpl(initiativeConfigServiceMock);
    }

    @Test
    void getInitiative(){
        // Given
        String initiativeIdTest = "NEW_INITIATIVEID";
        String organizationIdTest = "NEW_ORGANIZATIONID";
        InitiativeConfig initiativeConfigMock = InitiativeConfigFaker.mockInstanceBuilder(1).initiativeId(initiativeIdTest).organizationId(organizationIdTest).build();
        Mockito.when(initiativeConfigServiceMock.findByIdOptional(initiativeIdTest)).thenReturn(Optional.of(initiativeConfigMock));

        // When
        InitiativeConfig result = rankingContextHolderService.getInitiativeConfig(initiativeIdTest, organizationIdTest);

        // Then
        Assertions.assertNotNull(result);
        TestUtils.checkNotNullFields(result, "rankingFilePath");
        Assertions.assertEquals(initiativeIdTest, result.getInitiativeId());
        Assertions.assertEquals(organizationIdTest, result.getOrganizationId());

        Mockito.verify(initiativeConfigServiceMock).findByIdOptional(initiativeIdTest);
    }

    @Test
    void getInitiativeNotMatchOrganizationId(){
        // Given
        String initiativeIdTest = "NEW_INITIATIVEID";
        String organizationIdTest = "NEW_ORGANIZATIONID";
        Mockito.when(initiativeConfigServiceMock.findByIdOptional(initiativeIdTest)).thenReturn(Optional.of(InitiativeConfig.builder().initiativeId(initiativeIdTest).organizationId(organizationIdTest).build()));

        // When
        Executable executable = () -> rankingContextHolderService.getInitiativeConfig(initiativeIdTest, "ANOTHER_ORGANIZATIONID");

        // Then
        Assertions.assertThrows(ClientExceptionNoBody.class, executable);

        Mockito.verify(initiativeConfigServiceMock).findByIdOptional(initiativeIdTest);
    }

    @Test
    void getInitiativeNotInDB(){
        // Given
        String initiativeIdTest = "NEW_INITIATIVEID";
        Mockito.when(initiativeConfigServiceMock.findByIdOptional(initiativeIdTest)).thenReturn(Optional.empty());

        // When
        Executable executable = () -> rankingContextHolderService.getInitiativeConfig(initiativeIdTest, "NEW_ORGANIZATIONID");

        // Then
        Assertions.assertThrows(ClientExceptionNoBody.class, executable);

        Mockito.verify(initiativeConfigServiceMock).findByIdOptional(initiativeIdTest);
    }
}