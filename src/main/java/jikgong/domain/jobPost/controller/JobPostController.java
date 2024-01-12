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
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JobPostController {
    private final JobPostService jobPostService;

    @Operation(summary = "모집 공고 등록")
    @PostMapping(value = "/api/company/job-post", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Response> saveJobPost(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @RequestPart JobPostSaveRequest request,
                                                @RequestPart(required = false) List<MultipartFile> imageList) {
        Long jobPostId = jobPostService.saveJobPost(principalDetails.getMember().getId(), request, imageList);
        return ResponseEntity.ok(new Response("모집 공고 등록 완료"));
    }

    @Operation(summary = "등록한 모집 공고 리스트 조회", description = "완료된 공고, 진행 중인 공고, 예정된 공고")
    @GetMapping("/api/company/job-posts/{projectId}")
    public ResponseEntity<Response> findJobPosts(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @RequestParam("jobPostStatus") JobPostStatus jobPostStatus,
                                                 @PathVariable("projectId") Long projectId,
                                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                                 @RequestParam(name = "size", defaultValue = "10") int size) {
        // 페이징 처리
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDate")));

        List<JobPostListResponse> jobPostListResponseList =
                jobPostService.findJobPostsByMemberAndProject(principalDetails.getMember().getId(), jobPostStatus, projectId, pageable);

        Page<JobPostListResponse> jobPostListResponsePage = new PageImpl<>(jobPostListResponseList, pageable, jobPostListResponseList.size());
        return ResponseEntity.ok(new Response(jobPostListResponsePage, "등록한 모집 공고 중 " + jobPostStatus.getDescription() + " 모집 공고 조회"));
    }

    @Operation(summary = "등록한 임시 모집 공고 리스트 조회", description = "완료된 공고, 진행 중인 공고, 예정된 공고")
    @GetMapping("/api/company/job-posts/temporary")
    public ResponseEntity<Response> findTemporaryJobPosts(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                          @RequestParam(name = "page", defaultValue = "0") int page,
                                                          @RequestParam(name = "size", defaultValue = "10") int size) {
        // 페이징 처리
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDate")));

        List<JobPostListResponse> temporaryJobPostList =
                jobPostService.findTemporaryJobPosts(principalDetails.getMember().getId(), pageable);

        Page<JobPostListResponse> temporaryJobPostPage = new PageImpl<>(temporaryJobPostList, pageable, temporaryJobPostList.size());
        return ResponseEntity.ok(new Response(temporaryJobPostPage, "임시 등록한 모집 공고 리스트 반환"));
    }

}
