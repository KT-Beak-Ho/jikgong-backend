package jikgong.domain.jobpost.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.Collections;
import java.util.List;
import jikgong.domain.jobpost.dto.company.JobPostListResponse;
import jikgong.domain.jobpost.dto.company.JobPostManageResponse;
import jikgong.domain.jobpost.dto.company.JobPostResponseForOffer;
import jikgong.domain.jobpost.dto.company.JobPostSaveRequest;
import jikgong.domain.jobpost.dto.company.TemporaryListResponse;
import jikgong.domain.jobpost.dto.company.TemporarySaveRequest;
import jikgong.domain.jobpost.dto.company.TemporaryUpdateRequest;
import jikgong.domain.jobpost.entity.JobPostStatus;
import jikgong.domain.jobpost.service.JobPostCompanyService;
import jikgong.global.common.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JobPostCompanyController {

    private final JobPostCompanyService jobPostCompanyService;

    @Operation(summary = "모집 공고 등록")
    @PostMapping(value = "/api/job-post/company", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Response> saveJobPost(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestPart JobPostSaveRequest request,
        @RequestPart(required = false) List<MultipartFile> imageList) {

        // 이미지 리스트가 null일 경우 빈 리스트로 초기화
        if (imageList == null) {
            imageList = Collections.emptyList();
        }
        
        Long jobPostId = jobPostCompanyService.saveJobPost(principalDetails.getMember().getId(), request, imageList);
        return ResponseEntity.ok(new Response("모집 공고 등록 완료"));
    }

    @Operation(summary = "등록한 모집 공고 리스트 조회", description = "완료된 공고, 진행 중인 공고, 예정된 공고")
    @GetMapping("/api/job-post/company/list/{projectId}")
    public ResponseEntity<Response> findJobPosts(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestParam("jobPostStatus") JobPostStatus jobPostStatus,
        @PathVariable("projectId") Long projectId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size) {
        // 페이징 처리
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDate")));

        List<JobPostListResponse> jobPostListResponseList =
            jobPostCompanyService.findJobPostsByMemberAndProject(principalDetails.getMember().getId(), jobPostStatus,
                projectId, pageable);

        Page<JobPostListResponse> jobPostListResponsePage = new PageImpl<>(jobPostListResponseList, pageable,
            jobPostListResponseList.size());
        return ResponseEntity.ok(
            new Response(jobPostListResponsePage, "등록한 모집 공고 중 " + jobPostStatus.getDescription() + " 모집 공고 조회"));
    }

    @Operation(summary = "인력 관리: 모집 공고 정보 반환", description = "인력 관리 버튼 클릭 시 모집 공고에 대한 정보 반환")
    @GetMapping("/api/job-post/company/{jobPostId}")
    public ResponseEntity<Response> findJobPostForManage(@PathVariable("jobPostId") Long jobPostId) {
        JobPostManageResponse jobPostManageResponse = jobPostCompanyService.findJobPostForManage(jobPostId);
        return ResponseEntity.ok(new Response(jobPostManageResponse, "인력 관리: 모집 공고 정보 반환"));
    }


    @Operation(summary = "임시 저장: 저장")
    @PostMapping("/api/job-post/company/temporary")
    public ResponseEntity<Response> saveTemporaryJobPost(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody TemporarySaveRequest request) {
        Long temporaryId = jobPostCompanyService.saveTemporary(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("임시 저장 등록 완료"));
    }


    @Operation(summary = "임시 저장: 업데이트", description = "임시 저장을 또 한번 임시 저장 할 경우")
    @PutMapping("/api/job-post/company/temporary")
    public ResponseEntity<Response> findTemporaryJobPosts(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody TemporaryUpdateRequest request) {
        jobPostCompanyService.updateTemporaryJobPost(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("임시 등록한 모집 공고 업데이트 완료"));
    }

    @Operation(summary = "임시 저장: 임지 저장 리스트 조회")
    @GetMapping("/api/job-post/company/temporary/list")
    public ResponseEntity<Response> findTemporaryJobPosts(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<TemporaryListResponse> temporaryJobPostPage = jobPostCompanyService.findTemporaryJobPosts(
            principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(temporaryJobPostPage, "임시 등록한 모집 공고 리스트 반환"));
    }

    @Operation(summary = "임시 저장: 삭제")
    @DeleteMapping("/api/job-post/company/temporary/{jobPostId}")
    public ResponseEntity<Response> deleteTemporaryJobPost(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("jobPostId") Long jobPostId) {
        jobPostCompanyService.deleteTemporaryJobPost(principalDetails.getMember().getId(), jobPostId);
        return ResponseEntity.ok(new Response("임시 저장 게시물 삭제 완료"));
    }

    // todo: 임시 저장 단건 조회 개발

    @Operation(summary = "일자리 제안: 출역 가능한 현장 목록")
    @GetMapping("/api/job-post/company/offer-available")
    public ResponseEntity<Response> findAvailableJobPosts(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestParam(name = "memberId") Long memberId,
        @RequestParam(name = "projectId") Long projectId) {
        JobPostResponseForOffer selectOfferJobPostResponse = jobPostCompanyService.findAvailableJobPosts(
            principalDetails.getMember().getId(), memberId, projectId);
        return ResponseEntity.ok(new Response(selectOfferJobPostResponse, "기업: 출역 가능한 현장 목록 반환 완료"));
    }
}
