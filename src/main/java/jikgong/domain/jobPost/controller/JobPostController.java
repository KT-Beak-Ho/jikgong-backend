package jikgong.domain.jobPost.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.jobPost.dtos.JobPostApplyHistoryResponse;
import jikgong.domain.jobPost.dtos.JobPostListResponse;
import jikgong.domain.jobPost.dtos.JobPostSaveRequest;
import jikgong.domain.jobPost.entity.JobPostStatus;
import jikgong.domain.jobPost.service.JobPostService;
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
public class JobPostController {
    private final JobPostService jobPostService;

    @Operation(summary = "모집 공고 등록")
    @PostMapping("/api/company/job-post")
    public ResponseEntity<Response> saveJobPost(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @RequestBody JobPostSaveRequest request) {
        Long jobPostId = jobPostService.saveJobPost(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("모집 공고 등록 완료"));
    }

    @Operation(summary = "등록한 모집 공고 리스트 조회", description = "완료된 공고, 진행 중인 공고, 예정된 공고")
    @GetMapping("/api/company/job-posts")
    public ResponseEntity<Response> findJobPosts(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @RequestParam("jobPostStatus") JobPostStatus jobPostStatus) {
        // todo: 페이징 처리
        List<JobPostListResponse> jobPostListResponseList =
                jobPostService.findJobPostsByMemberId(principalDetails.getMember().getId(), jobPostStatus);
        return ResponseEntity.ok(new Response(jobPostListResponseList, "등록한 모집 공고 중 " + jobPostStatus.getDescription() + " 모집 공고 조회"));
    }

    @Operation(summary = "등록한 임시 모집 공고 리스트 조회", description = "완료된 공고, 진행 중인 공고, 예정된 공고")
    @GetMapping("/api/company/job-posts/temporary")
    public ResponseEntity<Response> findTemporaryJobPosts(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        // todo: 페이징 처리
        List<JobPostListResponse> temporaryJobPostList =
                jobPostService.findTemporaryJobPosts(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(temporaryJobPostList, "임시 등록한 모집 공고 리스트 반환"));
    }
}
