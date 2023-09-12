package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.controller.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.dto.initiative.InitiativeGeneralDTO;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.springframework.stereotype.Service;

@Service
public class OnboardingRankingRequest2RankingRequestsApiDTOMapper {

    public RankingRequestsApiDTO apply(OnboardingRankingRequests onboardingRankingRequests, InitiativeConfig initiativeConfig) {
        RankingRequestsApiDTO out = new RankingRequestsApiDTO();

        out.setUserId(onboardingRankingRequests.getUserId());
        out.setInitiativeId(onboardingRankingRequests.getInitiativeId());
        out.setOrganizationId(onboardingRankingRequests.getOrganizationId());
        out.setAdmissibilityCheckDate(onboardingRankingRequests.getAdmissibilityCheckDate());
        out.setCriteriaConsensusTimestamp(onboardingRankingRequests.getCriteriaConsensusTimestamp());
        out.setRankingValue(onboardingRankingRequests.getRankingValue2Show());
        out.setRanking(onboardingRankingRequests.getRank());
        out.setBeneficiaryRankingStatus(onboardingRankingRequests.getBeneficiaryRankingStatus());

        if(InitiativeGeneralDTO.BeneficiaryTypeEnum.NF.equals(initiativeConfig.getBeneficiaryType())){
            out.setFamilyId(onboardingRankingRequests.getFamilyId());
            out.setMemberIds(onboardingRankingRequests.getMemberIds());
        }

        return out;
    }
}
