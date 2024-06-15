package jikgong.domain.certification.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.certification.dto.CertificationResponse;
import jikgong.domain.certification.service.CertificationService;
import jikgong.global.common.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CertificationController {
    private final CertificationService certificationService;

    @Operation(summary = "경력 증명서 등록 & 업데이트", description = "이미 업로드된 경력 증명서가 있다면 제거 후 다시 업로드")
    @PostMapping(value = "/api/certification/worker", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> saveCertification(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      @RequestPart("file")   MultipartFile file) {
        // 기존에 업로드된 경력 증명서 제거
        certificationService.checkAndDeleteCertification(principalDetails.getMember().getId());
        Long certificationId = certificationService.saveCertification(principalDetails.getMember().getId(), file);
        return ResponseEntity.ok(new Response("경력 증명서 등록 완료"));
    }

    @Operation(summary = "회원의 경력 증명서 조회")
    @GetMapping("/api/certification/{workerId}")
    public ResponseEntity<Response> findCertification(@PathVariable("workerId") Long workerId) {
        CertificationResponse certificationResponse = certificationService.findCertification(workerId);
        return ResponseEntity.ok(new Response(certificationResponse, "경력 증명서 등록 완료"));
    }

    @Operation(summary = "경력 증명서 제거")
    @DeleteMapping("/api/certification/worker")
    public ResponseEntity<Response> deleteCertification(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        certificationService.checkAndDeleteCertification(principalDetails.getMember().getId());
        return ResponseEntity.ok(new Response("등록된 경력 증명서 제거 완료"));
    }
}
