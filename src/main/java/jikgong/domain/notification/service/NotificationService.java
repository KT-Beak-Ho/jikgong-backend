package jikgong.domain.notification.service;

import jikgong.domain.member.entity.CompanyNotificationInfo;
import jikgong.domain.member.entity.Role;
import jikgong.domain.member.entity.WorkerNotificationInfo;
import jikgong.domain.notification.dto.NotificationInfoResponse;
import jikgong.domain.notification.dto.NotificationResponse;
import jikgong.domain.notification.dto.UnreadCountResponse;
import jikgong.domain.notification.dto.company.CompanyNotificationInfoRequest;
import jikgong.domain.notification.dto.worker.WorkerNotificationInfoRequest;
import jikgong.global.event.dto.AlarmEvent;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.notification.entity.Notification;
import jikgong.domain.notification.entity.NotificationType;
import jikgong.domain.notification.repository.NotificationRepository;
import jikgong.global.exception.JikgongException;
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

    /**
     * 알림 생성
     * spring event 발행
     * 1. FCM push alarm
     * 2. kakao 알림톡
     */
    public void saveNotification(Long receiverId, NotificationType type, String content, String url) {
        Member worker = memberRepository.findById(receiverId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Notification notification = Notification.builder()
                .content(content)
                .url(url)
                .notificationType(type)
                .receiver(worker)
                .build();

        notificationRepository.save(notification);

        // todo: templateCode 변경
        // todo: type에 따라 알람 수신 여부가 다를 것!
        publisher.publishEvent(new AlarmEvent(content, worker.getId(), worker.getPhone(), "code"));
    }

    /**
     * 알림 목록 조회
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> findNotifications(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        return notificationRepository.findByMember(member.getId()).stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 알림 읽음 처리
     */
    public void readNotification(Long memberId, Long notificationId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Notification notification = notificationRepository.findByIdAndMember(member.getId(), notificationId)
                .orElseThrow(() -> new JikgongException(ErrorCode.NOTIFICATION_NOT_FOUND));

        // 읽음 처리
        notification.readNotification();
    }

    /**
     * 읽지 않은 알림 개수 조회
     */
    @Transactional(readOnly = true)
    public UnreadCountResponse unreadNotification(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        int unreadCount = notificationRepository.countUnreadNotification(member.getId());
        return UnreadCountResponse.builder().unreadCount(unreadCount).build();
    }

    /**
     * 알림 설정 정보 조회
     * 노동자일 경우, 기업일 경우
     */
    @Transactional(readOnly = true)
    public NotificationInfoResponse findNotificationInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        // 기업일 경우
        if (member.getRole() == Role.ROLE_COMPANY) {
            CompanyNotificationInfo companyNotificationInfo = member.getCompanyInfo().getCompanyNotificationInfo();
            return NotificationInfoResponse.from(companyNotificationInfo);
        }
        // 노동자일 경우
        if(member.getRole() == Role.ROLE_WORKER) {
            WorkerNotificationInfo workerNotificationInfo = member.getWorkerInfo().getWorkerNotificationInfo();
            return NotificationInfoResponse.from(workerNotificationInfo);
        }

        // 특정할 수 없는 경우
        throw new JikgongException(ErrorCode.ROLE_NOT_FOUND);
    }

    /**
     * 노동자 알림 정보 업데이트
     */
    public void updateNotificationInfoWorker(Long workerId, WorkerNotificationInfoRequest request) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        worker.getWorkerInfo().getWorkerNotificationInfo().updateInfo(request);
    }

    /**
     * 기업 알림 정보 업데이트
     */
    public void updateNotificationInfoCompany(Long companyId, CompanyNotificationInfoRequest request) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        company.getCompanyInfo().getCompanyNotificationInfo().updateInfo(request);
    }
}
