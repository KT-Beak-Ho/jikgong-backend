package jikgong.global.event.dtos;

import jikgong.domain.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
public class NotificationEvent {
    // content & url 에 사용
    private String companyName;
    private List<LocalDate> workDateList;
    private Long jobPostId;

    private NotificationType notificationType;
    private Long receiverId;
}
