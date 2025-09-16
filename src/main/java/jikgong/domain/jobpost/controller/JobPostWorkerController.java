package jikgong.domain.jobpost.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import jikgong.domain.jobpost.dto.worker.JobPostDetailResponse;
import jikgong.domain.jobpost.dto.worker.JobPostListResponse;
import jikgong.domain.jobpost.dto.worker.JobPostMapResponse;
import jikgong.domain.jobpost.entity.jobpost.Park;
import jikgong.domain.jobpost.entity.jobpost.SortType;
import jikgong.domain.jobpost.service.JobPostWorkerService;
import jikgong.domain.workexperience.entity.Tech;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[노동자] 모집 공고")
@RestController
@RequiredArgsConstructor
@Slf4j
public class JobPostWorkerController {

    private final JobPostWorkerService jobPostWorkerService;

    // todo: 검색 기능 추가
    @Operation(summary = "모집 공고 조회", description = "직종, 날짜, 스크랩 여부, [식사, 주차 여부], [거리순, 일급 높은 순]")
    @GetMapping("/api/job-post/worker/list")
    public ResponseEntity<Response> getMainPage(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestParam(name = "tech", required = false) List<Tech> techList,
        @RequestParam(name = "workDateList", required = false) List<LocalDate> workDateList,
        @RequestParam(name = "scrap", required = false) Boolean scrap,
        @RequestParam(name = "meal", required = false) Boolean meal,
        @RequestParam(name = "park", required = false) Park park,
        @RequestParam(name = "city", required = false) String city,
        @RequestParam(name = "district", required = false) String district,
        @RequestParam(name = "sortType", required = false, defaultValue = "DISTANCE") SortType sortType,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDate")));

        Page<JobPostListResponse> jobPostListResponsePage;
        if (principalDetails != null) {
            jobPostListResponsePage = jobPostWorkerService.getMainPageForMember(principalDetails.getMember().getId(),
                techList, workDateList, scrap, meal, park, city, district, sortType, pageable);
        } else {
            jobPostListResponsePage = jobPostWorkerService.getMainPageForNonMember(techList, workDateList, scrap, meal,
                park, city, district, sortType, pageable);
        }

        return ResponseEntity.ok(new Response(jobPostListResponsePage, "구직자 홈화면 정보 반환 완료"));
    }

    @Operation(summary = "모집 공고 상세 화면 - 일반")
    @GetMapping("/api/job-post/worker/{jobPostId}")
    public ResponseEntity<Response> getJobPostDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("jobPostId") Long jobPostId) {
        JobPostDetailResponse jobPostDetailResponse;
        if (principalDetails != null) {
            jobPostDetailResponse = jobPostWorkerService.getJobPostDetailForMember(principalDetails.getMember().getId(),
                jobPostId);
        } else {
            jobPostDetailResponse = jobPostWorkerService.getJobPostDetailForNonMember(jobPostId);
        }
        return ResponseEntity.ok(new Response(jobPostDetailResponse, "모집 공고 상세 화면 - 일반 반환 완료"));
    }

    @Operation(summary = "지도에서 모집 공고 조회")
    @GetMapping("/api/job-post/worker/list-on-map")
    public ResponseEntity<Response> getJobPostOnMap(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestParam(name = "northEastLat") Float northEastLat,
        @RequestParam(name = "northEastLng") Float northEastLng,
        @RequestParam(name = "southWestLat") Float southWestLat,
        @RequestParam(name = "southWestLng") Float southWestLng,
        @RequestParam(name = "tech", required = false) List<Tech> techList,
        @RequestParam(name = "workDateList", required = false) List<LocalDate> dateList,
        @RequestParam(name = "scrap", required = false) Boolean scrap) {
        List<JobPostMapResponse> jobPostsOnMap;
        if (principalDetails != null) {
            jobPostsOnMap = jobPostWorkerService.findJobPostsOnMapForMember(principalDetails.getMember().getId(),
                northEastLat, northEastLng, southWestLat, southWestLng, techList, dateList, scrap);
        } else {
            jobPostsOnMap = jobPostWorkerService.findJobPostsOnMapForNonMember(northEastLat, northEastLng, southWestLat,
                southWestLng, techList, dateList, scrap);
        }
        return ResponseEntity.ok(new Response(jobPostsOnMap, "지도에서 경계 안에 속한 모집 공고 조회 완료"));
    }
}
