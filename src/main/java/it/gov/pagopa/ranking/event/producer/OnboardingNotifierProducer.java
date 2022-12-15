package it.gov.pagopa.ranking.event.producer;

import it.gov.pagopa.ranking.dto.event.EvaluationRankingDTO;

public interface OnboardingNotifierProducer {

    boolean notify(EvaluationRankingDTO evaluationDTO);

}
