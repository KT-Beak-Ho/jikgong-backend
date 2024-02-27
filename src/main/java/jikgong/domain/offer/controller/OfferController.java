package jikgong.domain.offer.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.offer.dtos.OfferHistoryResponse;
import jikgong.domain.offer.dtos.WorkerInfoResponse;
import jikgong.domain.offer.dtos.OfferRequest;
import jikgong.domain.offer.dtos.SelectOfferJobPostResponse;
import jikgong.domain.offer.entity.OfferStatus;
import jikgong.domain.offer.service.OfferService;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OfferController {
    private final OfferService offerService;

    @Operation(summary = "기업: 노동자 상세 정보")
    @GetMapping("/api/head-hunting/worker-detail/{resumeId}")
    public ResponseEntity<Response> findWorkerInfo(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   @PathVariable("resumeId") Long resumeId,
                                                   @RequestParam(name = "selectMonth") LocalDate selectMonth) {
        WorkerInfoResponse workerInfoResponse = offerService.findWorkerInfo(principalDetails.getMember().getId(), resumeId, selectMonth);
        return ResponseEntity.ok(new Response(workerInfoResponse, "기업: 노동자 상세 정보 조회 완료"));
    }

    @Operation(summary = "기업: 출역 가능한 현장 목록")
    @GetMapping("/api/head-hunting/available-jobPosts")
    public ResponseEntity<Response> findAvailableJobPosts(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                          @RequestParam(name = "memberId") Long memberId,
                                                          @RequestParam(name = "projectId") Long projectId) {
        SelectOfferJobPostResponse selectOfferJobPostResponse = offerService.findAvailableJobPosts(principalDetails.getMember().getId(), memberId, projectId);
        return ResponseEntity.ok(new Response(selectOfferJobPostResponse, "기업: 출역 가능한 현장 목록 반환 완료"));
    }

    @Operation(summary = "기업: 일자리 제안 하기")
    @PostMapping("/api/head-hunting/offer")
    public ResponseEntity<Response> offerJobPost(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @RequestBody OfferRequest request) {
        offerService.offerJobPost(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("기업: 일자리 제안 완료"));
    }

    @Operation(summary = "기업: 제안 기록 조회")
    @GetMapping("/api/head-hunting/history")
    public ResponseEntity<Response> findOfferList(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                  @RequestParam(name = "offerStatus") OfferStatus offerStatus,
                                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                                  @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDate")));
        Page<OfferHistoryResponse> offerHistory = offerService.findOfferHistory(principalDetails.getMember().getId(), offerStatus, pageable);
        return ResponseEntity.ok(new Response(offerHistory, "제안 기록 조회 완료"));
    }


}
