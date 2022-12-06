package it.gov.pagopa.ranking.dto.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import it.gov.pagopa.ranking.utils.csv.LocalDateTimeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingCsvDTO {

    @CsvBindByName(column = "userId") private String userId;
    @CsvCustomBindByName(column = "criteriaConsensusTimestamp", converter = LocalDateTimeConverter.class) private LocalDateTime criteriaConsensusTimestamp;
    @CsvBindByName(column = "rankingValue") private long rankingValue;
    @CsvBindByName(column = "ranking") private long rank;
}
