package it.gov.pagopa.ranking.repository;

import it.gov.pagopa.common.mongo.MongoTest;
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

@MongoTest
class InitiativeConfigRepositoryTest {
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
        List<String> resultTest = result.stream().map(InitiativeConfig::getInitiativeId).filter(id -> id.matches("initiativeid_test_[0-9]+")).toList();
        Assertions.assertEquals((initiativeEqualsStartDate / 2) + (initiativeBetweenStartAndEndDate / 2), resultTest.size());

        assertInitiative(initiativeStartInterval, resultTest);
        assertInitiative(initiativeBetweenInterval, resultTest);
    }

    private void assertInitiative(List<InitiativeConfig> typeList, List<String> resultTest) {
        typeList.stream()
                .filter(i -> i.getRankingStatus().equals(RankingStatus.WAITING_END))
                .forEach(i -> Assertions.assertTrue(resultTest.contains(i.getInitiativeId()), "Not found in result the initiative " + i.getInitiativeId()));
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