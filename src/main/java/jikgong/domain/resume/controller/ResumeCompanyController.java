package jikgong.domain.resume.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.resume.dtos.company.ResumeDetailResponse;
import jikgong.domain.offer.entity.SortType;
import jikgong.domain.resume.dtos.company.ResumeListResponse;
import jikgong.domain.resume.service.ResumeCompanyService;
import jikgong.global.common.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ResumeCompanyController {
    private final ResumeCompanyService resumeCompanyService;

    @Operation(summary = "기업: 이력서 목록 조회")
    @GetMapping("/api/resume/company/list")
    public ResponseEntity<Response> findResumeList(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   @RequestParam(name = "projectId") Long projectId,
                                                   @RequestParam(name = "tech", required = false) Tech tech,
                                                   @RequestParam(name = "bound", required = false) Float bound,
                                                   @RequestParam(name = "sortType", required = false) SortType sortType,
                                                   @RequestParam(name = "page", defaultValue = "0") int page,
                                                   @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ResumeListResponse> headHuntingListResponsePage = resumeCompanyService.findResumeList(principalDetails.getMember().getId(), projectId, tech, bound, sortType, pageable);
        return ResponseEntity.ok(new Response(headHuntingListResponsePage, "기업: 이력서 목록 조회 완료"));
    }

    @Operation(summary = "기업: 이력서 상세 정보")
    @GetMapping("/api/resume/company/{resumeId}")
    public ResponseEntity<Response> findWorkerInfo(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   @PathVariable("resumeId") Long resumeId) {
        ResumeDetailResponse resumeDetailResponse = resumeCompanyService.findResumeDetail(principalDetails.getMember().getId(), resumeId);
        return ResponseEntity.ok(new Response(resumeDetailResponse, "기업: 이력서 상세 정보 조회 완료"));
    }
}