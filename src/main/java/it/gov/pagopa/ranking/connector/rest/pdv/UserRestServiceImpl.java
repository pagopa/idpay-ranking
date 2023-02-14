package it.gov.pagopa.ranking.connector.rest.pdv;

import it.gov.pagopa.ranking.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UserRestServiceImpl implements UserRestService{

    private final String apiKey;
    private final UserRestClient userClient;

    private final Map<String, User> userCache = new ConcurrentHashMap<>();

    public UserRestServiceImpl(@Value("${app.pdv.headers.x-api-key}") String apiKey, UserRestClient userClient) {
        this.apiKey = apiKey;
        this.userClient = userClient;
    }

    @Override
    public User getUser(String userId) {
       User userFromCache = userCache.get(userId);

       if (userFromCache != null) {
           return userFromCache;
       } else {
           log.debug("[CACHE_MISS] Cannot locally find user with id {}", userId);
           long startTime = System.currentTimeMillis();

           User user = User.builder()
                   .fiscalCode(
                           userClient.getPii(userId, apiKey).getPii()
                   ).build();

           log.info("[PERFORMANCE_LOG] [PDV_INTEGRATION] Time occurred to call pdv {} ms", System.currentTimeMillis() - startTime);

           userCache.put(userId, user);
           log.debug("Added user info of user {} to local map", userId);

           return user;
       }
    }
}
