package jikgong.global.fcm;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.global.common.Response;
import jikgong.global.fcm.dto.FCMNotificationRequestDto;
import jikgong.global.fcm.service.FCMNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/fcm")
@RequiredArgsConstructor
public class FCMTestController {
    private final FCMNotificationService fcmNotificationService;

    @Operation(summary = "알림 보내기 위한 테스트 api")
    @PostMapping("/notification")
    public ResponseEntity<Response> sendNotification(@RequestBody FCMNotificationRequestDto requestDto) {
        fcmNotificationService.sendNotificationByToken(requestDto);
        fcmNotificationService.sendNotificationByToken(requestDto);
        return ResponseEntity.ok(new Response("알림 전송 완료"));
    }
}
