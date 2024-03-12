package jikgong.domain.notification.entity;

import lombok.Getter;

@Getter
public enum NotificationType {
    OFFER("헤드헌팅"),
    APPLY_CANCEL("요청 취소");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }
}
