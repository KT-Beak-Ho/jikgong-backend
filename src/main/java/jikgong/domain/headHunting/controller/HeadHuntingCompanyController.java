package jikgong.domain.headHunting.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.headHunting.dtos.HeadHuntingListResponse;
import jikgong.domain.headHunting.dtos.SelectOfferJobPostResponse;
import jikgong.domain.headHunting.dtos.WorkerInfoResponse;
import jikgong.domain.headHunting.entity.SortType;
import jikgong.domain.headHunting.service.HeadHuntingCompanyService;
import jikgong.domain.jobPost.entity.Tech;
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

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HeadHuntingCompanyController {
    private final HeadHuntingCompanyService headHuntingCompanyService;

    @Operation(summary = "기업: 헤드헌팅 노동자 조회")
    @GetMapping("/api/head-hunting/{projectId}")
    public ResponseEntity<Response> findHeadHuntingList(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                        @PathVariable("projectId") Long projectId,
                                                        @RequestParam(name = "tech", required = false) Tech tech,
                                                        @RequestParam(name = "bound", required = false) Float bound,
                                                        @RequestParam(name = "sortType", required = false) SortType sortType,
                                                        @RequestParam(name = "page", defaultValue = "0") int page,
                                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HeadHuntingListResponse> headHuntingListResponsePage = headHuntingCompanyService.findHeadHuntingList(principalDetails.getMember().getId(), projectId, tech, bound, sortType, pageable);
        return ResponseEntity.ok(new Response(headHuntingListResponsePage, "기업: 헤드헌팅 노동자 조회 완료"));
    }

    @Operation(summary = "기업: 노동자 상세 정보")
    @GetMapping("/api/head-hunting/worker-detail/{memberId}")
    public ResponseEntity<Response> findWorkerInfo(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   @PathVariable("memberId") Long memberId,
                                                   @RequestParam(name = "selectMonth") LocalDate selectMonth) {
        WorkerInfoResponse workerInfoResponse = headHuntingCompanyService.findWorkerInfo(principalDetails.getMember().getId(), memberId, selectMonth);
        return ResponseEntity.ok(new Response(workerInfoResponse, "기업: 노동자 상세 정보 조회 완료"));
    }

    @Operation(summary = "기업: 출역 가능한 현장 목록")
    @GetMapping("/api/head-hunting/available-jobPosts")
    public ResponseEntity<Response> findAvailableJobPosts(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                          @RequestParam(name = "memberId") Long memberId,
                                                          @RequestParam(name = "projectId") Long projectId) {
        SelectOfferJobPostResponse selectOfferJobPostResponse = headHuntingCompanyService.findAvailableJobPosts(principalDetails.getMember().getId(), memberId, projectId);
        return ResponseEntity.ok(new Response(selectOfferJobPostResponse, "기업: 출역 가능한 현장 목록 반환 완료"));
    }

    @Operation(summary = "기업: 일자리 제안 하기")
    @PostMapping("/api/head-hunting/offer/{memberId}")
    public ResponseEntity<Response> offerJobPost(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @PathVariable("memberId") Long memberId) {
        headHuntingCompanyService.offerJobPost(principalDetails.getMember().getId(), memberId);
        return ResponseEntity.ok(new Response("기업: 일자리 제안 완료"));
    }


}
