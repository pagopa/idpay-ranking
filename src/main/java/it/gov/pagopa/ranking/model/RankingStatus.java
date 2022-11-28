package it.gov.pagopa.ranking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets rankingStatus
 */
public enum RankingStatus {
    /**
     * Waiting for the end of the initiative
     */
    WAITING_END("WAITING_END"),
    /**
     * The initiative has been elaborated and the file has been generated
     */
    READY("READY"),
    /**
     * The user has confirmed the ranking, and it is now possible to proceed with the publication
     */
    PUBLISHING("PUBLISHING"),
    /**
     * Publication completed
     */
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
