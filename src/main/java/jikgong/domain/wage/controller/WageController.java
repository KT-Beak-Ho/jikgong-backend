package jikgong.domain.wage.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.wage.dtos.DailyWageResponse;
import jikgong.domain.wage.dtos.WageDetailResponse;
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
        return ResponseEntity.ok(new Response("지급 내역 등록"));
    }

    @Operation(summary = "일별 지급 내역 조회", description = "selectDay 예시: 2023-12-25T00:00:00")
    @GetMapping("/api/wages")
    public ResponseEntity<Response> findDailyWageHistory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                         @DateTimeFormat (pattern = "yyyy-MM-dd'T'HH:mm:ss")
                                                         @RequestParam("selectDay") LocalDateTime selectDay) {
        DailyWageResponse dailyWageResponse = wageService.findDailyWageHistory(principalDetails.getMember().getId(), selectDay);
        return ResponseEntity.ok(new Response(dailyWageResponse, "일별 지급 내역 반환"));
    }
}
