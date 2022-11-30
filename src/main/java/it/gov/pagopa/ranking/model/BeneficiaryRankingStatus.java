package it.gov.pagopa.ranking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BeneficiaryRankingStatus {

    ELIGIBLE_OK("ELIGIBLE_OK"),
    ELIBIGLE_KO("ELIBIGLE_KO"),
    TO_NOTIFY("TO_NOTIFY");
    private final String value;

    BeneficiaryRankingStatus(String value) {
        this.value = value;
    }

    @Override @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static BeneficiaryRankingStatus fromValue(String text) {
        for (BeneficiaryRankingStatus b : BeneficiaryRankingStatus.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
