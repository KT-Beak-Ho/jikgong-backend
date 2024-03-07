package jikgong.domain.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.notification.dtos.NotificationResponse;
import jikgong.domain.notification.dtos.UnreadCountResponse;
import jikgong.domain.notification.service.NotificationService;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "받은 알림 조회")
    @GetMapping("/api/notifications")
    public ResponseEntity<Response> findNotifications(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<NotificationResponse> notificationResponseList = notificationService.findNotifications(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(notificationResponseList, "받은 알림 조회 완료"));
    }

    @Operation(summary = "알림 읽음 처리")
    @PostMapping("/api/notification/{notificationId}")
    public ResponseEntity<Response> readNotification(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                     @PathVariable("notificationId") Long notificationId) {
        notificationService.readNotification(principalDetails.getMember().getId(), notificationId);
        return ResponseEntity.ok(new Response("앍음 읽음 처리 완료"));
    }

    @Operation(summary = "읽지 않은 알림 개수")
    @GetMapping("/api/notification/unread-count")
    public ResponseEntity<Response> unreadNotification(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        UnreadCountResponse unreadCountResponse = notificationService.unreadNotification(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(unreadCountResponse, "읽지 않은 알림 개수 반환"));
    }
}
