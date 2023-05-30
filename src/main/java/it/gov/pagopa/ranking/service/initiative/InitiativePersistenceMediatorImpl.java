package it.gov.pagopa.ranking.service.initiative;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.mongodb.MongoException;
import it.gov.pagopa.ranking.dto.initiative.InitiativeBuildDTO;
import it.gov.pagopa.ranking.dto.mapper.InitiativeBuild2ConfigMapper;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.service.BaseKafkaConsumer;
import it.gov.pagopa.ranking.service.RankingErrorNotifierService;
import it.gov.pagopa.ranking.service.RankingContextHolderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Slf4j
public class InitiativePersistenceMediatorImpl extends BaseKafkaConsumer<InitiativeBuildDTO> implements InitiativePersistenceMediator {
    private final InitiativeBuild2ConfigMapper initiativeBuild2ConfigMapper;
    private final InitiativeConfigService initiativeConfigService;
    private final RankingContextHolderService rankingContextHolderService;
    private final RankingErrorNotifierService rankingErrorNotifierService;
    private final ObjectReader objectReader;

    public InitiativePersistenceMediatorImpl(@Value("${spring.application.name}")String applicationName,
                                             InitiativeBuild2ConfigMapper initiativeBuild2ConfigMapper,
                                             InitiativeConfigService initiativeConfigService,
                                             RankingContextHolderService rankingContextHolderService,
                                             RankingErrorNotifierService rankingErrorNotifierService,

                                             ObjectMapper objectMapper){
        super(applicationName);
        this.initiativeBuild2ConfigMapper = initiativeBuild2ConfigMapper;
        this.initiativeConfigService = initiativeConfigService;
        this.rankingContextHolderService = rankingContextHolderService;
        this.rankingErrorNotifierService = rankingErrorNotifierService;

        this.objectReader = objectMapper.readerFor(InitiativeBuildDTO.class);

    }
    @Override
    protected ObjectReader getObjectReader() {
        return this.objectReader;
    }

    @Override
    protected Consumer<Throwable> onDeserializationError(Message<String> message) {
        return e -> rankingErrorNotifierService.notifyInitiativeBuild(message, "[INITIATIVE_RANKING] Unexpected JSON", true, e);
    }

    @Override
    protected void execute(InitiativeBuildDTO payload, Message<String> message) {
        if(payload.getGeneral() != null && payload.getGeneral().isRankingEnabled()) {
            try {
                InitiativeConfig initiativeRetrieved = initiativeConfigService.findById(payload.getInitiativeId());
                if(initiativeRetrieved == null
                        || initiativeRetrieved.getRankingStatus().equals(RankingStatus.WAITING_END)){

                    InitiativeConfig initiativeConfig = initiativeBuild2ConfigMapper.apply(payload);

                    rankingContextHolderService.setInitiativeConfig(initiativeConfig);
                }else {
                   log.error("The initiative is in the ending phase: {}", payload);
                }
            }catch (MongoException | IllegalStateException e){
                rankingErrorNotifierService.notifyInitiativeBuild(message, "[INITIATIVE_RANKING] An error occurred handling initiative ranking build", true, e);
            }
        }
    }
}
