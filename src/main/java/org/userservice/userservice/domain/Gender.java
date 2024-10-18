package org.userservice.userservice.domain;

import lombok.Getter;

@Getter
public enum Gender {

    MALE("남성"),
    FEMALE("여성"),
    NONE( "알 수 없음");

    private final String description;

    Gender(String description) {
        this.description = description;
    }
}
