package it.gov.pagopa.ranking.service.csv;

import it.gov.pagopa.ranking.dto.csv.RankingCsvDTO;

import java.util.List;

public interface RankingCsvWriterService {

    String write(List<RankingCsvDTO> csvLines);
}
