package it.gov.pagopa.ranking.service.onboarding;

import org.springframework.messaging.Message;

public interface OnboardingRankingRequestsMediator {
    void execute(Message<String> message);
}
