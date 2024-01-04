package it.gov.pagopa.ranking.constants;

public final class RankingConstants {
    private RankingConstants(){}

    //region onboarding statuses
    public static final String ONBOARDING_STATUS_KO = "ONBOARDING_KO";

    //for family unit
    public static final String ONBOARDING_STATUS_DEMANDED = "DEMANDED";

    //endregion

    //region rejection reasons
    public static final String REJECTION_REASON_CITIZEN_OUT_OF_RANKING = "CITIZEN_OUT_OF_RANKING";
    public static final String REJECTION_REASON_FAMILY_CRITERIA_FAIL = "FAMILY_CRITERIA_FAIL";
    //endregion


    public static final class ExceptionCode {
        public static final String INITIATIVE_NOT_FOUND = "RANKING_INITIATIVE_NOT_FOUND";
        public static final String INITIATIVE_NOT_RELATED = "RANKING_INITIATIVE_NOT_RELATED";
        public static final String GENERIC_ERROR = "RANKING_GENERIC_ERROR";
        public static final String TOO_MANY_REQUESTS = "RANKING_TOO_MANY_REQUESTS";
        public static final String INVALID_REQUEST = "RANKING_INVALID_REQUEST";

        private ExceptionCode() {}
    }
}
