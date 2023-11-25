package jikgong.domain.member.entity;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_WORKER("일반 사용자"), ROLE_COMPANY("이벤트 등록자");
    private final String description;

    Role(String description) {
        this.description = description;
    }
}
