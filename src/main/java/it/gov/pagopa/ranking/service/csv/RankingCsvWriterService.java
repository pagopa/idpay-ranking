package it.gov.pagopa.ranking.service.csv;

import it.gov.pagopa.ranking.dto.csv.RankingCsvDTO;

import java.io.FileWriter;
import java.nio.file.Path;
import java.util.List;

public interface RankingCsvWriterService {

    Path write(List<RankingCsvDTO> csvLines, FileWriter writer);
}
