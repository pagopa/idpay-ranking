package it.gov.pagopa.ranking.utils.csv;

import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/** A strategy to build a csv having column ordered as declaration order and columnName as defined using {@link CsvBindByName} annotation*/
@Slf4j
public class HeaderColumnNameStrategy<T> extends HeaderColumnNameMappingStrategy<T> {
    private final boolean useHeader;

    public HeaderColumnNameStrategy(Class<? extends T> clazz, boolean useHeader) {
        this.useHeader = useHeader;
        setType(clazz);

        // Build the header line which respects the declaration order
        String headerLine = Arrays.stream(clazz.getDeclaredFields())
                .map(field -> Optional.ofNullable(field.getAnnotation(CsvBindByName.class)).map(CsvBindByName::column)
                        .or(() -> Optional.ofNullable(field.getAnnotation(CsvDate.class)).map(CsvDate::value))
                        .orElse(null)
                )
                .filter(Objects::nonNull)
                .collect(Collectors.joining(","));

        // Initialize strategy by reading a CSV with header only
        try (StringReader reader = new StringReader(headerLine)) {
            CsvToBean<T> sampleCsv = new CsvToBeanBuilder<T>(reader)
                    .withType(clazz)
                    .withMappingStrategy(this)
                    .build();
            sampleCsv.forEach(l -> log.trace("Loading header position: {}", l));
        }
    }

    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        if (!useHeader) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return super.generateHeader(bean);
    }
}
