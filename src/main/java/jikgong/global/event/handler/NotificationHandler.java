package jikgong.global.event.handler;

import jikgong.domain.notification.service.NotificationService;
import jikgong.global.event.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationHandler {
    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void saveNotification(NotificationEvent event) {
        String dateList = event.getWorkDateList().stream()
                .map(date -> date.toString().substring(5, 10))
                .collect(Collectors.joining(", "));

        String content = "[" + event.getCompanyName() + "] 에서 " + dateList + "에 일자리를 제안했습니다.";
        String url = null; // 제안 내역으로 이동시키면 될 것 같음
        notificationService.saveNotification(event.getReceiverId(), event.getNotificationType(), content, url);
    }
}
