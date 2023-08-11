package it.gov.pagopa.ranking.service.initiative;

import it.gov.pagopa.common.web.exception.ClientException;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.Order;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.repository.InitiativeConfigRepository;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class InitiativeConfigServiceImplTest {

    @Mock
    private InitiativeConfigRepository initiativeConfigRepositoryMock;
    InitiativeConfigService initiativeConfigService;

    @BeforeEach
    void init() {
        initiativeConfigService = new InitiativeConfigServiceImpl(initiativeConfigRepositoryMock);
    }

    @Test
    void save() {
        //Given
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
                .rankingStatus(RankingStatus.WAITING_END)
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

    @Test
    void testSetDate() {
        LocalDate now = LocalDate.now();

        String initiativeId = "INITIATIVE_ID";
        InitiativeConfig initiative = InitiativeConfigFaker.mockInstanceBuilder(1)
                .initiativeId(initiativeId)
                .rankingEndDate(now.plusDays(7))
                .rankingStatus(RankingStatus.READY)
                .build();

        Mockito.when(initiativeConfigRepositoryMock.findById(initiativeId)).thenReturn(Optional.of(initiative));

        initiativeConfigService.setInitiativeRankingEndDateAndStatusWaitingEnd(initiativeId, now);

        InitiativeConfig expected = initiative.toBuilder()
                .rankingEndDate(now)
                .rankingStatus(RankingStatus.WAITING_END)
                .build();
        Mockito.verify(initiativeConfigRepositoryMock).save(expected);
    }

    @Test
    void testSetDateException() {
        LocalDate now = LocalDate.now();
        String initiativeId = "INITIATIVE_ID";

        Mockito.when(initiativeConfigRepositoryMock.findById(initiativeId)).thenReturn(Optional.empty());

        Executable executable = () -> initiativeConfigService.setInitiativeRankingEndDateAndStatusWaitingEnd(initiativeId, now);

        Assertions.assertThrows(ClientException.class, executable);
        Mockito.verify(initiativeConfigRepositoryMock, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteByInitiativeId() {
        // Given
        LocalDate now = LocalDate.now();
        String initiativeId = "InitiativeId";
        InitiativeConfig initiative = InitiativeConfigFaker.mockInstanceBuilder(1)
                .initiativeId(initiativeId)
                .rankingEndDate(now.plusDays(7))
                .rankingStatus(RankingStatus.READY)
                .build();

        Mockito.when(initiativeConfigRepositoryMock.deleteByInitiativeId(Mockito.any()))
                .thenReturn(Optional.of(initiative));

        // When
        Optional<InitiativeConfig> result =  initiativeConfigService.deleteByInitiativeId(initiativeId);

        // Then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(initiative.getInitiativeId(), result.get().getInitiativeId());
        Assertions.assertEquals(initiative.getRankingEndDate(), result.get().getRankingEndDate());
        Assertions.assertEquals(initiative.getRankingStatus(), result.get().getRankingStatus());
        Mockito.verify(initiativeConfigRepositoryMock, Mockito.times(1)).deleteByInitiativeId(initiativeId);
    }
}