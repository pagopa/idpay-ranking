package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.dto.initiative.InitiativeBuildDTO;
import it.gov.pagopa.ranking.dto.initiative.InitiativeGeneralDTO;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.RankingStatusEnum;
import it.gov.pagopa.ranking.test.fakers.Initiative2BuildDTOFaker;
import it.gov.pagopa.ranking.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

class InitiativeBuild2ConfigMapperTest {

    @Test
    void apply() {
        // Given
        InitiativeBuild2ConfigMapper initiativeBuild2ConfigMapper = new InitiativeBuild2ConfigMapper();

        InitiativeBuildDTO initiativeBuildDTO = Initiative2BuildDTOFaker.mockInstance(1);
        // When
        InitiativeConfig result = initiativeBuild2ConfigMapper.apply(initiativeBuildDTO);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(initiativeBuildDTO.getInitiativeId(), result.getInitiativeId());
        Assertions.assertEquals(initiativeBuildDTO.getInitiativeName(), result.getInitiativeName());
        Assertions.assertEquals(initiativeBuildDTO.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(initiativeBuildDTO.getStatus(), result.getInitiativeStatus());
        Assertions.assertEquals(initiativeBuildDTO.getGeneral().getRankingStartDate(), result.getRankingStartDate());
        Assertions.assertEquals(initiativeBuildDTO.getGeneral().getRankingEndDate(), result.getRankingEndDate());
        Assertions.assertEquals(initiativeBuildDTO.getGeneral().getBudget(), result.getInitiativeBudget());
        Assertions.assertEquals(initiativeBuildDTO.getGeneral().getBeneficiaryBudget(), result.getBeneficiaryInitiativeBudget());
        Assertions.assertEquals(RankingStatusEnum.RANKING_STATUS_WAITING_END, result.getRankingStatus());
        Assertions.assertEquals(InitiativeBuild2ConfigMapper.calculateSize(initiativeBuildDTO), result.getSize());
        Assertions.assertEquals(InitiativeBuild2ConfigMapper.retrieveRankingFieldCodes(initiativeBuildDTO.getBeneficiaryRule().getAutomatedCriteria()), result.getRankingFields());

        TestUtils.checkNotNullFields(result);
    }

    @Test
    void applyErrorCalculateSize() {
        // Given
        InitiativeBuild2ConfigMapper initiativeBuild2ConfigMapper = new InitiativeBuild2ConfigMapper();

        InitiativeBuildDTO initiativeBuildDTO = Initiative2BuildDTOFaker.mockInstance(1);

        LocalDate now = LocalDate.now();
        InitiativeGeneralDTO initiativeGeneralDTO = InitiativeGeneralDTO.builder()
                .rankingEnabled(Boolean.TRUE)
                .beneficiaryBudget(BigDecimal.TEN)
                .rankingStartDate(now)
                .rankingEndDate(now.plusMonths(7L))
                .build();

        initiativeBuildDTO.setGeneral(initiativeGeneralDTO);

        // When
        try {
            initiativeBuild2ConfigMapper.apply(initiativeBuildDTO);
        }catch (Throwable e){
            Assertions.assertTrue(e instanceof IllegalStateException);
        }

    }
}