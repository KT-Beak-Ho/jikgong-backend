package jikgong.domain.member.entity;

import lombok.Getter;

@Getter
public enum Nationality {
    KOREA("한국");

    private final String description;
    // todo: 추가 예정
    Nationality(String description) {
        this.description = description;
    }
}
