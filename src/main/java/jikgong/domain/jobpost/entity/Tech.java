package jikgong.domain.jobpost.entity;

import lombok.Getter;

@Getter
public enum Tech {

    NORMAL("보통 인부"),
    TILE("타일공");
    private final String description;
    // todo: 추가 예정
    Tech(String description) {
        this.description = description;
    }
}
