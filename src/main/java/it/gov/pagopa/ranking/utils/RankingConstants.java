package it.gov.pagopa.ranking.utils;

public final class RankingConstants {
    private RankingConstants(){}

    //region initiative ranking status
    public static final String INITIATIVE_RANKING_STATUS_WAITING_END = "WAITING_END";
    public static final String INITIATIVE_RANKING_STATUS_BUILDING = "BUILDING";
    public static final String INITIATIVE_RANKING_STATUS_NOTIFY = "NOTIFY";
    public static final String INITIATIVE_RANKING_STATUS_READY = "READY";
    public static final String INITIATIVE_RANKING_STATUS_COMPLETED = "COMPLETED";
    //endregion

    //region criteria code
    public static final String CRITERIA_CODE_ISEE = "ISEE";
    public static final String CRITERIA_CODE_BIRTHDATE = "BIRTHDATE";
    public static final String CRITERIA_CODE_RESIDENCE = "RESIDENCE";
    //endregion
}
