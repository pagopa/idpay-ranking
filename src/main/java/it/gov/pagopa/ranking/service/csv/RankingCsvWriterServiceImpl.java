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

import java.io.FileWriter;
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
    public void write(List<RankingCsvDTO> csvLines, FileWriter writer, boolean printHeader) {

        try {
            // TODO Configure printHeader
            StatefulBeanToCsv<RankingCsvDTO> csvWriter = buildCsvWriter(writer);
            csvWriter.write(csvLines);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new IllegalStateException("[RANKING_CSV] Cannot create csv writer", e);
        }
    }

    private StatefulBeanToCsv<RankingCsvDTO> buildCsvWriter(FileWriter writer) {
        return new StatefulBeanToCsvBuilder<RankingCsvDTO>(writer)
                .withMappingStrategy(mappingStrategy)
                .withSeparator(csvSeparator)
                .withLineEnd("\n")
                .build();
    }
}
