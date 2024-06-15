package jikgong.global.event.handler;

import jikgong.global.alimtalk.service.AlimTalkService;
import jikgong.global.event.dtos.AlarmEvent;
import jikgong.global.fcm.dtos.FCMNotificationRequestDto;
import jikgong.global.fcm.service.FCMNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AlarmHandler {
    private final FCMNotificationService fcmNotificationService;
    private final AlimTalkService alimTalkService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendFCM(AlarmEvent event) {
        // fcm 알림 발송
        FCMNotificationRequestDto request = FCMNotificationRequestDto.builder()
                .targetMemberId(event.getReceiverId())
                .title(event.getContent())
                .body(event.getContent())
                .build();
        fcmNotificationService.sendNotificationByToken(request);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendAlimTalk(AlarmEvent event) {
        alimTalkService.sendAlimTalk(event.getPhone(), event.getTemplateCode(), event.getContent());
    }
}
