package it.gov.pagopa.ranking.service;

import it.gov.pagopa.ranking.dto.event.EvaluationDTO;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequest2EvaluationMapper;
import it.gov.pagopa.ranking.event.producer.OnboardingNotifierProducer;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OnboardingNotifierServiceImpl implements OnboardingNotifierService {

    private final OnboardingNotifierProducer onboardingNotifierProducer;
    private final OnboardingRankingRequest2EvaluationMapper onboardingRankingRequest2EvaluationMapper;

    public OnboardingNotifierServiceImpl(
            OnboardingNotifierProducer onboardingNotifierProducer,
            OnboardingRankingRequest2EvaluationMapper onboardingRankingRequest2EvaluationMapper) {
        this.onboardingNotifierProducer = onboardingNotifierProducer;
        this.onboardingRankingRequest2EvaluationMapper = onboardingRankingRequest2EvaluationMapper;
    }

    @Async
    @Override
    public void callOnboardingNotifier(OnboardingRankingRequests onboardingRankingRequests) {
        EvaluationDTO evaluationDTO = onboardingRankingRequest2EvaluationMapper.apply(onboardingRankingRequests);
        log.info("[NOTIFY_CITIZEN] - notifying onboarding request to onboarding outcome topic: {}", evaluationDTO);
        try {
            if (!onboardingNotifierProducer.notify(evaluationDTO)) {
                throw new IllegalStateException("[ONBOARDING_NOTIFIER] Something gone wrong while onboarding notify");
            }
        } catch (Exception e) {
            log.error(String.format("[UNEXPECTED_ONBOARDING_NOTIFIER_PROCESSOR_ERROR] Unexpected error occurred publishing onboarding ranking result: %s", evaluationDTO), e);
        }
    }
}
