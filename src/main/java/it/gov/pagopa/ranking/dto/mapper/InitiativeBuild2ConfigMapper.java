package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.initiative.AutomatedCriteriaDTO;
import it.gov.pagopa.ranking.dto.initiative.InitiativeAdditionalInfoDTO;
import it.gov.pagopa.ranking.dto.initiative.InitiativeBuildDTO;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.Order;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class InitiativeBuild2ConfigMapper implements Function<InitiativeBuildDTO, InitiativeConfig> {
    @Override
    public InitiativeConfig apply(InitiativeBuildDTO initiativeBuildDTO) {
        List<AutomatedCriteriaDTO> automatedCriteriaList = initiativeBuildDTO.getBeneficiaryRule().getAutomatedCriteria();
        InitiativeAdditionalInfoDTO additionalInfo = initiativeBuildDTO.getAdditionalInfo();

        return InitiativeConfig.builder()
                .initiativeId(initiativeBuildDTO.getInitiativeId())
                .initiativeName(initiativeBuildDTO.getInitiativeName())
                .initiativeEndDate(initiativeBuildDTO.getGeneral().getEndDate())
                .initiativeRewardType(initiativeBuildDTO.getInitiativeRewardType())
                .organizationId(initiativeBuildDTO.getOrganizationId())
                .organizationName(initiativeBuildDTO.getOrganizationName())
                .initiativeStatus(initiativeBuildDTO.getStatus())
                .rankingStartDate(initiativeBuildDTO.getGeneral().getRankingStartDate())
                .rankingEndDate(initiativeBuildDTO.getGeneral().getRankingEndDate())
                .initiativeBudget(initiativeBuildDTO.getGeneral().getBudget())
                .beneficiaryInitiativeBudget(initiativeBuildDTO.getGeneral().getBeneficiaryBudget())
                .rankingStatus(RankingStatus.WAITING_END)
                .size(calculateSize(initiativeBuildDTO))
                .rankingFields(retrieveRankingFieldCodes(automatedCriteriaList))
                .isLogoPresent((additionalInfo != null && additionalInfo.getLogoFileName() != null) ? Boolean.TRUE : Boolean.FALSE)
                .build();
    }

    public static long calculateSize(InitiativeBuildDTO initiativeBuildDTO) {
        try {
            Long totalBudget = Utils.euro2Cents(initiativeBuildDTO.getGeneral().getBudget());
            Long beneficiaryBudget = Utils.euro2Cents(initiativeBuildDTO.getGeneral().getBeneficiaryBudget());

            return totalBudget/beneficiaryBudget;
        }catch (NullPointerException e){
            log.error("Something gone wrong calculate ranking size in the initiative {}", initiativeBuildDTO);
            throw new IllegalStateException("Something gone wrong calculate ranking size");
        }
    }

    public static List<Order> retrieveRankingFieldCodes(List<AutomatedCriteriaDTO> automatedCriteriaList) {
        return automatedCriteriaList != null ? automatedCriteriaList
                .stream().filter(item -> item.getOrderDirection()!= null)
                .map(automatedCriteria-> Order.builder()
                        .fieldCode(automatedCriteria.getCode())
                        .direction(automatedCriteria.getOrderDirection())
                        .build())
                .toList()
                : Collections.emptyList();
    }
}
