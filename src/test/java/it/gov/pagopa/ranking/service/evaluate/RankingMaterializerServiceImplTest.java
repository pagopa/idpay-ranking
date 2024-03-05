package it.gov.pagopa.ranking.service.evaluate;

import com.mongodb.assertions.Assertions;
import it.gov.pagopa.ranking.dto.mapper.OnboardingRankingRequests2RankingCsvDTOMapper;
import it.gov.pagopa.ranking.model.BeneficiaryRankingStatus;
import it.gov.pagopa.ranking.model.InitiativeConfig;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.repository.OnboardingRankingRequestsRepository;
import it.gov.pagopa.ranking.service.evaluate.csv.RankingCsvWriterService;
import it.gov.pagopa.ranking.test.fakers.InitiativeConfigFaker;
import it.gov.pagopa.ranking.test.fakers.OnboardingRankingRequestsFaker;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@TestPropertySource(
        properties = {
                "app.ranking.csv.tmp-dir=DUMMYDIRECTORYFORTEST",
                "app.ranking.query-page-size=10",
                "app.ranking.savable-entities-size=10"

        })
@ContextConfiguration(classes = {RankingMaterializerServiceImpl.class})
class RankingMaterializerServiceImplTest {
    private static final String RANKING_FILEPATH= "DUMMYRANKINGFILEPATH";

    @MockBean
    private OnboardingRankingRequestsRepository onboardingRankingRequestsRepository;

    @MockBean
    private RankingCsvWriterService csvWriterService;

    @MockBean
    private OnboardingRankingRequests2RankingCsvDTOMapper onboardingRankingRequests2RankingCsvDTOMapper;

    @Value("${app.ranking.query-page-size}")
    private int size;

    @Value("${app.ranking.csv.tmp-dir}")
    private String directoryName;
    @Autowired
    RankingMaterializerService rankingMaterializerService;

    @Test
    void materialize() throws IOException {
        //Given
        String initiativeId = "DUMMYINITIATIVEID";
        InitiativeConfig initiativeConfig = InitiativeConfigFaker.mockInstance(1);
        initiativeConfig.setOrganizationId("DUMMYORGANIZATIONID");
        initiativeConfig.setInitiativeId(initiativeId);
        initiativeConfig.setInitiativeName("DUMMYINITIATIVENAME");
        initiativeConfig.setRankingFilePath(RANKING_FILEPATH);
        initiativeConfig.setSize(1);

        OnboardingRankingRequests onboardingOk = OnboardingRankingRequestsFaker.mockInstance(1);
        onboardingOk.setBeneficiaryRankingStatus(BeneficiaryRankingStatus.TO_NOTIFY);

        List<OnboardingRankingRequests> onboardings = List.of(onboardingOk);

        Mockito.when(onboardingRankingRequestsRepository.findAllByInitiativeId(initiativeId,
                        PageRequest.of(0, size,
                                Sort.by(List.of(
                                        new Sort.Order(Sort.Direction.ASC, OnboardingRankingRequests.Fields.initiativeId),
                                        new Sort.Order(Sort.Direction.ASC, OnboardingRankingRequests.Fields.rankingValue),
                                        new Sort.Order(Sort.Direction.ASC, OnboardingRankingRequests.Fields.criteriaConsensusTimestamp)
                                ))
                        )))
                .thenReturn(onboardings);

        Mockito.when(onboardingRankingRequestsRepository.findAllByInitiativeId(initiativeId,
                        PageRequest.of(1, size,
                                Sort.by(List.of(
                                        new Sort.Order(Sort.Direction.ASC, OnboardingRankingRequests.Fields.initiativeId),
                                        new Sort.Order(Sort.Direction.ASC, OnboardingRankingRequests.Fields.rankingValue),
                                        new Sort.Order(Sort.Direction.ASC, OnboardingRankingRequests.Fields.criteriaConsensusTimestamp)
                                ))
                        )))
                .thenReturn(Collections.emptyList());

        Mockito.when(onboardingRankingRequestsRepository.saveAll(Mockito.any())).thenAnswer(a -> a.getArguments()[0]);

        Mockito.doNothing().when(csvWriterService).write(Mockito.any(), Mockito.any(), Mockito.anyBoolean());

        //When
        rankingMaterializerService.materialize(initiativeConfig);

        //Then
        File fResult = new File("%s/%s/%s/%s-ranking.csv".formatted(
                directoryName,
                initiativeConfig.getOrganizationId(),
                initiativeConfig.getInitiativeId(),
                initiativeConfig.getInitiativeName().substring(0, 10)));
        Assertions.assertTrue(fResult.exists());

        //Clean data
        FileUtils.deleteDirectory(new File(directoryName));

    }
}