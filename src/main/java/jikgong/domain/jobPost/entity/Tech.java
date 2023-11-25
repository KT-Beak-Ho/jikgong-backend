package jikgong.domain.jobPost.entity;

import lombok.Getter;

@Getter
public enum Tech {
    NORMAL("보통 인부");
    private final String description;
    // todo: 추가 예정
    Tech(String description) {
        this.description = description;
    }
}
