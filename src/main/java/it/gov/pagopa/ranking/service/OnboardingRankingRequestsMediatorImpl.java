package it.gov.pagopa.ranking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.mongodb.MongoException;
import it.gov.pagopa.ranking.dto.OnboardingRankingRequestDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequestsDTO2ModelMapper;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;


@Service
@Slf4j
public class OnboardingRankingRequestsMediatorImpl extends BaseKafkaConsumer<OnboardingRankingRequestDTO> implements OnboardingRankingRequestsMediator{
    private final OnboardingRankingRequestsService onboardingRankingRequestsService;
    private final ErrorNotifierService errorNotifierService;
    private final OnboardingRankingRequestsDTO2ModelMapper onboardingRankingRequestsDTO2ModelMapper;
    private final ObjectReader objectReader;

    public OnboardingRankingRequestsMediatorImpl(@Value("${spring.application.name}")String applicationName,
                                                 OnboardingRankingRequestsService onboardingRankingRequestsService,
                                                 ErrorNotifierService errorNotifierService,
                                                 OnboardingRankingRequestsDTO2ModelMapper onboardingRankingRequestsDTO2ModelMapper,
                                                 ObjectMapper objectMapper) {
        super(applicationName);

        this.onboardingRankingRequestsService = onboardingRankingRequestsService;
        this.errorNotifierService = errorNotifierService;
        this.onboardingRankingRequestsDTO2ModelMapper = onboardingRankingRequestsDTO2ModelMapper;
        this.objectReader = objectMapper.readerFor(OnboardingRankingRequestDTO.class);
    }

    public void execute(OnboardingRankingRequestDTO onboardingRankingRequestDTO, Message<String> message) {
        try{
            OnboardingRankingRequests onboardingRankingRequests = onboardingRankingRequestsDTO2ModelMapper.apply(onboardingRankingRequestDTO);
            onboardingRankingRequestsService.save(onboardingRankingRequests);
        } catch (MongoException e){
            errorNotifierService.notifyRanking(message, "[ONBOARDING_RANKING_REQUEST] An error occurred handling onboarding ranking request", true, e);
        }
    }

    @Override
    protected ObjectReader getObjectReader() {
        return this.objectReader;
    }

    @Override
    protected Consumer<Throwable> onDeserializationError(Message<String> message) {
        return e -> errorNotifierService.notifyRanking(message, "[ONBOARDING_RANKING_REQUEST] Unexpected JSON", true, e);
    }
}
