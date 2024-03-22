package jikgong.domain.offer.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.offer.dtos.worker.OfferProcessRequest;
import jikgong.domain.offer.dtos.worker.ReceivedOfferListResponse;
import jikgong.domain.offer.service.OfferWorkerService;
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
public class OfferWorkerController {
    private final OfferWorkerService offerWorkerService;

    @Operation(summary = "노동자: 제안 받은 내역 조회")
    @GetMapping("/api/worker/offers")
    public ResponseEntity<Response> findReceivedOffer(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      @RequestParam(name = "isPending") Boolean isPending) {
        List<ReceivedOfferListResponse> receivedOfferListResponseList = offerWorkerService.findReceivedOffer(principalDetails.getMember().getId(), isPending);
        return ResponseEntity.ok(new Response(receivedOfferListResponseList, "노동자: 제안 받은 내역 조회 완료"));
    }

    @Operation(summary = "노동자: 제안 수락, 거부")
    @PostMapping("/api/worder/offer")
    public ResponseEntity<Response>  processOffer(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                  @RequestBody OfferProcessRequest request) {
        offerWorkerService.processOffer(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("노동자: 제안 " + (request.getIsAccept() ? "수락" : "거부" + "완료")));
    }
}
