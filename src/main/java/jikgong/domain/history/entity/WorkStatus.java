package jikgong.domain.history.entity;

import lombok.Getter;

@Getter
public enum WorkStatus {
    ALL("전체"),
    WORK("출근"),
    NOT_WORK("결근");
    private final String description;

    WorkStatus(String description) {
        this.description = description;
    }
}
