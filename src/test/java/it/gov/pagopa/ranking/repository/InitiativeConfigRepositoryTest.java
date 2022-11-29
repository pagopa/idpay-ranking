package it.gov.pagopa.ranking.repository;

import it.gov.pagopa.ranking.BaseIntegrationTest;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

class InitiativeConfigRepositoryTest extends BaseIntegrationTest {
    private final String initiativeId = "initiativeid_test_%d";
    int initiativeBeforeDate = 10;
    int initiativeEqualsDate = 15;
    int initiativeAfterDate = 20;
    @Autowired
    private InitiativeConfigRepository initiativeConfigRepository;

    @AfterEach
    void clearData(){
        IntStream.range(0, initiativeBeforeDate+initiativeEqualsDate+initiativeAfterDate)
                .forEach(i -> initiativeConfigRepository.deleteById(initiativeId.formatted(i)));
    }

    @Test
    void findByRankingEndDateBefore() {
        // Given
        LocalDate nowDate = LocalDate.now();

        List<InitiativeConfig> initiativeConfigList = new ArrayList<>(initiativeBeforeDate + initiativeEqualsDate + initiativeAfterDate);
        List<InitiativeConfig> initiativeBeforeDateList  = buildInitiative(0, initiativeBeforeDate, nowDate.minusDays(9L));

        initiativeConfigList.addAll(initiativeBeforeDateList);
        initiativeConfigList.addAll(buildInitiative(initiativeBeforeDate, initiativeEqualsDate, nowDate));
        initiativeConfigList.addAll(buildInitiative(initiativeBeforeDate+initiativeEqualsDate, initiativeAfterDate, nowDate.plusDays(2L)));

        List<InitiativeConfig> initiativeConfigListSaved = initiativeConfigRepository.saveAll(initiativeConfigList);
        Assertions.assertEquals(initiativeBeforeDate + initiativeEqualsDate + initiativeAfterDate,initiativeConfigListSaved.size());

        // When
        List<InitiativeConfig> result = initiativeConfigRepository.findByRankingEndDateBeforeAndRankingStatus(nowDate, RankingStatus.WAITING_END);

        //Then
        Assertions.assertEquals(initiativeBeforeDate/2, result.size());
        List<InitiativeConfig> initiativeListExpected = initiativeBeforeDateList.stream().filter(i -> i.getRankingStatus().equals(RankingStatus.WAITING_END)).toList();
        Assertions.assertEquals(initiativeListExpected, result);
    }

    private List<InitiativeConfig> buildInitiative(int bias, int n, LocalDate rankingEndDate){
        return IntStream.range(bias, bias+n)
                .mapToObj(i -> InitiativeConfigFaker.mockInstanceBuilder(i)
                        .initiativeId(initiativeId.formatted(i))
                        .rankingEndDate(rankingEndDate)
                        .rankingStatus(i % 2 ==0 ? RankingStatus.WAITING_END : RankingStatus.READY)
                        .build())
                .toList();
    }
}