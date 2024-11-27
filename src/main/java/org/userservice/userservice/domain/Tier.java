package org.userservice.userservice.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Tier {
    KING(1000, "킹 등급", "King"),
    SENIOR(700, "시니어","Senior"),
    JUNIOR(500, "주니어 등급", "Junior"),
    SOPHOMORE(300, "소포모어 등급", "Sophomore"),
    FRESHMAN(0, "프레시맨 등급", "freshman");

    private final int minScore;
    private final String description;
    private final String value;

    public static Tier fromScore(int score) {
        for (Tier tier : values()) {
            if (score >= tier.minScore) {
                return tier;
            }
        }
        return FRESHMAN;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Tier fromValue(String value) {
        for (Tier tier : Tier.values()) {
            if (tier.value.equalsIgnoreCase(value)) {
                return tier;
            }
        }
        throw new IllegalArgumentException("Unknown Tier value: " + value);
    }
}
