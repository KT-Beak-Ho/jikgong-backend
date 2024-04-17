package jikgong.domain.offer.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.offer.dtos.company.OfferHistoryResponse;
import jikgong.domain.offer.dtos.company.OfferRequest;
import jikgong.domain.offer.entity.OfferStatus;
import jikgong.domain.offer.service.OfferCompanyService;
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

@RestController
@RequiredArgsConstructor
@Slf4j
public class OfferCompanyController {
    private final OfferCompanyService offerCompanyService;

    @Operation(summary = "기업: 일자리 제안 하기")
    @PostMapping("/api/company/offer")
    public ResponseEntity<Response> offerJobPost(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @RequestBody OfferRequest request) {
        offerCompanyService.offerJobPost(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("기업: 일자리 제안 완료"));
    }

    @Operation(summary = "기업: 제안 기록 조회")
    @GetMapping("/api/company/offers")
    public ResponseEntity<Response> findOfferList(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                  @RequestParam(name = "offerStatus") OfferStatus offerStatus,
                                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                                  @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDate")));
        Page<OfferHistoryResponse> offerHistory = offerCompanyService.findOfferHistory(principalDetails.getMember().getId(), offerStatus, pageable);
        return ResponseEntity.ok(new Response(offerHistory, "제안 기록 조회 완료"));
    }

    @Operation(summary = "기업: 제안 취소")
    @DeleteMapping("/api/company/offer/{offerId}")
    public ResponseEntity<Response> cancelOffer(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @PathVariable("offerId") Long offerId) {
        offerCompanyService.cancelOffer(principalDetails.getMember().getId(), offerId);
        return ResponseEntity.ok(new Response("제안 취소 완료"));
    }
}
