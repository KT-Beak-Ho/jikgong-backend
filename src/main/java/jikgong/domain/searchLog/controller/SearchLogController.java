package jikgong.domain.searchLog.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.searchLog.entity.SearchLog;
import jikgong.domain.searchLog.service.SearchLogService;
import jikgong.domain.searchLog.dtos.SearchLogSaveRequest;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SearchLogController {
    private final SearchLogService searchLogService;

    @Operation(summary = "최근 검색 기록: 저장")
    @PostMapping("/api/searchLog")
    public ResponseEntity<Response> saveRecentSearchLog(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                        @RequestBody SearchLogSaveRequest request) {
        searchLogService.saveRecentSearchLog(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("최근 검색 기록 저장 완료"));
    }

    @Operation(summary = "최근 검색 기록: 조회")
    @GetMapping("/api/searchLog/list")
    public ResponseEntity<Response> findRecentSearchLog(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<SearchLog> recentSearchLogList = searchLogService.findRecentSearchLogs(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(recentSearchLogList, "최근 검색 기록 조회 완료"));
    }
}
