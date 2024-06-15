package jikgong.domain.jobpost.entity;

import lombok.Getter;

@Getter
public enum Park {

    FREE("무료 주차"),
    PAID("유료 주차"),
    NONE("주차 공간 없음");

    private final String description;

    Park(String description) {
        this.description = description;
    }
}
