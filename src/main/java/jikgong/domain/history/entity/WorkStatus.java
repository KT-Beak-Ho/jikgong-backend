package jikgong.domain.history.entity;

import lombok.Getter;

@Getter
public enum WorkStatus {

    ALL("전체"),
    START_WORK("출근"),
    FINISH_WORK("퇴근"),
    NOT_WORK("결근"),
    EARLY_LEAVE("조퇴");
    private final String description;

    WorkStatus(String description) {
        this.description = description;
    }
}
