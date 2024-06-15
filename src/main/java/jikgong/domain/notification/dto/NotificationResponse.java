package jikgong.domain.notification.dto;

import jikgong.domain.notification.entity.Notification;
import jikgong.domain.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class NotificationResponse {

    private Long notificationId;
    private String content; // 내용
    private String url; // 이동 url
    private Boolean isRead; // 읽음 여부

    private NotificationType notificationType;

    private LocalDateTime receivedAt; // 알림 받은 시각

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
            .notificationId(notification.getId())
            .content(notification.getContent())
            .url(notification.getUrl())
            .isRead(notification.getIsRead())
            .notificationType(notification.getNotificationType())
            .receivedAt(notification.getCreatedDate())
            .build();
    }
}
