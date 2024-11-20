package org.userservice.userservice.domain;

import lombok.Getter;

@Getter
public enum Tier {
    DIAMOND("다이아 등급"),
    PLATINUM("플래티넘 등급"),
    GOLD( "골드 등급"),
    SILVER("실버 등급"),
    BRONZE("브론즈 등급");

    private final String description;
    Tier(String description) {
        this.description = description;
    }
}
