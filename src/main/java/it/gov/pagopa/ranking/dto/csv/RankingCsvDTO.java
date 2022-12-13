package it.gov.pagopa.ranking.dto.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
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

    @CsvBindByName(column = "criteriaConsensusTimestamp")
    @CsvDate(value = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime criteriaConsensusTimestamp;

    @CsvBindByName(column = "rankingValue") private long rankingValue;
    @CsvBindByName(column = "ranking") private long rank;
}
