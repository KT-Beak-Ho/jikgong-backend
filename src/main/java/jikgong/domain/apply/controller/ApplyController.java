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
                                                           @RequestParam("status") ApplyStatus status) {
        List<ApplyResponseForWorker> applyResponseForWorkerList =
                applyService.findApplyHistoryWorker(principalDetails.getMember().getId(), status);
        return ResponseEntity.ok(new Response(applyResponseForWorkerList, "일자리 신청 내역 조회 완료"));
    }

    @Operation(summary = "기업: 공고 글에 신청된 내역 조회 - 대기 중인 요청")
    @GetMapping("/api/apply/company/pending/{jobPostId}")
    public ResponseEntity<Response> findPendingApplyCompany(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                            @PathVariable("jobPostId") Long jobPostId) {
        List<ApplyPendingResponseForCompany> applyResponseForCompanyList =
                applyService.findPendingApplyHistoryCompany(principalDetails.getMember().getId(), jobPostId);
        return ResponseEntity.ok(new Response(applyResponseForCompanyList, "공고 글에 신청된 내역 조회 완료"));
    }

    @Operation(summary = "기업: 공고 글에 확정된 노동자 조회")
    @PostMapping("/api/apply/company/accepted")
    public ResponseEntity<Response> findAcceptedApplyCompany(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                             @RequestBody AcceptedMemberRequest request) {

        List<MemberAcceptedResponse> memberAcceptedResponseList =
                applyService.findAcceptedHistoryCompany(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response(memberAcceptedResponseList, "공고 글에 확정된 노동자 조회"));
    }
}
