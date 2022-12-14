package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.event.EvaluationRankingDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequest2EvaluationMapper;
import it.gov.pagopa.ranking.event.producer.OnboardingNotifierProducer;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.service.initiative.InitiativeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class OnboardingNotifierServiceImpl implements OnboardingNotifierService {

    private final OnboardingNotifierProducer onboardingNotifierProducer;
    private final OnboardingRankingRequest2EvaluationMapper onboardingRankingRequest2EvaluationMapper;
    private final InitiativeConfigService initiativeConfigService;

    public OnboardingNotifierServiceImpl(
            OnboardingNotifierProducer onboardingNotifierProducer,
            OnboardingRankingRequest2EvaluationMapper onboardingRankingRequest2EvaluationMapper,
            InitiativeConfigService initiativeConfigService
            ) {
        this.onboardingNotifierProducer = onboardingNotifierProducer;
        this.onboardingRankingRequest2EvaluationMapper = onboardingRankingRequest2EvaluationMapper;
        this.initiativeConfigService = initiativeConfigService;
    }

    @Override
    @Async
    public void callOnboardingNotifier(InitiativeConfig initiative, List<OnboardingRankingRequests> onboardingRankingRequests) {
        initiative.setRankingStatus(RankingStatus.PUBLISHING);
        initiativeConfigService.save(initiative);
        log.info("[NOTIFY_CITIZEN] - onboarding_ranking_rule saved with Ranking status: {}", initiative.getRankingStatus());
        onboardingRankingRequests.forEach(onboardingRankingRequest -> {
            EvaluationRankingDTO evaluationDTO = onboardingRankingRequest2EvaluationMapper.apply(onboardingRankingRequest);
            log.debug("[NOTIFY_CITIZEN] - notifying onboarding request to onboarding outcome topic: {}", evaluationDTO);
            try {
                if (!onboardingNotifierProducer.notify(evaluationDTO)) {
                    throw new IllegalStateException("[ONBOARDING_NOTIFIER] Something gone wrong while onboarding notify");
                }
            } catch (Exception e) {
                log.error(String.format("[UNEXPECTED_ONBOARDING_NOTIFIER_PROCESSOR_ERROR] Unexpected error occurred publishing onboarding ranking result: %s", evaluationDTO), e);
            }
        }); //Check if parallel is needed and calculate Numb of Threads necessary
        initiative.setRankingPublishedTimestamp(LocalDateTime.now());
        initiative.setRankingStatus(RankingStatus.COMPLETED);
        initiativeConfigService.save(initiative); //TODO need to send the modification to someone?
        log.info("[NOTIFY_CITIZEN] - onboarding_ranking_rule saved with Ranking status: {}", initiative.getRankingStatus());
    }
}
