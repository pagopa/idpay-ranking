package it.gov.pagopa.ranking.service.notify;

import it.gov.pagopa.ranking.constants.OnboardingConstants;
import it.gov.pagopa.ranking.dto.event.EvaluationRankingDTO;
import it.gov.pagopa.ranking.dto.event.OnboardingRejectionReason;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequest2EvaluationMapper;
import it.gov.pagopa.ranking.event.producer.OnboardingNotifierProducer;
import it.gov.pagopa.ranking.event.producer.OnboardingNotifierProducerImpl;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.service.RankingContextHolderService;
import it.gov.pagopa.ranking.service.RankingErrorNotifierService;
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
    private final RankingContextHolderService rankingContextHolderService;
    private final RankingErrorNotifierService rankingErrorNotifierService;

    public OnboardingNotifierServiceImpl(
            OnboardingNotifierProducer onboardingNotifierProducer,
            OnboardingRankingRequest2EvaluationMapper onboardingRankingRequest2EvaluationMapper,
            RankingContextHolderService rankingContextHolderService,
            RankingErrorNotifierService rankingErrorNotifierService) {
        this.onboardingNotifierProducer = onboardingNotifierProducer;
        this.onboardingRankingRequest2EvaluationMapper = onboardingRankingRequest2EvaluationMapper;
        this.rankingContextHolderService = rankingContextHolderService;
        this.rankingErrorNotifierService = rankingErrorNotifierService;
    }

    @Override
    @Async
    public void callOnboardingNotifier(InitiativeConfig initiative, List<OnboardingRankingRequests> onboardingRankingRequests) {
        log.info("[NOTIFY_CITIZEN] - onboarding_ranking_rule saved with Ranking status: {}", initiative.getRankingStatus());
        onboardingRankingRequests.stream().parallel().forEach(onboardingRankingRequest -> {
            EvaluationRankingDTO evaluationDTO = onboardingRankingRequest2EvaluationMapper.apply(onboardingRankingRequest, initiative);
            log.debug("[NOTIFY_CITIZEN] - notifying onboarding request to onboarding outcome topic: {}", evaluationDTO);

            callOnboardingUserNotifier(evaluationDTO);

            inviteFamilyMembers(onboardingRankingRequest, evaluationDTO);
        });
        initiative.setRankingPublishedTimestamp(LocalDateTime.now());
        initiative.setRankingStatus(RankingStatus.COMPLETED);
        rankingContextHolderService.setInitiativeConfig(initiative);
        log.info("[NOTIFY_CITIZEN] - onboarding_ranking_rule saved with Ranking status: {}", initiative.getRankingStatus());
    }

    private void inviteFamilyMembers(OnboardingRankingRequests request,EvaluationRankingDTO evaluation) {
        if(request.getFamilyId()!=null){
            if(BeneficiaryRankingStatus.ELIGIBLE_OK.equals(request.getBeneficiaryRankingStatus())){
                log.info("[NOTIFY_CITIZEN] - notify onboarding demanded to onboarding outcome topic for family {}", request.getFamilyId());
                request.getMemberIds().forEach(userId -> {
                    if(!userId.equals(request.getUserId())){
                        callOnboardingUserNotifier(evaluation.toBuilder()
                                .userId(userId)
                                .status(OnboardingConstants.ONBOARDING_STATUS_DEMANDED)
                                .build());
                    }
                });
            }
            if(BeneficiaryRankingStatus.ELIGIBLE_KO.equals(request.getBeneficiaryRankingStatus())){
                log.info("[NOTIFY_CITIZEN] - notify onboarding KO to onboarding outcome topic for family {}", request.getFamilyId());
                request.getMemberIds().forEach(userId -> {
                    if(!userId.equals(request.getUserId())){
                        callOnboardingUserNotifier(evaluation.toBuilder()
                                .userId(userId)
                                .status(OnboardingConstants.ONBOARDING_STATUS_KO)
                                .onboardingRejectionReasons(
                                        List.of(OnboardingRejectionReason.builder()
                                                .type(OnboardingRejectionReason.OnboardingRejectionReasonType.FAMILY_CRITERIA_KO)
                                                .code(OnboardingConstants.REJECTION_REASON_FAMILY_CRITERIA_FAIL)
                                                .detail("Nucleo familiare non soddisfa i requisiti")
                                                .build()))
                                .build());
                    }
                });
            }
        }
    }

    private void callOnboardingUserNotifier(EvaluationRankingDTO evaluationCompletedDTO) {
        log.info("[ONBOARDING_REQUEST] notifying onboarding request to outcome topic: {}", evaluationCompletedDTO);
        try {
            if (!onboardingNotifierProducer.notify(evaluationCompletedDTO)) {
                throw new IllegalStateException("[ONBOARDING_NOTIFIER] Something gone wrong while onboarding notify");
            }
        } catch (Exception e) {
            log.error(String.format("[UNEXPECTED_ONBOARDING_NOTIFIER_PROCESSOR_ERROR] Unexpected error occurred publishing onboarding ranking result: %s", evaluationCompletedDTO), e);
            rankingErrorNotifierService.notifyRankingOnboardingOutcome(OnboardingNotifierProducerImpl.buildMessage(evaluationCompletedDTO), "[ONBOARDING_REQUEST] An error occurred while publishing the onboarding evaluation result", true, e);
        }
    }
}
