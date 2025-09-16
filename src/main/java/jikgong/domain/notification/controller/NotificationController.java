package jikgong.domain.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import jikgong.domain.notification.dto.NotificationInfoResponse;
import jikgong.domain.notification.dto.NotificationResponse;
import jikgong.domain.notification.dto.UnreadCountResponse;
import jikgong.domain.notification.dto.company.CompanyNotificationInfoRequest;
import jikgong.domain.notification.dto.worker.WorkerNotificationInfoRequest;
import jikgong.domain.notification.service.NotificationService;
import jikgong.global.annotation.AuthenticatedRequired;
import jikgong.global.annotation.CompanyRoleRequired;
import jikgong.global.annotation.WorkerRoleRequired;
import jikgong.global.common.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="[공통] 알림")
@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "받은 알림 리스트 조회")
    @GetMapping("/api/notification/list")
    @AuthenticatedRequired
    public ResponseEntity<Response> findNotifications(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<NotificationResponse> notificationResponseList = notificationService.findNotifications(
            principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(notificationResponseList, "받은 알림 조회 완료"));
    }

    @Operation(summary = "알림 읽음 처리")
    @PostMapping("/api/notification/{notificationId}")
    @AuthenticatedRequired
    public ResponseEntity<Response> readNotification(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("notificationId") Long notificationId) {
        notificationService.readNotification(principalDetails.getMember().getId(), notificationId);
        return ResponseEntity.ok(new Response("앍음 읽음 처리 완료"));
    }

    @Operation(summary = "읽지 않은 알림 개수")
    @GetMapping("/api/notification/unread-count")
    @AuthenticatedRequired
    public ResponseEntity<Response> unreadNotification(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        UnreadCountResponse unreadCountResponse = notificationService.unreadNotification(
            principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(unreadCountResponse, "읽지 않은 알림 개수 반환"));
    }

    @Operation(summary = "노동자 or 기업의 알림 설정 정보 조회")
    @GetMapping("/api/notification/setting-info")
    @AuthenticatedRequired
    public ResponseEntity<Response> findNotificationInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        NotificationInfoResponse notificationInfoResponse = notificationService.findNotificationInfo(
            principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(notificationInfoResponse, "알림 설정 정보 조회 완료"));
    }

    @Operation(summary = "노동자: 알림 설정 수정")
    @PutMapping("/api/notification/worker/setting")
    @WorkerRoleRequired
    public ResponseEntity<Response> updateWorkerNotificationInfo(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody WorkerNotificationInfoRequest request) {
        notificationService.updateNotificationInfoWorker(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("노동자: 알림 설정 수정 완료"));
    }

    @Operation(summary = "기업: 알림 설정 수정")
    @PutMapping("/api/notification/company/setting")
    @CompanyRoleRequired
    public ResponseEntity<Response> updateCompanyNotificationInfo(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody CompanyNotificationInfoRequest request) {
        notificationService.updateNotificationInfoCompany(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("기업: 알림 설정 수정 완료"));
    }
}
