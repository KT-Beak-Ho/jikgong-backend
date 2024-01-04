package jikgong.domain.jobPost.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.jobPost.dtos.JobPostApplyHistoryResponse;
import jikgong.domain.jobPost.dtos.JobPostSaveRequest;
import jikgong.domain.jobPost.service.JobPostService;
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
public class JobController {
    private final JobPostService jobPostService;

    @Operation(summary = "모집 공고 등록")
    @PostMapping("/api/job-post")
    public ResponseEntity<Response> saveJobPost(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @RequestBody JobPostSaveRequest request) {
        Long jobPostId = jobPostService.saveJobPost(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("모집 공고 등록 완료"));
    }

    @Operation(summary = "등록한 모집 공고 조회", description = "노동자 신청을 받기 전 본인이 등록한 공고를 조회 하고, 그 공고에 등록된 신청을 따로 보기 위해 만든 api")
    @GetMapping("/api/job-posts")
    public ResponseEntity<Response> findJobPosts(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        //todo: 필터링 추가
        List<JobPostApplyHistoryResponse> jobPostApplyHistoryResponseList =
                jobPostService.findJobPostsByMemberId(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(jobPostApplyHistoryResponseList, "등록한 모집 공고 조회"));
    }
}
