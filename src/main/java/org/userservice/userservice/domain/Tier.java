package org.userservice.userservice.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Tier {
    KING(1000, "킹 등급"),
    SENIOR(700, "시니어 등급"),
    JUNIOR(500, "주니어 등급"),
    SOPHOMORE(300, "소포모어 등급"),
    FRESHMAN(0, "프레시맨 등급");

    private final int minScore;
    private final String description;

    public static Tier fromScore(int score) {
        for (Tier tier : values()) {
            if (score >= tier.minScore) {
                return tier;
            }
        }
        return FRESHMAN;
    }
}
