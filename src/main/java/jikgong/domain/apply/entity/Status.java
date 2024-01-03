package jikgong.domain.apply.entity;

import lombok.Getter;

@Getter
public enum Status {
    PENDING("대기중"),
    REJECTED("거절됨"),
    ACCEPTED("수락됨");

    private final String description;

    Status(String description) {
        this.description = description;
    }
}
