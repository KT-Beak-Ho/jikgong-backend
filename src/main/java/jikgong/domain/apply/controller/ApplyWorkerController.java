package jikgong.domain.apply.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.List;
import jikgong.domain.apply.dto.worker.ApplyAcceptedGetResponse;
import jikgong.domain.apply.dto.worker.ApplyDailyGetResponse;
import jikgong.domain.apply.dto.worker.ApplyMonthlyGetResponse;
import jikgong.domain.apply.dto.worker.ApplySaveRequest;
import jikgong.domain.apply.service.ApplyWorkerService;
import jikgong.global.annotation.WorkerRoleRequired;
import jikgong.global.common.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@WorkerRoleRequired
public class ApplyWorkerController {

    private final ApplyWorkerService applyWorkerService;

    @Operation(summary = "노동자: 일자리 신청")
    @PostMapping("/api/apply/worker")
    public ResponseEntity<Response> saveApply(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody ApplySaveRequest request) {
        applyWorkerService.saveApply(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("일자리 신청 완료"));
    }

    @Operation(summary = "노동자: 신청 내역 월별 조회", description = "신청 내역 확정 화면의 달력 동그라미 표시할 날짜 반환  \n workMonth: 2024-01-01  << 이렇게 주면 년,월만 추출 예정")
    @GetMapping("/api/apply/worker/monthly")
    public ResponseEntity<Response> getAppliesMonthly(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      @RequestParam("workMonth") LocalDate workMonth) {
        List<ApplyMonthlyGetResponse> applyMonthlyGetResponseList = applyWorkerService.findAppliesPerMonth(
            principalDetails.getMember().getId(), workMonth);
        return ResponseEntity.ok(new Response(applyMonthlyGetResponseList, "일자리 신청 내역 조회 완료"));
    }

    @Operation(summary = "노동자: 신청 내역 일별 조회 - 확정", description = "workDate: 2024-01-01")
    @GetMapping("/api/apply/worker/accepted")
    public ResponseEntity<Response> getApplyAccepted(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                     @RequestParam("date") LocalDate date) {
        ApplyAcceptedGetResponse applyAcceptedGetResponse = applyWorkerService.findApplyHistoryPerDay(
                principalDetails.getMember().getId(), date);
        return ResponseEntity.ok(new Response(applyAcceptedGetResponse, "일자리 신청 내역 조회 완료"));
    }

    @Operation(summary = "노동자: 신청 내역 일별 조회 - 진행중")
    @GetMapping("/api/apply/worker/pending")
    public ResponseEntity<Response> getAppliesPending(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<ApplyDailyGetResponse> applyDailyGetResponseList = applyWorkerService.findAppliesPerDay(
            principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(applyDailyGetResponseList, "일자리 신청 내역 조회 완료"));
    }

    @Operation(summary = "노동자: 신청 내역 일별 조회 - 마감")
    @GetMapping("/api/apply/worker/closed")
    public ResponseEntity<Response> getAppliesClosed(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<ApplyDailyGetResponse> applyDailyGetResponseList = applyWorkerService.findAppliesPerDay(
                principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(applyDailyGetResponseList, "일자리 신청 내역 조회 완료"));
    }

    @Operation(summary = "노동자: 일자리 지원 취소", description = "지원 status가 수락됨, 대기중 일때만 가능")
    @DeleteMapping("/api/apply/worker/{applyId}")
    public ResponseEntity<Response> cancelApply(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("applyId") Long applyId) {
        applyWorkerService.cancelApply(principalDetails.getMember().getId(), applyId);
        return ResponseEntity.ok(new Response("일자리 취소 완료"));
    }
}
