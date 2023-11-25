package jikgong.domain.member.entity;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("Male"), FEMALE("Female");
    private final String description;

    Gender(String description) {
        this.description = description;
    }
}
