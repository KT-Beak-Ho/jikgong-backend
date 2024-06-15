package jikgong.domain.profit.entity;

import lombok.Getter;

@Getter
public enum ProfitType {

    AUTO("자동 입력"),
    CUSTOM("직접 입력");

    private final String description;

    ProfitType(String description) {
        this.description = description;
    }
}
