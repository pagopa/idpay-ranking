package it.gov.pagopa.ranking.service;

import org.springframework.messaging.Message;

public interface OnboardingRankingRequestsMediator {
    void execute(Message<String> message);
}
