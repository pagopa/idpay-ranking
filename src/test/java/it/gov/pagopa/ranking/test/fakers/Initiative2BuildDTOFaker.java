package it.gov.pagopa.ranking.test.fakers;

import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import it.gov.pagopa.ranking.dto.initiative.*;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Initiative2BuildDTOFaker {

    private Initiative2BuildDTOFaker() {
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
     * It will return an example of {@link InitiativeBuildDTO}. Providing a bias, it will return a pseudo-casual object
     */
    public static InitiativeBuildDTO mockInstance(Integer bias) {
        return mockInstanceBuilder(bias).build();
    }

    public static InitiativeBuildDTO.InitiativeBuildDTOBuilder mockInstanceBuilder(Integer bias) {
        InitiativeBuildDTO.InitiativeBuildDTOBuilder out = InitiativeBuildDTO.builder();

        LocalDate nowDate=LocalDate.now();

        out.initiativeId(bias!=null? "initiativeId_%d".formatted(bias) : "?????");
        out.initiativeName(bias!=null? "initiativeName_%d".formatted(bias) : "?????");
        out.organizationId(bias!=null? "organizationId_%d".formatted(bias) : "?????");
        out.status(bias!=null? "status_%d".formatted(bias) : "?????");
        out.beneficiaryRanking(true);

        InitiativeGeneralDTO initiativeGeneralDTO = InitiativeGeneralDTO.builder()
                .name(bias!=null? "name_%d".formatted(bias) : "?????")
                .budget(BigDecimal.TEN)
                .beneficiaryBudget(BigDecimal.ONE)
                .rankingStartDate(nowDate)
                .rankingEndDate(nowDate.plusMonths(1L))
                .rankingEnabled(Boolean.TRUE)
                .build();
        out.general(initiativeGeneralDTO);

        List<AutomatedCriteriaDTO> automatedCriteriaDTOS = List.of(
                AutomatedCriteriaDTO.builder().authority("INPS").code("ISEE").orderDirection(Sort.Direction.ASC).build()
        );
        InitiativeBeneficiaryRuleDTO initiativeBeneficiaryRuleDTO = InitiativeBeneficiaryRuleDTO.builder()
                .automatedCriteria(automatedCriteriaDTOS)
                .build();
        out.beneficiaryRule(initiativeBeneficiaryRuleDTO);

        return out;

    }
}

