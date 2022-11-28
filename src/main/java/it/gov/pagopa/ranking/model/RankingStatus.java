package it.gov.pagopa.ranking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets rankingStatus
 */
public enum RankingStatus {
    WAITING_END("WAITING_END"),
    READY("READY"),
    PUBLISHING("PUBLISHING"),
    COMPLETED("COMPLETED");

    private final String value;

    RankingStatus(String value) {
            this.value = value;
        }

    @Override @JsonValue
    public String toString() {
            return String.valueOf(value);
        }

    @JsonCreator
    public static RankingStatus fromValue(String text) {
        for (RankingStatus b : RankingStatus.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

}
