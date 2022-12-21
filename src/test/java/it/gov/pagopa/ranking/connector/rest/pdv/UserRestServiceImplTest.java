package it.gov.pagopa.ranking.connector.rest.pdv;

import it.gov.pagopa.ranking.dto.pdv.UserInfoPDV;
import it.gov.pagopa.ranking.model.User;
import it.gov.pagopa.ranking.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
class UserRestServiceImplTest {

    private static final String TEST_API_KEY = "x_api_key";
    @Mock
    private UserRestClient userRestClientMock;

    private UserRestService userService;

    private final int initialSizeCache = 2;
    private Field userCacheField;

    @BeforeEach
    void setUp() {
        userService = new UserRestServiceImpl(TEST_API_KEY, userRestClientMock);

        Map<String, User>  userCacheTest = new ConcurrentHashMap<>();
        IntStream.range(0, initialSizeCache).forEach(i -> userCacheTest.put("USERID_%d".formatted(i),
                User.builder().fiscalCode("FISCALCODE_%d".formatted(i)).build()));

        userCacheField = ReflectionUtils.findField(UserRestServiceImpl.class, "userCache");
        Assertions.assertNotNull(userCacheField);
        ReflectionUtils.makeAccessible(userCacheField);
        ReflectionUtils.setField(userCacheField, userService,userCacheTest);
    }

    @Test
    void getUserInfoNotInCache(){
        // Given
        String userIdTest = "USERID_NEW";
        Mockito.when(userRestClientMock.getPii(userIdTest, TEST_API_KEY)).thenReturn(UserInfoPDV.builder().pii("FISCALCODE_RETRIEVED").build());

        // When
        Map<String, User> inspectCache = retrieveCache();
        Assertions.assertNull(inspectCache.get(userIdTest));
        Assertions.assertEquals(initialSizeCache,inspectCache.size());

        User result = userService.getUser(userIdTest);


        // Then
        Assertions.assertNotNull(result);
        TestUtils.checkNotNullFields(result, "name", "surname"); // TODO name and surname will be filled here?
        Assertions.assertEquals("FISCALCODE_RETRIEVED", result.getFiscalCode());
        Assertions.assertNotNull(inspectCache.get(userIdTest));
        Assertions.assertEquals(initialSizeCache+1,inspectCache.size());


        Mockito.verify(userRestClientMock).getPii(userIdTest, TEST_API_KEY);
    }

    @Test
    void getUserInfoInCache(){
        // Given
        String userIdTest = "USERID_0";

        // When
        Map<String, User> inspectCache = retrieveCache();
        Assertions.assertNotNull(inspectCache.get(userIdTest));
        Assertions.assertEquals(initialSizeCache,inspectCache.size());

        User result = userService.getUser(userIdTest);

        // Then
        Assertions.assertNotNull(result);
        TestUtils.checkNotNullFields(result, "name", "surname"); // TODO name and surname will be filled here?
        Assertions.assertEquals("FISCALCODE_0", result.getFiscalCode());
        Assertions.assertNotNull(inspectCache.get(userIdTest));
        Assertions.assertEquals(initialSizeCache,inspectCache.size());

        Mockito.verify(userRestClientMock, Mockito.never()).getPii(userIdTest, TEST_API_KEY);
    }

    private Map<String, User> retrieveCache() {
        Object cacheBefore = ReflectionUtils.getField(userCacheField, userService);
        Assertions.assertNotNull(cacheBefore);
        //noinspection unchecked
        return (Map<String, User>) cacheBefore;
    }
}