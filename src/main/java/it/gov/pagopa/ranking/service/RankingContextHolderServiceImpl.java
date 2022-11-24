package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.repository.InitiativeConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RankingContextHolderServiceImpl implements RankingContextHolderService{
    private final InitiativeConfigRepository initiativeConfigRepository;
    private final Map<String, InitiativeConfig> initiativeId2Config=new ConcurrentHashMap<>();

    public RankingContextHolderServiceImpl(InitiativeConfigRepository initiativeConfigRepository) {
        this.initiativeConfigRepository = initiativeConfigRepository;
    }

    @Override
    public InitiativeConfig getInitiativeConfig(String initiativeId) {
        return initiativeId2Config.computeIfAbsent(initiativeId, this::retrieveInitiativeConfig);
    }

    @Override
    public InitiativeConfig getInitiativeConfig(String initiativeId, String organizationId) {
        InitiativeConfig initiativeConfigRetrieved = getInitiativeConfig(initiativeId);
        return initiativeConfigRetrieved.getOrganizationId().equals(organizationId) ? initiativeConfigRetrieved : null;
    }

    @Override
    public void setInitiativeConfig(InitiativeConfig initiativeConfig) {
        initiativeId2Config.put(initiativeConfig.getInitiativeId(),initiativeConfig);
    }

    private InitiativeConfig retrieveInitiativeConfig(String initiativeId) {
        log.debug("[CACHE_MISS] Cannot find locally initiativeId {}", initiativeId);
        long startTime = System.currentTimeMillis();
        InitiativeConfig initiativeConfig = initiativeConfigRepository.findById(initiativeId).orElse(null);
        log.info("[CACHE_MISS] [PERFORMANCE_LOG] Time spent fetching initiativeId: {} ms", System.currentTimeMillis() - startTime);
        if (initiativeConfig==null){
            log.error("[ONBOARDING_CONTEXT] cannot find initiative having id %s".formatted(initiativeId));
            return null;
        }
        return initiativeConfig;
    }
}
