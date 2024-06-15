package jikgong.global.event.dto;

import jikgong.domain.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
public class NotificationEvent {
    // content & url 에 사용
    // [회사]에서 [10, 11, 12]일날 일자리를 제안했습니다.
    private String companyName;
    private List<LocalDate> workDateList;
    private Long jobPostId;

    private NotificationType notificationType;
    private Long receiverId;
}
