package jikgong.domain.notification.entity;

import lombok.Getter;

@Getter
public enum NotificationType {
    OFFER("헤드헌팅");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }
}
