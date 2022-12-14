package it.gov.pagopa.ranking.event.producer;

import it.gov.pagopa.ranking.dto.event.EvaluationDTO;

public interface OnboardingNotifierProducer {

    public boolean notify(EvaluationDTO evaluationDTO);

}
