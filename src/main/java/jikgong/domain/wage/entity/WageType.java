package jikgong.domain.wage.entity;

import lombok.Getter;

@Getter
public enum WageType {
    AUTO("자동 입력"),
    CUSTOM("직접 입력");

    private final String description;

    WageType(String description) {
        this.description = description;
    }
}
