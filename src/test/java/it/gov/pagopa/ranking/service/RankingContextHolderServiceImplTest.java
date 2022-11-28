package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.service.initiative.InitiativeConfigService;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.ranking.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ExtendWith(MockitoExtension.class)
class RankingContextHolderServiceImplTest {

    @Mock
    private InitiativeConfigService initiativeConfigServiceMock;

    private RankingContextHolderService rankingContextHolderService;

    private final String initiativeIdInCache = "INITIATIVEID_1";
    private final String organizationIdInCache = "ORGANIZATIONID_1";

    private Field initiativeCacheField;

    @BeforeEach
    void setUp() {
        rankingContextHolderService = new RankingContextHolderServiceImpl(initiativeConfigServiceMock);

        Map<String, InitiativeConfig> initiativeId2ConfigTest = new ConcurrentHashMap<>();
        initiativeId2ConfigTest.put(initiativeIdInCache,
                InitiativeConfigFaker.mockInstanceBuilder(1).initiativeId(initiativeIdInCache).organizationId(organizationIdInCache).build());

        initiativeCacheField = ReflectionUtils.findField(RankingContextHolderServiceImpl.class, "initiativeId2Config");
        Assertions.assertNotNull(initiativeCacheField);
        ReflectionUtils.makeAccessible(initiativeCacheField);
        ReflectionUtils.setField(initiativeCacheField,rankingContextHolderService, initiativeId2ConfigTest);
    }

    @Test
    void getInitiativeInCache(){
        // When
        Map<String, InitiativeConfig> inspectCache = retrieveCache();
        Assertions.assertNotNull(inspectCache.get(initiativeIdInCache));
        Assertions.assertEquals(1,inspectCache.size());

        InitiativeConfig result = rankingContextHolderService.getInitiativeConfig(initiativeIdInCache, organizationIdInCache);

        // Then
        Assertions.assertNotNull(result);
        TestUtils.checkNotNullFields(result, "rankingPathFile");
        Assertions.assertEquals(initiativeIdInCache, result.getInitiativeId());
        Assertions.assertEquals(organizationIdInCache, result.getOrganizationId());
        Assertions.assertNotNull(inspectCache.get(initiativeIdInCache));
        Assertions.assertEquals(1,inspectCache.size());

        Mockito.verify(initiativeConfigServiceMock, Mockito.never()).findById(initiativeIdInCache);
    }

    @Test
    void getInitiativeInCacheNotMatchOrganizationId(){
        // When
        Map<String, InitiativeConfig> inspectCache = retrieveCache();
        Assertions.assertNotNull(inspectCache.get(initiativeIdInCache));
        Assertions.assertEquals(1,inspectCache.size());

        InitiativeConfig result = rankingContextHolderService.getInitiativeConfig(initiativeIdInCache, "ANOTHER_ORGANIZATIONID");

        // Then
        Assertions.assertNull(result);

        Assertions.assertEquals(1,inspectCache.size());
        Mockito.verify(initiativeConfigServiceMock, Mockito.never()).findById(initiativeIdInCache);
    }

    @Test
    void getInitiativeNotInCache(){
        // Given
        String initiativeIdTest = "NEW_INITIATIVEID";
        String organizationIdTest = "NEW_ORGANIZATIONID";
        InitiativeConfig initiativeConfigMock = InitiativeConfigFaker.mockInstanceBuilder(1).initiativeId(initiativeIdTest).organizationId(organizationIdTest).build();
        Mockito.when(initiativeConfigServiceMock.findById(initiativeIdTest)).thenReturn(initiativeConfigMock);

        // When
        Map<String, InitiativeConfig> inspectCache = retrieveCache();
        Assertions.assertNull(inspectCache.get(initiativeIdTest));
        Assertions.assertEquals(1,inspectCache.size());

        InitiativeConfig result = rankingContextHolderService.getInitiativeConfig(initiativeIdTest, organizationIdTest);

        // Then
        Assertions.assertNotNull(result);
        TestUtils.checkNotNullFields(result, "rankingPathFile");
        Assertions.assertEquals(initiativeIdTest, result.getInitiativeId());
        Assertions.assertEquals(organizationIdTest, result.getOrganizationId());
        Assertions.assertNotNull(inspectCache.get(initiativeIdTest));
        Assertions.assertEquals(2,inspectCache.size());

        Mockito.verify(initiativeConfigServiceMock).findById(initiativeIdTest);
    }

    @Test
    void getInitiativeNotInCacheAndNotMatchOrganizationId(){
        // Given
        String initiativeIdTest = "NEW_INITIATIVEID";
        String organizationIdTest = "NEW_ORGANIZATIONID";
        Mockito.when(initiativeConfigServiceMock.findById(initiativeIdTest)).thenReturn(InitiativeConfig.builder().initiativeId(initiativeIdTest).organizationId(organizationIdTest).build());

        // When
        Map<String, InitiativeConfig> inspectCache = retrieveCache();
        Assertions.assertNull(inspectCache.get(initiativeIdTest));
        Assertions.assertEquals(1,inspectCache.size());

        InitiativeConfig result = rankingContextHolderService.getInitiativeConfig(initiativeIdTest, "ANOTHER_ORGANIZATIONID");

        // Then
        Assertions.assertNull(result);

        Assertions.assertNotNull(inspectCache.get(initiativeIdTest));
        Assertions.assertEquals(2,inspectCache.size());

        Mockito.verify(initiativeConfigServiceMock).findById(initiativeIdTest);
    }

    @Test
    void getInitiativeNotInDB(){
        // Given
        String initiativeIdTest = "NEW_INITIATIVEID";
        Mockito.when(initiativeConfigServiceMock.findById(initiativeIdTest)).thenReturn(null);

        // When
        Map<String, InitiativeConfig> inspectCache = retrieveCache();
        Assertions.assertNull(inspectCache.get(initiativeIdTest));
        Assertions.assertEquals(1,inspectCache.size());

        InitiativeConfig result = rankingContextHolderService.getInitiativeConfig(initiativeIdTest, "NEW_ORGANIZATIONID");

        // Then
        Assertions.assertNull(result);

        Assertions.assertNull(inspectCache.get(initiativeIdTest));
        Assertions.assertEquals(1,inspectCache.size());

        Mockito.verify(initiativeConfigServiceMock).findById(initiativeIdTest);
    }

    private Map<String, InitiativeConfig> retrieveCache() {
        Object cacheBefore = ReflectionUtils.getField(initiativeCacheField, rankingContextHolderService);
        Assertions.assertNotNull(cacheBefore);
        return (Map<String, InitiativeConfig>) cacheBefore;
    }
}