package it.gov.pagopa.ranking.repository;

import it.gov.pagopa.ranking.BaseIntegrationTest;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.RankingStatus;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@TestPropertySource(properties = {
        "logging.level.it.gov.pagopa.ranking.repository.InitiativeConfigRepository=WARN",
})
class InitiativeConfigRepositoryTest extends BaseIntegrationTest {
    private final String initiativeId = "initiativeid_test_%d";
    private final int initiativeBeforeStartInterval = 10;
    private final int initiativeEqualsStartDate = 20;
    private final int initiativeBetweenStartAndEndDate = 30;
    private final int initiativeEqualsEndDate = 40;
    private final int initiativeAfterEndInterval = 50;
    @Autowired
    private InitiativeConfigRepository initiativeConfigRepository;

    @AfterEach
    void clearData(){
        IntStream.range(0, initiativeBeforeStartInterval + initiativeEqualsStartDate + initiativeBetweenStartAndEndDate + initiativeEqualsEndDate + initiativeAfterEndInterval)
                .forEach(i -> initiativeConfigRepository.deleteById(initiativeId.formatted(i)));
    }

    @Test
    void findByRankingEndDateBefore() {
        // Given
        int dayBefore = 7;
        LocalDate nowDate = LocalDate.now();

        List<InitiativeConfig> initiativeConfigList = new ArrayList<>(initiativeBeforeStartInterval + initiativeEqualsStartDate + initiativeBetweenStartAndEndDate + initiativeEqualsEndDate + initiativeAfterEndInterval);
        // initiative rankingEndDate before start interval
        initiativeConfigList.addAll(buildInitiative(0, initiativeBeforeStartInterval, nowDate.minusDays(9L)));

        // initiative rankingEndDate equals start interval
        List<InitiativeConfig> initiativeStartInterval = buildInitiative(initiativeBeforeStartInterval, initiativeEqualsStartDate, nowDate.minusDays(dayBefore));
        initiativeConfigList.addAll(initiativeStartInterval);

        // initiative rankingEndDate initiativeBetweenStartAndEndDate interval
        List<InitiativeConfig> initiativeBetweenInterval = buildInitiative(initiativeBeforeStartInterval + initiativeEqualsStartDate, initiativeBetweenStartAndEndDate, nowDate.minusDays(3L));
        initiativeConfigList.addAll(initiativeBetweenInterval);

        // initiative rankingEndDate equals end interval
        initiativeConfigList.addAll(buildInitiative(initiativeBeforeStartInterval + initiativeEqualsStartDate + initiativeBetweenStartAndEndDate, initiativeEqualsEndDate, nowDate));

        // initiative rankingEndDate after end interval
        initiativeConfigList.addAll(buildInitiative(initiativeBeforeStartInterval + initiativeEqualsStartDate + initiativeBetweenStartAndEndDate + initiativeEqualsEndDate, initiativeAfterEndInterval, nowDate.plusDays(2L)));

        List<InitiativeConfig> initiativeConfigListSaved = initiativeConfigRepository.saveAll(initiativeConfigList);
        Assertions.assertEquals(initiativeBeforeStartInterval + initiativeEqualsStartDate + initiativeBetweenStartAndEndDate + initiativeEqualsEndDate + initiativeAfterEndInterval, initiativeConfigListSaved.size());

        // When
        List<InitiativeConfig> result = initiativeConfigRepository.findByRankingStatusAndRankingEndDateBetween(RankingStatus.WAITING_END, nowDate.minusDays(dayBefore +1),nowDate);

        //Then
        List<InitiativeConfig> resultTest = result.stream().filter(initiativeConfig -> initiativeConfig.getInitiativeId().matches("initiativeid_test_[0-9]+")).toList();
        Assertions.assertEquals((initiativeEqualsStartDate / 2) + (initiativeBetweenStartAndEndDate / 2), resultTest.size());

        List<InitiativeConfig> initiativeStartExpected = initiativeStartInterval.stream().filter(i -> i.getRankingStatus().equals(RankingStatus.WAITING_END)).toList();
        initiativeStartExpected.forEach(System.out::println);
        Assertions.assertTrue(result.containsAll(initiativeStartExpected), "result list" + result+ " elements expected" + initiativeStartExpected);

        List<InitiativeConfig> initiativeBetweenExpected = initiativeBetweenInterval.stream().filter(i -> i.getRankingStatus().equals(RankingStatus.WAITING_END)).toList();
        Assertions.assertTrue(result.containsAll(initiativeBetweenExpected), "result list" + result+ " elements expected" + initiativeBetweenExpected);
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