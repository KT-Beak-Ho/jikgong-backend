package jikgong.domain.jobPost.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.jobPost.dtos.worker.JobPostListResponse;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Park;
import jikgong.domain.jobPost.entity.SortType;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.jobPost.service.JobPostWorkerService;
import jikgong.global.dto.Response;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JobPostWorkerController {
    private final JobPostWorkerService jobPostWorkerService;

    @Operation(summary = "구직자 홈화면")
    @GetMapping("/api/worker/job-posts")
    public ResponseEntity<Response> getMainPage(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @RequestParam(name = "tech", required = false) Tech tech,
                                                @RequestParam(name = "workDateList", required = false)List<LocalDate> workDateList,
                                                @RequestParam(name = "scrap", required = false) Boolean scrap,
                                                @RequestParam(name = "meal", required = false) Boolean meal,
                                                @RequestParam(name = "park", required = false)Park park,
                                                @RequestParam(name = "sortType", required = false, defaultValue = "DISTANCE") SortType sortType,
                                                @RequestParam(name = "page", defaultValue = "0") int page,
                                                @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDate")));
        Page<JobPostListResponse> jobPostListResponsePage =
                jobPostWorkerService.getMainPage(principalDetails.getMember().getId(), tech, workDateList, scrap, meal, park, sortType, pageable);
        return ResponseEntity.ok(new Response(jobPostListResponsePage, "구직자 홈화면 정보 반환 완료"));
    }

}
