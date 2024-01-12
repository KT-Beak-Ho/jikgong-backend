package jikgong.domain.apply.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.apply.dtos.AcceptedMemberRequest;
import jikgong.domain.apply.dtos.ApplyPendingResponseForCompany;
import jikgong.domain.apply.dtos.ApplyResponseForWorker;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.apply.service.ApplyService;
import jikgong.domain.member.dtos.MemberAcceptedResponse;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApplyController {
    private final ApplyService applyService;

    @Operation(summary = "노동자: 일자리 신청")
    @PostMapping("/api/apply/{jobPostId}")
    public ResponseEntity<Response> saveApply(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                              @PathVariable("jobPostId") Long jobPostId) {
        Long applyId = applyService.saveApply(principalDetails.getMember().getId(), jobPostId);
        return ResponseEntity.ok(new Response("일자리 신청 완료"));
    }

    @Operation(summary = "노동자: 일자리 신청 내역 조회")
    @GetMapping("/api/apply/worker")
    public ResponseEntity<Response> findApplyHistoryWorker(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                           @RequestParam("status") ApplyStatus status,
                                                           @RequestParam(name = "page", defaultValue = "0") int page,
                                                           @RequestParam(name = "size", defaultValue = "10") int size) {
        // 페이징 처리 (먼저 요청한 순)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("createdDate")));
        Page<ApplyResponseForWorker> applyResponseForWorkerPage =
                applyService.findApplyHistoryWorker(principalDetails.getMember().getId(), status, pageable);
        return ResponseEntity.ok(new Response(applyResponseForWorkerPage, "일자리 신청 내역 조회 완료"));
    }

    // todo: 일자리 신청 취소 프로세스 확정 안 남

    @Operation(summary = "인력 관리: 공고 글에 신청된 내역 조회 - 대기 중인 요청")
    @GetMapping("/api/apply/company/pending/{jobPostId}")
    public ResponseEntity<Response> findPendingApplyCompany(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                            @PathVariable("jobPostId") Long jobPostId,
                                                            @RequestParam(name = "page", defaultValue = "0") int page,
                                                            @RequestParam(name = "size", defaultValue = "10") int size) {
        // 페이징 처리 (먼저 요청한 순)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("createdDate")));
        Page<ApplyPendingResponseForCompany> applyResponseForCompanyPage =
                applyService.findPendingApplyHistoryCompany(principalDetails.getMember().getId(), jobPostId, pageable);
        return ResponseEntity.ok(new Response(applyResponseForCompanyPage, "공고 글에 신청된 내역 조회 완료"));
    }

    @Operation(summary = "인력 관리: 공고 글에 확정된 노동자 조회")
    @PostMapping("/api/apply/company/accepted")
    public ResponseEntity<Response> findAcceptedApplyCompany(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                             @RequestBody AcceptedMemberRequest request,
                                                             @RequestParam(name = "page", defaultValue = "0") int page,
                                                             @RequestParam(name = "size", defaultValue = "10") int size) {
        // 페이징 처리 (이름 순)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("workerName")));
        Page<MemberAcceptedResponse> memberAcceptedResponsePage =
                applyService.findAcceptedHistoryCompany(principalDetails.getMember().getId(), request, pageable);
        return ResponseEntity.ok(new Response(memberAcceptedResponsePage, "공고 글에 확정된 노동자 조회"));
    }
}
