package it.gov.pagopa.ranking.service.evaluate.csv;

import it.gov.pagopa.ranking.dto.csv.RankingCsvDTO;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

class RankingCsvWriterServiceImplTest {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void write(boolean headersIsPresent) throws IOException {
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

        List<RankingCsvDTO> onboardingsList = List.of(rankingCsvDTO, rankingCsvDTO2);

        String rankingFilePath = "DUMMYRANKINGFILEPATH";
        String directoryName = "DUMMYDIRECTORYFORTEST";
        String filePathString = "%s/%s.csv".formatted(directoryName, rankingFilePath);
        Path path = Paths.get(filePathString);

        Files.createDirectories(path.getParent());
        FileWriter outputCsvWriter = new FileWriter(filePathString);

        rankingCsvWriterService.write(onboardingsList, outputCsvWriter,headersIsPresent);

        outputCsvWriter.close();

        //Then
        int lines = getLinesFromFile(filePathString);

        Assertions.assertEquals(onboardingsList.size()+ (headersIsPresent? 1 : 0), lines);

        //clean temporally directory for test
        FileUtils.delete(new File(filePathString));
        FileUtils.deleteDirectory(new File(directoryName));
    }

    private static int getLinesFromFile(String filePathString) throws IOException {
        FileReader finalCsv = new FileReader(filePathString);
        BufferedReader reader = new BufferedReader(finalCsv);
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();
        return lines;
    }
}