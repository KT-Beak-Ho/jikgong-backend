package jikgong.domain.offer.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import jikgong.domain.offer.dto.worker.OfferJobPostResponse;
import jikgong.domain.offer.dto.worker.OfferProcessRequest;
import jikgong.domain.offer.dto.worker.ReceivedOfferResponse;
import jikgong.domain.offer.service.OfferWorkerService;
import jikgong.global.annotation.WorkerRoleRequired;
import jikgong.global.common.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="[노동자] 채용 제안")
@RestController
@RequiredArgsConstructor
@Slf4j
@WorkerRoleRequired
public class OfferWorkerController {

    private final OfferWorkerService offerWorkerService;

    @Operation(summary = "제안 받은 내역 조회")
    @GetMapping("/api/offer/worker/list")
    public ResponseEntity<Response> findReceivedOffer(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestParam(name = "isPending") Boolean isPending) {
        List<ReceivedOfferResponse> receivedOfferResponseList = offerWorkerService.findReceivedOffer(
            principalDetails.getMember().getId(), isPending);
        return ResponseEntity.ok(new Response(receivedOfferResponseList, "노동자: 제안 받은 내역 조회 완료"));
    }

    @Operation(summary = "제안 수락, 거부")
    @PostMapping("/api/offer/worker")
    public ResponseEntity<Response> processOffer(@AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody OfferProcessRequest request) {
        offerWorkerService.processOffer(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("노동자: 제안 " + (request.getIsAccept() ? "수락" : "거부" + "완료")));
    }

    @Operation(summary = "제안 받은 내역 상세 조회")
    @GetMapping("/api/offer/worker/{offerWorkDateId}")
    public ResponseEntity<Response> getJobPostDetailBeforeOfferProcess(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("offerWorkDateId") Long offerWorkDateId) {
        OfferJobPostResponse offerJobPostResponse = offerWorkerService.getJobPostDetailForOffer(
            principalDetails.getMember().getId(), offerWorkDateId);
        return ResponseEntity.ok(new Response(offerJobPostResponse, "모집 공고 상세 화면 - 제안 수락 시 반환 완료"));
    }
}
