package jikgong.domain.project.entity;

import lombok.Getter;

@Getter
public enum ProjectStatus {
    COMPLETED("완료됨"),
    IN_PROGRESS("진행중"),
    PLANNED("예정");

    ProjectStatus(String description) {
        this.description = description;
    }

    private final String description;
}
