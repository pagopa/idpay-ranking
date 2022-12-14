package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.exception.ClientExceptionNoBody;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.service.initiative.InitiativeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RankingContextHolderServiceImpl implements RankingContextHolderService{
    private final InitiativeConfigService initiativeConfigService;
    private final Map<String, InitiativeConfig> initiativeId2Config=new ConcurrentHashMap<>();

    public RankingContextHolderServiceImpl(InitiativeConfigService initiativeConfigService) {
        this.initiativeConfigService = initiativeConfigService;
    }

    @Override
    public InitiativeConfig getInitiativeConfig(String initiativeId, String organizationId) {
        InitiativeConfig initiativeConfigRetrieved = initiativeId2Config.computeIfAbsent(initiativeId, this::retrieveInitiativeConfig);
        if(initiativeConfigRetrieved.getOrganizationId().equals(organizationId)){
            return initiativeConfigRetrieved;
        } else {
            log.info("The initiative {} does not related with organization {}", initiativeId, organizationId);
            throw new ClientExceptionNoBody(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void setInitiativeConfig(InitiativeConfig initiativeConfig) {
        InitiativeConfig initiativeSaved = initiativeConfigService.save(initiativeConfig);
        initiativeId2Config.put(initiativeSaved.getInitiativeId(),initiativeSaved);
    }

    private InitiativeConfig retrieveInitiativeConfig(String initiativeId) {
        log.debug("[CACHE_MISS] Cannot find locally initiativeId {}", initiativeId);
        long startTime = System.currentTimeMillis();
        InitiativeConfig initiativeConfig = initiativeConfigService.findByIdOptional(initiativeId).orElseThrow(() -> {
            log.error("[RANKING_CONTEXT] cannot find initiative having id %s".formatted(initiativeId));
            return new ClientExceptionNoBody(HttpStatus.NOT_FOUND);
        });
        log.info("[CACHE_MISS] [PERFORMANCE_LOG] Time spent fetching initiativeId: {} ms", System.currentTimeMillis() - startTime);
        return initiativeConfig;
    }
}
