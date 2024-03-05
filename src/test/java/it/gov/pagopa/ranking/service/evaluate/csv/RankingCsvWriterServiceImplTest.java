package it.gov.pagopa.ranking.service.evaluate.csv;

import it.gov.pagopa.ranking.dto.csv.RankingCsvDTO;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

class RankingCsvWriterServiceImplTest {

    @Test
    void write() throws IOException {
        RankingCsvWriterService rankingCsvWriterService = new RankingCsvWriterServiceImpl(';');
        RankingCsvDTO rankingCsvDTO = RankingCsvDTO.builder()
                .fiscalCode("DUMMYFISCALCODE")
                .criteriaConsensusTimestamp(LocalDateTime.now())
                .rankingValue(10000)
                .rank(1)
                .status(BeneficiaryRankingStatus.ELIGIBLE_OK)
                .build();
        RankingCsvDTO rankingCsvDTO2 = RankingCsvDTO.builder()
                .fiscalCode("DUMMYFISCALCODE")
                .criteriaConsensusTimestamp(LocalDateTime.now())
                .rankingValue(11000)
                .rank(2)
                .status(BeneficiaryRankingStatus.ELIGIBLE_OK)
                .build();

        String rankingFilePath = "DUMMYRANKINGFILEPATH";
        String directoryName = "DUMMYDIRECTORYFORTEST";
        String filePathString = "%s/%s.csv".formatted(directoryName, rankingFilePath);
        Path path = Paths.get(filePathString);

        Files.createDirectories(path.getParent());
        FileWriter outputCsvWriter = new FileWriter(filePathString);

        rankingCsvWriterService.write(List.of(rankingCsvDTO, rankingCsvDTO2), outputCsvWriter,false);

        outputCsvWriter.close();

        //TODO added check lines

        //clean temporally directory for test
        FileUtils.delete(new File(filePathString));
        FileUtils.deleteDirectory(new File(directoryName));
    }
}