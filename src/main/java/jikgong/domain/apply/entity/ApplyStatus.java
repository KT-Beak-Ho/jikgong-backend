package jikgong.domain.apply.entity;

import lombok.Getter;

@Getter
public enum ApplyStatus {

    PENDING("대기중"),
    REJECTED("거절됨"),
    ACCEPTED("수락됨"),
    CANCELED("취소됨"),
    OFFERED("제안됨"),
    APPLIED("신청됨");

    private final String description;

    ApplyStatus(String description) {
        this.description = description;
    }
}
