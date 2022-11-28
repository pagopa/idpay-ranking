package it.gov.pagopa.ranking.test.fakers;

import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.Order;
import it.gov.pagopa.ranking.model.RankingStatus;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
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
        InitiativeConfig.InitiativeConfigBuilder out = InitiativeConfig.builder();
        LocalDate now = LocalDate.now();

        out.initiativeId(bias!=null? "initiativeId_%d".formatted(bias) : "?????");
        out.initiativeName(bias!=null? "initiativeName_%d".formatted(bias) : "?????");
        out.organizationId(bias!=null? "organizationId_%d".formatted(bias) : "?????");
        out.initiativeStatus(bias!=null? "status_%d".formatted(bias) : "?????");
        out.rankingStartDate(now);
        out.rankingEndDate(now.plusMonths(7L));
        out.initiativeBudget(BigDecimal.TEN);
        out.beneficiaryInitiativeBudget(BigDecimal.ONE);
        out.rankingStatus(RankingStatus.WAITING_END);
        out.size(10);
        out.rankingFields(List.of(
                Order.builder().fieldCode("ISEE").direction(Sort.Direction.ASC).build()
        ));

        return out;

    }
}

