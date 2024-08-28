package jikgong.domain.apply.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.apply.dto.company.ApplyManageResponse;
import jikgong.domain.apply.dto.company.ApplyProcessRequest;
import jikgong.domain.apply.service.ApplyCompanyService;
import jikgong.global.annotation.CompanyRoleRequired;
import jikgong.global.common.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@CompanyRoleRequired
public class ApplyCompanyController {

    private final ApplyCompanyService applyCompanyService;

    @Operation(summary = "인력 관리: 대기 중인 노동자 조회")
    @GetMapping("/api/apply/company/pending/{jobPostId}/{workDateId}")
    public ResponseEntity<Response> findPendingApplyCompany(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("jobPostId") Long jobPostId,
        @PathVariable("workDateId") Long workDateId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size) {
        // 페이징 처리 (먼저 요청한 순)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("createdDate")));
        Page<ApplyManageResponse> applyManageResponsePage =
            applyCompanyService.findPendingApplyHistoryCompany(principalDetails.getMember().getId(), jobPostId,
                workDateId, pageable);
        return ResponseEntity.ok(new Response(applyManageResponsePage, "공고 글에 신청된 내역 조회 완료"));
    }

    @Operation(summary = "인력 관리: 확정된 노동자 조회")
    @GetMapping("/api/apply/company/accepted/{jobPostId}/{workDateId}")
    public ResponseEntity<Response> findAcceptedApplyCompany(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("jobPostId") Long jobPostId,
        @PathVariable("workDateId") Long workDateId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size) {
        // 페이징 처리 (이름 순)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("m.workerInfo.workerName")));
        Page<ApplyManageResponse> applyManageResponsePage =
            applyCompanyService.findAcceptedHistoryCompany(principalDetails.getMember().getId(), jobPostId, workDateId,
                pageable);
        return ResponseEntity.ok(new Response(applyManageResponsePage, "공고 글에 확정된 노동자 조회"));
    }


    @Operation(summary = "인력 관리: 인부 요청 처리")
    @PostMapping("/api/apply/company/process-request")
    public ResponseEntity<Response> processApply(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody ApplyProcessRequest request) {
        applyCompanyService.processApply(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("인부 요청 처리 완료"));
    }
}
