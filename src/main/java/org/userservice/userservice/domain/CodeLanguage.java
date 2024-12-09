package org.userservice.userservice.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum CodeLanguage {
    JAVA("java"),
    PYTHON("python"),
    C("c"),
    C_PLUS_PLUS("c_plus_plus"),
    JAVASCRIPT("javascript"),
    RUBY("ruby"),
    SWIFT("swift"),
    KOTLIN("kotlin"),
    PHP("php"),
    GO("go"),
    R("r"),
    TYPESCRIPT("typescript");

    private final String description;

    CodeLanguage(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static CodeLanguage fromDescription(String description) {
        for (CodeLanguage language : CodeLanguage.values()) {
            if (language.description.equalsIgnoreCase(description)) {
                return language;
            }
        }
        throw new IllegalArgumentException("Unknown CodeLanguage description: " + description);
    }
}
