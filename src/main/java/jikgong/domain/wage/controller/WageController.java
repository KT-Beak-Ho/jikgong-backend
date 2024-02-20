package jikgong.domain.wage.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.wage.dtos.graph.DailyGraphResponse;
import jikgong.domain.wage.dtos.graph.MonthlyGraphResponse;
import jikgong.domain.wage.dtos.history.*;
import jikgong.domain.wage.service.WageService;
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
public class WageController {
    private final WageService wageService;

    @Operation(summary = "지금 내역 등록")
    @PostMapping("/api/wage")
    public ResponseEntity<Response> saveWageHistory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @RequestBody WageSaveRequest request) {
        Long wageId = wageService.saveWageHistory(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("지급 내역 등록 완료"));
    }

    @Operation(summary = "수익금 내역 (캘린더, 일별)", description = "selectDay 예시: 2024-01-01")
    @GetMapping("/api/wages")
    public ResponseEntity<Response> findDailyWageHistory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                         @RequestParam("selectDay") LocalDate selectDay) {
        List<DailyWageResponse> dailyWageResponseList = wageService.findDailyWageHistory(principalDetails.getMember().getId(), selectDay);
        return ResponseEntity.ok(new Response(dailyWageResponseList, "일별 지급 내역 반환 완료"));
    }

    @Operation(summary = "수익금 내역 (캘린더, 월별)", description = "selectMonth 예시: 2024-01-01")
    @GetMapping("/api/wages/calendar/month")
    public ResponseEntity<Response> findMonthlyWageHistoryCalendar(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                         @RequestParam("selectMonth") LocalDate selectMonth) {
        MonthlyWageResponse dailyWageResponse = wageService.findMonthlyWageHistoryCalendar(principalDetails.getMember().getId(), selectMonth);
        return ResponseEntity.ok(new Response(dailyWageResponse, "월 별 근무일 & 월 수입 합계"));
    }

    @Operation(summary = "지급 내역 삭제")
    @DeleteMapping("/api/wage/{wageId}")
    public ResponseEntity<Response> deleteWageHistory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      @PathVariable("wageId") Long wageId) {
        wageService.deleteWageHistory(principalDetails.getMember().getId(), wageId);
        return ResponseEntity.ok(new Response("지급 내역 삭제 완료"));
    }

    @Operation(summary = "지급 내역 수정")
    @PutMapping("/api/wage/{wageId}")
    public ResponseEntity<Response> ModifyWageHistory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      @RequestBody WageModifyRequest request) {
        wageService.modifyWageHistory(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("지급 내역 수정 완료"));
    }

    @Operation(summary = "수익금 내역 (리스트)")
    @GetMapping("/api/wages/list")
    public ResponseEntity<Response> findWageHistoryList(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<DailyWageResponse> dailyWageResponseList = wageService.findWageHistoryList(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(dailyWageResponseList, "수익금 내역 (리스트) 반환 완료"));
    }

    @Operation(summary = "총 수익금 및 근무시간")
    @GetMapping("/api/wage/summary-info")
    public ResponseEntity<Response> findSummaryInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        WageSummaryInfoResponse summaryInfo = wageService.findSummaryInfo(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(summaryInfo, "수익금 내역 (리스트) 반환 완료"));
    }

    @Operation(summary = "근무 시간 그래프: 일별")
    @GetMapping("/api/wage/graph-info/daily")
    public ResponseEntity<Response> findDailyGraphInfo(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                  @RequestParam(name = "selectMonth") LocalDate selectMonth) {
        DailyGraphResponse wageDailyGraphResponse = wageService.findDailyGraphInfo(principalDetails.getMember().getId(), selectMonth);
        return ResponseEntity.ok(new Response(wageDailyGraphResponse, "근무 시간 일별 그래프"));
    }

    @Operation(summary = "근무 시간 그래프: 월별")
    @GetMapping("/api/wage/graph-info/monthly")
    public ResponseEntity<Response> findMonthlyGraphInfo(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                       @RequestParam(name = "selectYear") LocalDate selectYear) {
        MonthlyGraphResponse wage = wageService.findMonthlyGraphInfo(principalDetails.getMember().getId(), selectYear);
        return ResponseEntity.ok(new Response(monthlyGraphInfo, "근무 시간 일별 그래프"));
    }
}
