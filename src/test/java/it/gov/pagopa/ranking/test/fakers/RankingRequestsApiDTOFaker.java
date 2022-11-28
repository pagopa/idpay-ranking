package it.gov.pagopa.ranking.test.fakers;

import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import it.gov.pagopa.ranking.dto.RankingRequestsApiDTO;
import it.gov.pagopa.ranking.model.InitiativeConfig;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

public class RankingRequestsApiDTOFaker {

    private RankingRequestsApiDTOFaker() {
    }
    private static final Random randomGenerator = new Random();

    private static Random getRandom(Integer bias) {
        return bias == null ? randomGenerator : new Random(bias);
    }

    private static int getRandomPositiveNumber(Integer bias) {
        return Math.abs(getRandom(bias).nextInt());
    }

    private static int getRandomPositiveNumber(Integer bias, int bound) {
        return Math.abs(getRandom(bias).nextInt(bound));
    }

    private static final FakeValuesService fakeValuesServiceGlobal = new FakeValuesService(new Locale("it"), new RandomService(null));

    private static FakeValuesService getFakeValuesService(Integer bias) {
        return bias == null ? fakeValuesServiceGlobal : new FakeValuesService(new Locale("it"), new RandomService(getRandom(bias)));
    }

    /**
     * It will return an example of {@link InitiativeConfig}. Providing a bias, it will return a pseudo-casual object
     */
    public static RankingRequestsApiDTO mockInstance(Integer bias) {
        return mockInstanceBuilder(bias).build();
    }

    public static RankingRequestsApiDTO.RankingRequestsApiDTOBuilder mockInstanceBuilder(Integer bias) {
        LocalDateTime today = LocalDateTime.now();

        RankingRequestsApiDTO.RankingRequestsApiDTOBuilder out = RankingRequestsApiDTO.builder();
        return out.userId("userId_%d".formatted(bias))
                .initiativeId("initiativeId_%d".formatted(bias))
                .organizationId("organizationId_%d".formatted(bias))
                .admissibilityCheckDate(today)
                .criteriaConsensusTimestamp(today)
                .rankingValue(getRandomPositiveNumber(1))
                .rank(1);

    }
}
