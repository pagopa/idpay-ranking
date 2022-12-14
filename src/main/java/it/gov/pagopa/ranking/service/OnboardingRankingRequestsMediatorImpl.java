package it.gov.pagopa.ranking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.mongodb.MongoException;
import it.gov.pagopa.ranking.dto.OnboardingRankingRequestDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequestsDTO2ModelMapper;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.repository.InitiativeConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;


@Service
@Slf4j
public class OnboardingRankingRequestsMediatorImpl extends BaseKafkaConsumer<OnboardingRankingRequestDTO> implements OnboardingRankingRequestsMediator{

    private final OnboardingRankingRequestsService onboardingRankingRequestsService;
    private final InitiativeConfigRepository initiativeConfigRepository;
    private final ErrorNotifierService errorNotifierService;
    private final OnboardingRankingRequestsDTO2ModelMapper onboardingRankingRequestsDTO2ModelMapper;

    private final ObjectReader objectReader;

    public OnboardingRankingRequestsMediatorImpl(@Value("${spring.application.name}")String applicationName,
                                                 OnboardingRankingRequestsService onboardingRankingRequestsService,
                                                 InitiativeConfigRepository initiativeConfigRepository, ErrorNotifierService errorNotifierService,
                                                 OnboardingRankingRequestsDTO2ModelMapper onboardingRankingRequestsDTO2ModelMapper,
                                                 ObjectMapper objectMapper) {
        super(applicationName);

        this.onboardingRankingRequestsService = onboardingRankingRequestsService;
        this.initiativeConfigRepository = initiativeConfigRepository;
        this.errorNotifierService = errorNotifierService;
        this.onboardingRankingRequestsDTO2ModelMapper = onboardingRankingRequestsDTO2ModelMapper;
        this.objectReader = objectMapper.readerFor(OnboardingRankingRequestDTO.class);
    }

    public void execute(OnboardingRankingRequestDTO onboardingRankingRequestDTO, Message<String> message) {
        try{
            InitiativeConfig initiative = initiativeConfigRepository.findById(onboardingRankingRequestDTO.getInitiativeId()).orElse(null);
            if(initiative!=null){
                OnboardingRankingRequests onboardingRankingRequests = onboardingRankingRequestsDTO2ModelMapper.apply(onboardingRankingRequestDTO, initiative);
                onboardingRankingRequestsService.save(onboardingRankingRequests);
            } else {
                errorNotifierService.notifyOnboardingRankingRequest(message, "[ONBOARDING_RANKING_REQUEST] The input initiative doesn't exists: %s".formatted(onboardingRankingRequestDTO.getInitiativeId()), true, new IllegalStateException("The input initiative doesn't exists!"));
            }
        } catch (MongoException e){
            errorNotifierService.notifyOnboardingRankingRequest(message, "[ONBOARDING_RANKING_REQUEST] An error occurred handling onboarding ranking request", true, e);
        }
    }

    @Override
    protected ObjectReader getObjectReader() {
        return this.objectReader;
    }

    @Override
    protected Consumer<Throwable> onDeserializationError(Message<String> message) {
        return e -> errorNotifierService.notifyOnboardingRankingRequest(message, "[ONBOARDING_RANKING_REQUEST] Unexpected JSON", true, e);
    }
}
