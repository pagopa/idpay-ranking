package it.gov.pagopa.ranking.constants;

public final class OnboardingConstants {
    private OnboardingConstants(){}

    //region onboarding statuses
    public static final String ONBOARDING_STATUS_KO = "ONBOARDING_KO";
    public static final String ONBOARDING_STATUS_OK = "ONBOARDING_OK";

    //for family unit
    public static final String ONBOARDING_STATUS_DEMANDED = "DEMANDED";

    //endregion

    //region rejection reasons
    public static final String REJECTION_REASON_CITIZEN_OUT_OF_RANKING = "CITIZEN_OUT_OF_RANKING";
    public static final String REJECTION_REASON_FAMILY_CRITERIA_FAIL = "FAMILY_CRITERIA_FAIL";
    //endregion
}
