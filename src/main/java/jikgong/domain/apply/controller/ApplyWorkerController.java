package jikgong.domain.apply.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import jikgong.domain.apply.dto.worker.*;
import jikgong.domain.apply.service.ApplyWorkerService;
import jikgong.global.annotation.WorkerRoleRequired;
import jikgong.global.common.Response;
import jikgong.global.security.principal.PrincipalDetails;
import jikgong.global.utils.TimeTransfer;
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

@Tag(name = "[노동자] 일자리 신청")
@RestController
@RequiredArgsConstructor
@Slf4j
@WorkerRoleRequired
public class ApplyWorkerController {

    private final ApplyWorkerService applyWorkerService;

    @Operation(summary = "신규 일자리 신청 생성")
    @PostMapping("/api/apply/worker")
    public ResponseEntity<Response> saveApply(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody ApplySaveRequest request) {
        applyWorkerService.saveApply(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("일자리 신청 완료"));
    }

    @Operation(summary = "상태 정보 조회", description = "신청 내역의 상태 정보를 날짜와 함께 반환함. **모든 파라미터가 없어도 정상 동작**함.")
    @GetMapping("/api/apply/worker/status")
    public ResponseEntity<Response> getApplyStatus(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   ApplyGetRequest request) {
        List<ApplyStatusGetResponse> applyStatusGetResponseList = applyWorkerService.findApplyStatus(
                principalDetails.getMember().getId(),
                request);

        return ResponseEntity.ok(new Response(applyStatusGetResponseList, "일자리 신청 내역 상태 조회 완료"));
    }

    @Operation(summary = "상태 정보 월별 조회", description = "신청 내역 확정 화면의 달력 동그라미 표시할 날짜 반환  \n workMonth: 2024-01-01  << 이렇게 주면 년,월만 추출 예정")
    @GetMapping("/api/apply/worker/status/monthly")
    public ResponseEntity<Response> getApplyStatusMonthly(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                          @RequestParam("workMonth") LocalDate workMonth) {
        ApplyGetRequest request = ApplyGetRequest.builder()
                .startWorkDate(TimeTransfer.getFirstDayOfMonth(workMonth))
                .endWorkDate(TimeTransfer.getLastDayOfMonth(workMonth))
                .build();

        List<ApplyStatusGetResponse> applyStatusGetResponseList = applyWorkerService.findApplyStatus(
                principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response(applyStatusGetResponseList, "일자리 신청 내역 상태 조회 완료"));
    }

    @Operation(summary = "일별 조회")
    @GetMapping("/api/apply/worker/daily")
    public ResponseEntity<Response> getAppliesDaily(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                               ApplyDailyGetRequest request) {

        List<ApplyDailyGetResponse> response = applyWorkerService.findAppliesDaily(
                principalDetails.getMember().getId(),
                request);
        return ResponseEntity.ok(new Response(response, "일자리 신청 내역 조회 완료"));
    }

    @Operation(summary = "일자리 신청 취소", description = "지원 status가 수락됨, 대기중 일때만 가능")
    @DeleteMapping("/api/apply/worker/{applyId}")
    public ResponseEntity<Response> cancelApply(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("applyId") Long applyId) {
        applyWorkerService.cancelApply(principalDetails.getMember().getId(), applyId);
        return ResponseEntity.ok(new Response("일자리 취소 완료"));
    }
}
