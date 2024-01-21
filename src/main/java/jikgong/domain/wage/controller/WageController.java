package jikgong.domain.wage.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.wage.dtos.DailyWageResponse;
import jikgong.domain.wage.dtos.MonthlyWageResponse;
import jikgong.domain.wage.dtos.WageModifyRequest;
import jikgong.domain.wage.dtos.WageSaveRequest;
import jikgong.domain.wage.service.WageService;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
}
