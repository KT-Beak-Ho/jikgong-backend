package jikgong.domain.member.entity;

import lombok.Getter;

@Getter

public enum Nationality {

    KOREAN("내국인"),
    FOREIGNER("외국인");

    private final String description;

    Nationality(String description) {
        this.description = description;
    }
}
