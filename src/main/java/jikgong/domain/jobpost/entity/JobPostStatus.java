package jikgong.domain.jobpost.entity;

import lombok.Getter;

@Getter
public enum JobPostStatus {

    COMPLETED("완료됨"),
    IN_PROGRESS("진행중"),
    PLANNED("예정");

    private final String description;

    JobPostStatus(String description) {
        this.description = description;
    }
}
