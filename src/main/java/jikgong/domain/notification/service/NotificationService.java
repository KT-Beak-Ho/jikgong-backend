package jikgong.domain.notification.service;

import jikgong.domain.notification.dtos.NotificationResponse;
import jikgong.domain.notification.dtos.UnreadCountResponse;
import jikgong.global.event.dtos.AlarmEvent;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.notification.entity.Notification;
import jikgong.domain.notification.entity.NotificationType;
import jikgong.domain.notification.repository.NotificationRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher publisher;

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

        // todo: templateCode 변경
        publisher.publishEvent(new AlarmEvent(content, worker.getId(), worker.getPhone(), "code"));
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

    public UnreadCountResponse unreadNotification(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        int unreadCount = notificationRepository.countUnreadNotification(member.getId());
        return UnreadCountResponse.builder().unreadCount(unreadCount).build();
    }
}
