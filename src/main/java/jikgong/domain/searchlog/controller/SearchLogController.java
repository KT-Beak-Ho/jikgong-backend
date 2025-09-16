package jikgong.domain.searchlog.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import jikgong.domain.searchlog.dto.SearchLogSaveRequest;
import jikgong.domain.searchlog.entity.SearchLog;
import jikgong.domain.searchlog.service.SearchLogService;
import jikgong.global.annotation.AuthenticatedRequired;
import jikgong.global.common.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[공통] 최근 검색 기록")
@RestController
@RequiredArgsConstructor
@Slf4j
@AuthenticatedRequired
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
        List<SearchLog> recentSearchLogList = searchLogService.findRecentSearchLogs(
            principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(recentSearchLogList, "최근 검색 기록 조회 완료"));
    }
}
