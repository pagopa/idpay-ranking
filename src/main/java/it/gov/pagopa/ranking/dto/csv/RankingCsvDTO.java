package it.gov.pagopa.ranking.dto.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
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

    @CsvBindByName(column = "id") private String id;
    @CsvBindByName(column = "userId") private String userId;
    @CsvBindByName(column = "initiativeId") private String initiativeId;
    @CsvBindByName(column = "organizationId") private String organizationId;

    @CsvCustomBindByName(column = "admissibilityCheckDate", converter= LocalDateTimeConverter.class) private LocalDateTime admissibilityCheckDate;
    @CsvCustomBindByName(column = "criteriaConsensusTimestamp", converter = LocalDateTimeConverter.class) private LocalDateTime criteriaConsensusTimestamp;

    @CsvBindByName(column = "rankingValue") private long rankingValue;
    @CsvBindByName(column = "rankingValueOriginal") private long rankingValueOriginal;
    @CsvBindByName(column = "ranking") private long rank;
    @CsvBindByName(column = "beneficiaryRankingStatus") private BeneficiaryRankingStatus beneficiaryRankingStatus;
}
