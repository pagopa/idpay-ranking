package it.gov.pagopa.idpay.ranking.service;

import it.gov.pagopa.idpay.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.idpay.ranking.repository.OnboardingRankingRequestsRepository;
import org.springframework.stereotype.Service;

@Service
public class OnboardingRankingRequestsServiceImpl implements OnboardingRankingRequestsService {
    private final OnboardingRankingRequestsRepository onboardingRankingRequestsRepository;

    public OnboardingRankingRequestsServiceImpl(OnboardingRankingRequestsRepository onboardingRankingRequestsRepository) {
        this.onboardingRankingRequestsRepository = onboardingRankingRequestsRepository;
    }

    @Override
    public OnboardingRankingRequests save(OnboardingRankingRequests onboardingRankingRequests) {
        return onboardingRankingRequestsRepository.save(onboardingRankingRequests);
    }
}
