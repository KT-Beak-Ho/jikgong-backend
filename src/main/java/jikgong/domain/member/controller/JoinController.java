package jikgong.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jikgong.domain.member.dto.join.JoinCompanyRequest;
import jikgong.domain.member.dto.join.JoinWorkerRequest;
import jikgong.domain.member.dto.join.ValidationLoginIdRequest;
import jikgong.domain.member.dto.join.ValidationPhoneRequest;
import jikgong.domain.member.dto.join.VerificationAccountRequest;
import jikgong.domain.member.dto.join.VerificationAccountResponse;
import jikgong.domain.member.dto.join.VerificationSmsRequest;
import jikgong.domain.member.dto.join.VerificationSmsResponse;
import jikgong.domain.member.service.JoinService;
import jikgong.global.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name="[공통] 회원 가입")
@RestController
@RequiredArgsConstructor
@Slf4j
public class JoinController {

    private final JoinService joinService;

    @Operation(summary = "노동자")
    @PostMapping(value = "/api/join/worker/join", consumes = {"multipart/form-data"})
    public ResponseEntity<Response> joinWorkerMember(
        @RequestPart(name = "request") @Valid JoinWorkerRequest request,
        @RequestPart(name = "educationCertificateImage", required = false) MultipartFile educationCertificateImage,
        @RequestPart(name = "workerCardImage", required = false) MultipartFile workerCardImage,
        @RequestPart(name = "signatureImage", required = false) MultipartFile signatureImage) {
        Long savedMemberId = joinService.joinWorkerMember(
            request, educationCertificateImage, workerCardImage, signatureImage
        );
        return ResponseEntity.ok(new Response("노동자 회원 가입 완료"));
    }

    @Operation(summary = "사업자")
    @PostMapping(value = "/api/join/company/join", consumes = {"multipart/form-data"})
    public ResponseEntity<Response> joinCompanyMember(
        @RequestPart(name = "request") @Valid JoinCompanyRequest request,
        @RequestPart(name = "signatureImage", required = false) MultipartFile signatureImage) {
        Long savedMemberId = joinService.joinCompanyMember(request, signatureImage);
        return ResponseEntity.ok(new Response("사업자 회원 가입 완료"));
    }

    @Operation(summary = "아이디 중복 체크")
    @PostMapping("/api/join/validation-loginId")
    public ResponseEntity<Response> validationPhone(@RequestBody ValidationLoginIdRequest request) {
        joinService.validationLoginId(request.getLoginId());
        return ResponseEntity.ok(new Response("사용 가능한 아이디 입니다."));
    }

    @Operation(summary = "휴대폰 중복 체크")
    @PostMapping("/api/join/validation-phone")
    public ResponseEntity<Response> validationPhone(@RequestBody ValidationPhoneRequest request) {
        joinService.validationPhone(request.getPhone());
        return ResponseEntity.ok(new Response("사용 가능한 휴대폰 입니다."));
    }

    @Operation(summary = "휴대폰 인증", description = "문자로 인증 코드를 발송하고, 발송한 인증 코드를 반환합니다.")
    @PostMapping("/api/join/sms-verification")
    public ResponseEntity<Response> sendVerificationCode(
        @RequestBody VerificationSmsRequest request) {
        VerificationSmsResponse verificationSmsResponse = joinService.sendVerificationSms(request);
        return ResponseEntity.ok(new Response(verificationSmsResponse, "6자리 인증 코드 반환"));
    }

    @Operation(summary = "계좌 인증")
    @PostMapping("/api/join/account-verification")
    public ResponseEntity<Response> verificationAccount(
        @RequestBody VerificationAccountRequest request) {
        VerificationAccountResponse verificationAccountResponse = joinService.verificationAccount(
            request);
        return ResponseEntity.ok(new Response(verificationAccountResponse, "2자리 인증 코드 반환"));
    }
}
