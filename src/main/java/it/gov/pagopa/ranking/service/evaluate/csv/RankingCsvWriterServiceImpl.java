package it.gov.pagopa.ranking.service.evaluate.csv;

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
    private final HeaderColumnNameStrategy<RankingCsvDTO> mappingStrategyWithHeader;
    private final HeaderColumnNameStrategy<RankingCsvDTO> mappingStrategyNoHeader;

    public RankingCsvWriterServiceImpl(
            @Value("${app.ranking.csv.separator}") char csvSeparator
    ) {
        this.csvSeparator = csvSeparator;
        this.mappingStrategyWithHeader = new HeaderColumnNameStrategy<>(RankingCsvDTO.class, true);
        this.mappingStrategyNoHeader = new HeaderColumnNameStrategy<>(RankingCsvDTO.class, false);
    }

    @Override
    public void write(List<RankingCsvDTO> csvLines, FileWriter writer, boolean useHeader) {

        try {
            StatefulBeanToCsv<RankingCsvDTO> csvWriter = buildCsvWriter(writer, useHeader);
            csvWriter.write(csvLines);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new IllegalStateException("[RANKING_CSV] Cannot create csv writer", e);
        }
    }

    private StatefulBeanToCsv<RankingCsvDTO> buildCsvWriter(FileWriter writer, boolean useHeader) {
        return  new StatefulBeanToCsvBuilder<RankingCsvDTO>(writer)
                .withMappingStrategy(useHeader? mappingStrategyWithHeader : mappingStrategyNoHeader)
                .withSeparator(csvSeparator)
                .withLineEnd("\n")
                .build();
    }
}
