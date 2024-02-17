package jikgong.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.member.dtos.notification.NotificationInfoResponse;
import jikgong.domain.member.service.MemberService;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "알림 수신 여부 조회")
    @GetMapping("/api/member/notification")
    public ResponseEntity<Response> findMemberNotificationInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        NotificationInfoResponse memberNotificationInfo = memberService.findMemberNotificationInfo(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(memberNotificationInfo, "알림 수신 여부 조회 완료"));
    }

    @Operation(summary = "알림 수신 여부 변경")
    @PostMapping("/api/member/notifcation")
    public ResponseEntity<Response> updateNotificationInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        memberService.updateNotificationInfo(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response("알림 수신 여부 변경 완료"));
    }
}
