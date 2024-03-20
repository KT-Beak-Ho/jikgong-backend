package jikgong.domain.offer.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.offer.dtos.worker.ReceivedOfferListResponse;
import jikgong.domain.offer.service.OfferWorkerService;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
