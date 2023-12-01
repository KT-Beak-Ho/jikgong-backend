package jikgong.domain.member.entity;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_WORKER("노동자"), ROLE_COMPANY("회사");
    private final String description;

    Role(String description) {
        this.description = description;
    }
}
