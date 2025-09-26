package jikgong.domain.apply.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jikgong.domain.apply.dto.company.ApplyManageResponse;
import jikgong.domain.apply.dto.company.ApplyProcessRequest;
import jikgong.domain.apply.entity.ApplyStatus;
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

@Tag(name = "[사업자] 인력 관리")
@RestController
@RequiredArgsConstructor
@Slf4j
@CompanyRoleRequired
public class ApplyCompanyController {

    private final ApplyCompanyService applyCompanyService;

    @Operation(summary = "모집 공고에 대한 일자리 신청 내역 조회")
    @GetMapping("/api/apply/company/{jobPostId}")
    public ResponseEntity<Response> findPendingApplyCompany(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                            @PathVariable("jobPostId") Long jobPostId,
                                                            @RequestParam(name = "page", defaultValue = "0") int page,
                                                            @RequestParam(name = "size", defaultValue = "10") int size,
                                                            @RequestParam(required = false, name = "status") ApplyStatus status) {
        Long companyId = principalDetails.getMember().getId();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("createdDate"))); // 페이징 처리 (먼저 요청한 순)

        Page<ApplyManageResponse> applyManageResponsePage = applyCompanyService.findApplyHistoryCompany(
                companyId,
                jobPostId,
                status,
                pageable);
        return ResponseEntity.ok(new Response(applyManageResponsePage, "모집 공고에 대한 신청 내역 조회 완료"));
    }


    @Operation(summary = "인부 요청 처리")
    @PostMapping("/api/apply/company/process-request")
    public ResponseEntity<Response> processApply(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody ApplyProcessRequest request) {
        applyCompanyService.processApply(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("인부 요청 처리 완료"));
    }
}
