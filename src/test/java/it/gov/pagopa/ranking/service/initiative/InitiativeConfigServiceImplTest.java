package it.gov.pagopa.ranking.service.initiative;

import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.Order;
import it.gov.pagopa.ranking.model.RankingStatusEnum;
import it.gov.pagopa.ranking.repository.InitiativeConfigRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

class InitiativeConfigServiceImplTest {

    @Test
    void save() {
        //Given
        InitiativeConfigRepository initiativeConfigRepositoryMock = Mockito.mock(InitiativeConfigRepository.class);

        InitiativeConfigService initiativeConfigService = new InitiativeConfigServiceImpl(initiativeConfigRepositoryMock);

        Mockito.when(initiativeConfigRepositoryMock.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);

        LocalDate now = LocalDate.now();
        InitiativeConfig request = InitiativeConfig.builder()
                .initiativeId("INITIATIVEID")
                .initiativeName("INITIATIVENAME")
                .organizationId("ORGANIZATIONID")
                .initiativeStatus("STATUS")
                .rankingStartDate(now)
                .rankingEndDate(now.plusMonths(7L))
                .initiativeBudget(BigDecimal.TEN)
                .beneficiaryInitiativeBudget(BigDecimal.ONE)
                .rankingStatus(RankingStatusEnum.RANKING_STATUS_WAITING_END)
                .size(10)
                .rankingFields(List.of(
                        Order.builder().fieldCode("ISEE").direction(Sort.Direction.ASC).build()))
                .build();

        // When
        InitiativeConfig result = initiativeConfigService.save(request);

        // Then
        Assertions.assertNotNull(result);

        Assertions.assertEquals(request.getInitiativeId(), result.getInitiativeId());
        Assertions.assertEquals(request.getInitiativeName(), result.getInitiativeName());
        Assertions.assertEquals(request.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(request.getInitiativeStatus(), result.getInitiativeStatus());
        Assertions.assertEquals(request.getRankingStartDate(), result.getRankingStartDate());
        Assertions.assertEquals(request.getRankingEndDate(), result.getRankingEndDate());
        Assertions.assertEquals(request.getInitiativeBudget(), result.getInitiativeBudget());
        Assertions.assertEquals(request.getBeneficiaryInitiativeBudget(), result.getBeneficiaryInitiativeBudget());
        Assertions.assertEquals(request.getRankingStatus(), result.getRankingStatus());
        Assertions.assertEquals(request.getRankingFields(), result.getRankingFields());

        Mockito.verify(initiativeConfigRepositoryMock).save(request);
    }
}