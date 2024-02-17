package jikgong.domain.notification.service;

import jikgong.domain.notification.dtos.NotificationResponse;
import jikgong.global.fcm.dtos.FCMNotificationRequestDto;
import jikgong.global.fcm.service.FCMNotificationService;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.notification.entity.Notification;
import jikgong.domain.notification.entity.NotificationType;
import jikgong.domain.notification.repository.NotificationRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final FCMNotificationService fcmNotificationService;
    private final MemberRepository memberRepository;

    public void saveNotification(Long receiverId, NotificationType type, String content, String url) {
        Member worker = memberRepository.findById(receiverId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Notification notification = Notification.builder()
                .content(content)
                .url(url)
                .notificationType(type)
                .receiver(worker)
                .build();

        notificationRepository.save(notification);

        // push 알림 수락 시
        if (worker.getIsNotification()) {
            // fcm 알림 발송
            FCMNotificationRequestDto request = FCMNotificationRequestDto.builder()
                    .targetMemberId(worker.getId())
                    .title(content)
                    .body(content)
                    .build();
//            fcmNotificationService.sendNotificationByToken(request);
        }
    }

    public List<NotificationResponse> findNotifications(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<NotificationResponse> notificationResponseList = notificationRepository.findByMember(member.getId()).stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());

        return notificationResponseList;
    }

    public void readNotification(Long memberId, Long notificationId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        notification.readNotification();
    }
}
