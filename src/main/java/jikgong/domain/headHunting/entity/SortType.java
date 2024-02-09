package jikgong.domain.headHunting.entity;

import lombok.Getter;

@Getter
public enum SortType {
    DISTANCE("거리순"),
    CAREER("경력순");

    private final String description;

    SortType(String description) {
        this.description = description;
    }
}

