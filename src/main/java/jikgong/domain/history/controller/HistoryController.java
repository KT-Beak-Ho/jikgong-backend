package jikgong.domain.history.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import jikgong.domain.history.dto.*;
import jikgong.domain.history.service.HistoryService;
import jikgong.global.annotation.CompanyRoleRequired;
import jikgong.global.common.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name="[사업자] 근무 기록")
@RestController
@RequiredArgsConstructor
@Slf4j
@CompanyRoleRequired
public class HistoryController {

    private final HistoryService historyService;

    @Operation(summary = "근무 기록 목록 조회")
    @GetMapping("/api/history/company/{jobPostId}/{workDateId}")
    public ResponseEntity<Response> getHistories(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @PathVariable("jobPostId") Long jobPostId,
                                                 @PathVariable("workDateId") Long workDateId) {
        Long companyId = principalDetails.getMember().getId();

        List<HistoryManageResponse> response = historyService.findHistories(companyId, jobPostId, workDateId);
        return ResponseEntity.ok(new Response(response, "근무 기록 목록 반환 완료"));
    }

    @Operation(summary = "지급 내역서 확인")
    @GetMapping("/api/history/payment-statement/{jobPostId}/{workDateId}")
    public ResponseEntity<Response> findPaymentStatement(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("jobPostId") Long jobPostId,
        @PathVariable("workDateId") Long workDateId) {
        PaymentStatementResponse paymentStatementResponse = historyService.findPaymentStatement(
            principalDetails.getMember().getId(), jobPostId, workDateId);
        return ResponseEntity.ok(new Response(paymentStatementResponse, "지급 내역서 확인 정보 반환 완료"));
    }

    @Operation(summary = "출근 / 결근 선택")
    @PutMapping("/api/history/company/start")
    public ResponseEntity<Response> updateHistoryAtStart(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                         @RequestBody HistoryStartSaveRequest request) {
        int saveCount = historyService.saveHistoryAtStart(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("출근, 결근 결과 저장 완료"));
    }

    @Operation(summary = "조퇴 / 퇴근 선택")
    @PutMapping("/api/history/company/finish")
    public ResponseEntity<Response> updateHistoryAtFinish(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                          @RequestBody HistoryFinishSaveRequest request) {
        int updateCount = historyService.updateHistoryAtFinish(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("조퇴, 퇴근 결과 저장 완료"));
    }

    @Operation(summary = "근무 기록 수정", description = "historyId를 제외한 모든 파라미터는 생략 가능하며, 그런 경우 기존의 데이터를 유지함.")
    @PutMapping("/api/history/company/{historyId}")
    public ResponseEntity<Response> putHistory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                               @PathVariable(name = "historyId") Long historyId,
                                               HistoryPutRequest request) {
        Long companyId = principalDetails.getMember().getId();
        historyService.updateHistory(companyId, historyId, request);
        return ResponseEntity.ok(new Response("근무 기록 수정 완료"));
    }
}
