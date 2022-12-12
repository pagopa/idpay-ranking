package it.gov.pagopa.ranking.service.csv;

import it.gov.pagopa.ranking.dto.csv.RankingCsvDTO;

import java.io.FileWriter;
import java.util.List;

public interface RankingCsvWriterService {

    void write(List<RankingCsvDTO> csvLines, FileWriter writer, boolean printHeader);
}
