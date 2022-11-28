package it.gov.pagopa.ranking.test.fakers;

import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.Order;
import it.gov.pagopa.ranking.utils.RankingConstants;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class InitiativeConfigFaker {

    private InitiativeConfigFaker() {
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
    public static InitiativeConfig mockInstance(Integer bias) {
        return mockInstanceBuilder(bias).build();
    }

    public static InitiativeConfig.InitiativeConfigBuilder mockInstanceBuilder(Integer bias) {
        LocalDate today = LocalDate.now();

        InitiativeConfig.InitiativeConfigBuilder out = InitiativeConfig.builder();
        return out.initiativeId("initiativeId_%d".formatted(bias))
                .initiativeName("initiativeName")
                .organizationId("organizationId_%d".formatted(bias))
                .status("STATUS")
                .rankingStartDate(today.minusDays(5))
                .rankingEndDate(today.plusDays(5))
                .initiativeBudget(BigDecimal.valueOf(10000L))
                .beneficiaryInitiativeBudget(BigDecimal.valueOf(200L))
                .rankingStatus(RankingConstants.INITIATIVE_RANKING_STATUS_COMPLETED)
                .size(getRandomPositiveNumber(bias))
                .rankingFields(List.of(
                      new Order("ISEE", Sort.Direction.ASC)
                ));

    }
}
