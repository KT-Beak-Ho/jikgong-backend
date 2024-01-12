package jikgong.domain.history.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.history.dtos.HistorySaveRequest;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.history.service.HistoryService;
import jikgong.domain.member.dtos.MemberAcceptedResponse;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HistoryController {
    private final HistoryService historyService;

    @Operation(summary = "출근 / 결근 선택")
    @PostMapping("/api/history")
    public ResponseEntity<Response> saveHistory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @RequestBody HistorySaveRequest request) {
        Long historyId = historyService.saveHistory(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("출근, 결근 결과 저장 완료"));
    }

    @Operation(summary = "출근 / 결근 조회", description = "ex) workDate: 2024-01-01")
    @GetMapping("/api/history")
    public ResponseEntity<Response> findHistoryMembers(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                       @RequestParam("isWork") Boolean isWork,
                                                       @RequestParam("jobPostId") Long jobPostId,
                                                       @RequestParam("workDate") LocalDate workDate,
                                                       @RequestParam(name = "page", defaultValue = "0") int page,
                                                       @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("m.workerInfo.workerName")));
        Page<MemberAcceptedResponse> memberAcceptedResponsePage = historyService.findHistoryMembers(principalDetails.getMember().getId(), jobPostId, workDate, isWork, pageable);
        return ResponseEntity.ok(new Response(memberAcceptedResponsePage, "출근 / 결근 조회 결과 반환 완료"));
    }
}
