package jikgong.domain.visaimage.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.visaimage.dto.VisaImageResponse;
import jikgong.domain.visaimage.service.VisaImageService;
import jikgong.global.annotation.WorkerRoleRequired;
import jikgong.global.common.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VisaImageController {

    private final VisaImageService visaImageService;

    @Operation(summary = "노동자: 비자 이미지 조회")
    @GetMapping("/api/visa-image")
    @WorkerRoleRequired
    public ResponseEntity<Response> findMyVisaImage(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        VisaImageResponse visaImageResponse = visaImageService.findMyVisaImage(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response(visaImageResponse, "비자 이미지 조회 완료"));
    }
}
