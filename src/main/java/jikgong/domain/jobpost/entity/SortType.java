package jikgong.domain.jobpost.entity;

import lombok.Getter;

@Getter
public enum SortType {
    DISTANCE("거리순"),
    WAGE("임금순");

    private final String description;

    SortType(String description) {
        this.description = description;
    }
}
