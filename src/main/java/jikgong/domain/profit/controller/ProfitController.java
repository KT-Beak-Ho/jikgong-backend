package jikgong.domain.profit.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.profit.dtos.graph.DailyGraphResponse;
import jikgong.domain.profit.dtos.graph.MonthlyGraphResponse;
import jikgong.domain.profit.dtos.history.*;
import jikgong.domain.profit.service.ProfitService;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProfitController {
    private final ProfitService profitService;

    @Operation(summary = "지금 내역 등록")
    @PostMapping("/api/profit")
    public ResponseEntity<Response> saveProfitHistory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @RequestBody ProfitSaveRequest request) {
        Long profitId = profitService.saveProfitHistory(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("지급 내역 등록 완료"));
    }

    @Operation(summary = "수익금 내역 (캘린더, 일별)", description = "selectDay 예시: 2024-01-01")
    @GetMapping("/api/profits")
    public ResponseEntity<Response> findDailyProfitHistory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                         @RequestParam("selectDay") LocalDate selectDay) {
        List<DailyProfitResponse> dailyProfitResponseList = profitService.findDailyProfitHistory(principalDetails.getMember().getId(), selectDay);
        return ResponseEntity.ok(new Response(dailyProfitResponseList, "일별 지급 내역 반환 완료"));
    }

    @Operation(summary = "수익금 내역 (캘린더, 월별)", description = "selectMonth 예시: 2024-01-01")
    @GetMapping("/api/profits/calendar/month")
    public ResponseEntity<Response> findMonthlyProfitHistoryCalendar(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                         @RequestParam("selectMonth") LocalDate selectMonth) {
        MonthlyProfitResponse monthlyProfitResponse = profitService.findMonthlyProfitHistoryCalendar(principalDetails.getMember().getId(), selectMonth);
        return ResponseEntity.ok(new Response(monthlyProfitResponse, "월 별 근무일 & 월 수입 합계"));
    }

    @Operation(summary = "지급 내역 삭제")
    @DeleteMapping("/api/profit/{profitId}")
    public ResponseEntity<Response> deleteProfitHistory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      @PathVariable("profitId") Long profitId) {
        profitService.deleteProfitHistory(principalDetails.getMember().getId(), profitId);
        return ResponseEntity.ok(new Response("지급 내역 삭제 완료"));
    }

    @Operation(summary = "지급 내역 수정")
    @PutMapping("/api/profit")
    public ResponseEntity<Response> ModifyProfitHistory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      @RequestBody ProfitModifyRequest request) {
        profitService.modifyProfitHistory(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("지급 내역 수정 완료"));
    }

    @Operation(summary = "수익금 내역 (리스트)")
    @GetMapping("/api/profits/list")
    public ResponseEntity<Response> findProfitHistoryList(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<DailyProfitResponse> dailyProfitResponseList = profitService.findProfitHistoryList(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(dailyProfitResponseList, "수익금 내역 (리스트) 반환 완료"));
    }

    @Operation(summary = "총 수익금 및 근무시간")
    @GetMapping("/api/profit/summary-info")
    public ResponseEntity<Response> findSummaryInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        ProfitSummaryInfoResponse summaryInfo = profitService.findSummaryInfo(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(summaryInfo, "수익금 내역 (리스트) 반환 완료"));
    }

    @Operation(summary = "근무 시간 그래프: 일별")
    @GetMapping("/api/profit/graph-info/daily")
    public ResponseEntity<Response> findDailyGraphInfo(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                  @RequestParam(name = "selectMonth") LocalDate selectMonth) {
        DailyGraphResponse dailyGraphResponse = profitService.findDailyGraphInfo(principalDetails.getMember().getId(), selectMonth);
        return ResponseEntity.ok(new Response(dailyGraphResponse, "근무 시간 일별 그래프"));
    }

    @Operation(summary = "근무 시간 그래프: 월별")
    @GetMapping("/api/profit/graph-info/monthly")
    public ResponseEntity<Response> findMonthlyGraphInfo(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                       @RequestParam(name = "selectYear") LocalDate selectYear) {
        MonthlyGraphResponse monthlyGraphResponse = profitService.findMonthlyGraphInfo(principalDetails.getMember().getId(), selectYear);
        return ResponseEntity.ok(new Response(monthlyGraphResponse, "근무 시간 일별 그래프"));
    }
}
