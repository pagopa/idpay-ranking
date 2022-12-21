package it.gov.pagopa.ranking.dto.mapper;

import it.gov.pagopa.ranking.connector.rest.pdv.UserRestService;
import it.gov.pagopa.ranking.dto.csv.RankingCsvDTO;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import it.gov.pagopa.ranking.model.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class OnboardingRankingRequests2RankingCsvDTOMapper implements Function<OnboardingRankingRequests, RankingCsvDTO> {

    private final UserRestService userRestService;

    public OnboardingRankingRequests2RankingCsvDTOMapper(UserRestService userRestService) {
        this.userRestService = userRestService;
    }

    @Override
    public RankingCsvDTO apply(OnboardingRankingRequests onboardingRankingRequests) {

        User user = userRestService.getUser(onboardingRankingRequests.getUserId());

        return RankingCsvDTO.builder()
                .fiscalCode(user.getFiscalCode())
                .criteriaConsensusTimestamp(onboardingRankingRequests.getCriteriaConsensusTimestamp())
                .rankingValue(onboardingRankingRequests.getRankingValue2Show())
                .rank(onboardingRankingRequests.getRank())
                .status(onboardingRankingRequests.getBeneficiaryRankingStatus())
                .build();
    }
}
