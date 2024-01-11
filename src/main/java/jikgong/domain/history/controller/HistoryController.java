package jikgong.domain.history.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.history.dtos.HistorySaveRequest;
import jikgong.domain.history.service.HistoryService;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HistoryController {
    private final HistoryService historyService;

    @Operation(summary = "출근 / 결근 선택")
    @PostMapping("/api/history")
    public ResponseEntity<Response> saveHistory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @RequestBody HistorySaveRequest request) {
        Long historyId = historyService.saveHistory(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("출근, 결근 결과 저장 완료"));
    }
}
