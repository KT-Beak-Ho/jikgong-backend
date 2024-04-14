package jikgong.domain.history.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.history.dtos.HistoryAtFinishResponse;
import jikgong.domain.history.dtos.HistoryFinishSaveRequest;
import jikgong.domain.history.dtos.HistoryStartSaveRequest;
import jikgong.domain.history.dtos.PaymentStatementResponse;
import jikgong.domain.history.service.HistoryService;
import jikgong.domain.member.dtos.history.MemberAcceptedResponse;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HistoryController {
    private final HistoryService historyService;

    @Operation(summary = "출근 / 결근 선택")
    @PostMapping("/api/history/start")
    public ResponseEntity<Response> saveHistoryAtStart(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @RequestBody HistoryStartSaveRequest request) {
        int saveCount = historyService.saveHistoryAtStart(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("출근, 결근 결과 저장 완료"));
    }

    @Operation(summary = "조퇴 / 퇴근 선택")
    @PostMapping("/api/history/finish")
    public ResponseEntity<Response> updateHistoryAtFinish(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @RequestBody HistoryFinishSaveRequest request) {
        int updateCount = historyService.updateHistoryAtFinish(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("출근, 결근 결과 저장 완료"));
    }

    @Operation(summary = "인력 관리: 출근 / 결근 조회")
    @GetMapping("/api/history/start/{jobPostId}/{workDateId}")
    public ResponseEntity<Response> findHistoryAtStart(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                       @PathVariable("jobPostId") Long jobPostId,
                                                       @PathVariable("workDateId") Long workDateId) {
        List<MemberAcceptedResponse> memberAcceptedResponsePage = historyService.findApplyWithHistoryAtStart(principalDetails.getMember().getId(), jobPostId, workDateId);
        return ResponseEntity.ok(new Response(memberAcceptedResponsePage, "출근 / 결근 조회 결과 반환 완료"));
    }

    @Operation(summary = "인력 관리: 퇴근 / 조퇴 조회")
    @GetMapping("/api/history/finish/{jobPostId}/{workDateId}")
    public ResponseEntity<Response> findHistoryAtFinish(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                       @PathVariable("jobPostId") Long jobPostId,
                                                       @PathVariable("workDateId") Long workDateId) {
        HistoryAtFinishResponse historyAtFinishResponse = historyService.findHistoryAtFinish(principalDetails.getMember().getId(), jobPostId, workDateId);
        return ResponseEntity.ok(new Response(historyAtFinishResponse, "퇴근 / 조퇴 조회 결과 반환 완료"));
    }

    @Operation(summary = "지급 내역서 확인")
    @GetMapping("/api/history/payment-statement/{jobPostId}/{workDateId}")
    public ResponseEntity<Response> findPaymentStatement(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                         @PathVariable("jobPostId") Long jobPostId,
                                                         @PathVariable("workDateId") Long workDateId) {
        PaymentStatementResponse paymentStatementResponse = historyService.findPaymentStatement(principalDetails.getMember().getId(), jobPostId, workDateId);
        return ResponseEntity.ok(new Response(paymentStatementResponse, "지급 내역서 확인 정보 반환 완료"));
    }
}
