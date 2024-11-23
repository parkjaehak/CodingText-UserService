package org.userservice.userservice.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Tier {
    DIAMOND(1000, "다이아 등급"),
    PLATINUM(700, "플래티넘 등급"),
    GOLD(500, "골드 등급"),
    SILVER(300, "실버 등급"),
    BRONZE(0, "브론즈 등급");

    private final int minScore;
    private final String description;

    public static Tier fromScore(int score) {
        for (Tier tier : values()) {
            if (score >= tier.minScore) {
                return tier;
            }
        }
        return BRONZE;
    }
}
