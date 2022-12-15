package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.OnboardingRankingRequestDTO;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.function.BiFunction;

@Service
public class OnboardingRankingRequestsDTO2ModelMapper implements BiFunction<OnboardingRankingRequestDTO, InitiativeConfig, OnboardingRankingRequests> {
    @Override
    public OnboardingRankingRequests apply(OnboardingRankingRequestDTO onboardingRankingRequestDTO, InitiativeConfig initiativeConfig) {
        OnboardingRankingRequests out = new OnboardingRankingRequests();
        out.setId(buildId(onboardingRankingRequestDTO));
        out.setUserId(onboardingRankingRequestDTO.getUserId());
        out.setInitiativeId(onboardingRankingRequestDTO.getInitiativeId());
        out.setOrganizationId(onboardingRankingRequestDTO.getOrganizationId());
        out.setAdmissibilityCheckDate(onboardingRankingRequestDTO.getAdmissibilityCheckDate());
        out.setCriteriaConsensusTimestamp(onboardingRankingRequestDTO.getCriteriaConsensusTimestamp());
        out.setRankingValue2Show(onboardingRankingRequestDTO.getRankingValue());

        // when onboarding KO, it will store the lowest precedence value
        if(onboardingRankingRequestDTO.isOnboardingKo()){
            out.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.ONBOARDING_KO);
            out.setRankingValue(
                    !CollectionUtils.isEmpty(initiativeConfig.getRankingFields()) && Sort.Direction.ASC.equals(initiativeConfig.getRankingFields().get(0).getDirection())
                    ? Long.MAX_VALUE
                    : -1);
        } else {
            out.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.TO_NOTIFY);
            out.setRankingValue(onboardingRankingRequestDTO.getRankingValue());
        }

        out.setRankingValueOriginal(out.getRankingValue());

        return out;
    }

    public static String buildId(OnboardingRankingRequestDTO onboardingRankingRequestDTO) {
        return onboardingRankingRequestDTO.getUserId()
                .concat(onboardingRankingRequestDTO.getInitiativeId());
    }
}
