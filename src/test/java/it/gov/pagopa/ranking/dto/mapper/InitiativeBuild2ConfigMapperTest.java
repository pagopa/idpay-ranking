package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.common.utils.TestUtils;
import it.gov.pagopa.ranking.dto.initiative.InitiativeBuildDTO;
import it.gov.pagopa.ranking.dto.initiative.InitiativeGeneralDTO;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.test.fakers.Initiative2BuildDTOFaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertEquals(initiativeBuildDTO.getOrganizationName(), result.getOrganizationName());
        Assertions.assertEquals(initiativeBuildDTO.getStatus(), result.getInitiativeStatus());
        Assertions.assertEquals(initiativeBuildDTO.getGeneral().getRankingStartDate(), result.getRankingStartDate());
        Assertions.assertEquals(initiativeBuildDTO.getGeneral().getRankingEndDate(), result.getRankingEndDate());
        Assertions.assertEquals(initiativeBuildDTO.getGeneral().getBudgetCents(), result.getInitiativeBudgetCents());
        Assertions.assertEquals(initiativeBuildDTO.getGeneral().getBeneficiaryBudgetCents(), result.getBeneficiaryInitiativeBudgetCents());
        Assertions.assertEquals(RankingStatus.WAITING_END, result.getRankingStatus());
        Assertions.assertEquals(InitiativeBuild2ConfigMapper.calculateSize(initiativeBuildDTO), result.getSize());
        Assertions.assertEquals(InitiativeBuild2ConfigMapper.retrieveRankingFieldCodes(initiativeBuildDTO.getBeneficiaryRule().getAutomatedCriteria()), result.getRankingFields());
        Assertions.assertTrue(result.getIsLogoPresent());

        TestUtils.checkNotNullFields(result,"rankingFilePath", "rankingPublishedTimestamp", "rankingGeneratedTimestamp");
    }

    @Test
    void applyLogoNotPresent() {
        // Given
        InitiativeBuild2ConfigMapper initiativeBuild2ConfigMapper = new InitiativeBuild2ConfigMapper();

        InitiativeBuildDTO initiativeBuildDTO = Initiative2BuildDTOFaker.mockInstance(1);
        initiativeBuildDTO.getAdditionalInfo().setLogoFileName("");
        // When
        InitiativeConfig result = initiativeBuild2ConfigMapper.apply(initiativeBuildDTO);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getIsLogoPresent());

        TestUtils.checkNotNullFields(result,"rankingFilePath", "rankingPublishedTimestamp", "rankingGeneratedTimestamp");
    }

    @Test
    void applyErrorCalculateSize() {
        // Given
        InitiativeBuild2ConfigMapper initiativeBuild2ConfigMapper = new InitiativeBuild2ConfigMapper();

        InitiativeBuildDTO initiativeBuildDTO = Initiative2BuildDTOFaker.mockInstance(1);

        LocalDate now = LocalDate.now();
        InitiativeGeneralDTO initiativeGeneralDTO = InitiativeGeneralDTO.builder()
                .rankingEnabled(Boolean.TRUE)
                .beneficiaryBudgetCents(10L)
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