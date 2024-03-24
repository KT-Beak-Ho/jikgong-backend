package jikgong.domain.resume.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.offer.entity.SortType;
import jikgong.domain.resume.dtos.ResumeListResponse;
import jikgong.domain.resume.dtos.ResumeSaveRequest;
import jikgong.domain.resume.service.ResumeService;
import jikgong.global.dto.Response;
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
public class ResumeController {
    private final ResumeService resumeService;

    @Operation(summary = "노동자: 이력서 등록 (헤드헌팅 노출)")
    @PostMapping("/api/resume")
    public ResponseEntity<Response> saveResume(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @RequestBody ResumeSaveRequest request) {
        resumeService.saveResume(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("이력서 등록 완료"));
    }

    @Operation(summary = "기업: 이력서 목록 조회 (헤드헌팅)")
    @GetMapping("/api/resumes")
    public ResponseEntity<Response> findResumeList(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                        @RequestParam(name = "projectId") Long projectId,
                                                        @RequestParam(name = "tech", required = false) Tech tech,
                                                        @RequestParam(name = "bound", required = false) Float bound,
                                                        @RequestParam(name = "sortType", required = false) SortType sortType,
                                                        @RequestParam(name = "page", defaultValue = "0") int page,
                                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ResumeListResponse> headHuntingListResponsePage = resumeService.findResumeList(principalDetails.getMember().getId(), projectId, tech, bound, sortType, pageable);
        return ResponseEntity.ok(new Response(headHuntingListResponsePage, "기업: 헤드헌팅 노동자 조회 완료"));
    }
}
