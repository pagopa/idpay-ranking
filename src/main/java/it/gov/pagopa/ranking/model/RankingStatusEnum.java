package it.gov.pagopa.ranking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets rankingStatus
 */
public enum RankingStatusEnum {
    RANKING_STATUS_WAITING_END("WAITING_END"),
    RANKING_STATUS_BUILDING("BUILDING"),
    RANKING_STATUS_READY("READY"),
    RANKING_STATUS_COMPLETED("COMPLETED");

    private final String value;

    RankingStatusEnum(String value) {
            this.value = value;
        }

    @Override @JsonValue
    public String toString() {
            return String.valueOf(value);
        }

    @JsonCreator
    public static RankingStatusEnum fromValue(String text) {
        for (RankingStatusEnum b : RankingStatusEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

}
