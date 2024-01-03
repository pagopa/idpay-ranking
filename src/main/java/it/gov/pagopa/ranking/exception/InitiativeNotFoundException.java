package it.gov.pagopa.ranking.exception;

import it.gov.pagopa.common.web.exception.ServiceException;
import it.gov.pagopa.common.web.exception.ServiceExceptionPayload;
import it.gov.pagopa.ranking.constants.RankingConstants;

public class InitiativeNotFoundException extends ServiceException {

    public InitiativeNotFoundException(String message) {
        this(RankingConstants.ExceptionCode.INITIATIVE_NOT_FOUND, message);
    }

    public InitiativeNotFoundException(String code, String message) {
        this(code, message,null, false, null);
    }

    public InitiativeNotFoundException(String code, String message, ServiceExceptionPayload response, boolean printStackTrace, Throwable ex) {
        super(code, message, response, printStackTrace, ex);
    }

}
