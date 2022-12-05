package it.gov.pagopa.ranking.service.csv;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import it.gov.pagopa.ranking.dto.csv.RankingCsvDTO;
import it.gov.pagopa.ranking.utils.csv.HeaderColumnNameStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@Service
@Slf4j
public class RankingCsvWriterServiceImpl implements RankingCsvWriterService{

    private final char csvSeparator;
    private final HeaderColumnNameStrategy<RankingCsvDTO> mappingStrategy;

    public RankingCsvWriterServiceImpl(
            @Value("${app.ranking.csv.separator}") char csvSeparator
    ) {
        this.csvSeparator = csvSeparator;
        this.mappingStrategy = new HeaderColumnNameStrategy<>(RankingCsvDTO.class);
    }

    @Override
    public String write(List<RankingCsvDTO> csvLines) {

        try (StringWriter writer = new StringWriter()) {
            StatefulBeanToCsv<RankingCsvDTO> csvWriter = buildCsvWriter(writer);
            csvWriter.write(csvLines);
            return writer.toString();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new IllegalStateException("[RANKING_CSV] Cannot create csv writer", e);
        }
    }

    private StatefulBeanToCsv<RankingCsvDTO> buildCsvWriter(StringWriter writer) {
        return new StatefulBeanToCsvBuilder<RankingCsvDTO>(writer)
                .withMappingStrategy(mappingStrategy)
                .withSeparator(csvSeparator)
                .withLineEnd("\n")
                .build();
    }
}
