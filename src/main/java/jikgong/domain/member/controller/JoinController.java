package jikgong.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jikgong.domain.member.dto.join.*;
import jikgong.domain.member.dto.join.validation.JoinValidateEmailRequest;
import jikgong.domain.member.dto.join.validation.JoinValidateLoginIdRequest;
import jikgong.domain.member.dto.join.validation.JoinValidatePhoneRequest;
import jikgong.domain.member.dto.join.verification.JoinVerifyAccountRequest;
import jikgong.domain.member.dto.join.verification.JoinVerifyAccountResponse;
import jikgong.domain.member.dto.join.verification.JoinVerifySmsRequest;
import jikgong.domain.member.dto.join.verification.JoinVerifySmsResponse;
import jikgong.domain.member.dto.login.LoginRequest;
import jikgong.domain.member.dto.login.LoginResponse;
import jikgong.domain.member.service.JoinService;
import jikgong.domain.member.service.LoginService;
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
    private final LoginService loginService;

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
        LoginResponse loginResponse = loginService.login(new LoginRequest(
                request.getLoginId(), request.getPassword(), request.getDeviceToken()));
        return ResponseEntity.ok(new Response(JoinCompanyResponse.from(savedMemberId, loginResponse),"사업자 회원 가입 완료"));
    }

    @Operation(summary = "아이디 중복 체크")
    @PostMapping("/api/join/validation-loginId")
    public ResponseEntity<Response> validateLoginId(@RequestBody JoinValidateLoginIdRequest request) {
        joinService.validateLoginId(request.getLoginId());
        return ResponseEntity.ok(new Response("사용 가능한 아이디입니다."));
    }

    @Operation(summary = "휴대폰 중복 체크")
    @PostMapping("/api/join/validation-phone")
    public ResponseEntity<Response> validatePhone(@RequestBody JoinValidatePhoneRequest request) {
        joinService.validatePhone(request.getPhone());
        return ResponseEntity.ok(new Response("사용 가능한 휴대폰입니다."));
    }

    @Operation(summary = "이메일 중복 체크")
    @PostMapping("/api/join/validation-email")
    public ResponseEntity<Response> validateEmail(@RequestBody JoinValidateEmailRequest request) {
        joinService.validateEmail(request.getEmail());
        return ResponseEntity.ok(new Response("사용 가능한 이메일입니다."));
    }

    @Operation(summary = "휴대폰 인증", description = "문자로 인증 코드를 발송하고, 발송한 인증 코드를 반환합니다.")
    @PostMapping("/api/join/sms-verification")
    public ResponseEntity<Response> sendVerificationCode(
        @RequestBody JoinVerifySmsRequest request) {
        JoinVerifySmsResponse joinVerifySmsResponse = joinService.sendVerificationSms(request);
        return ResponseEntity.ok(new Response(joinVerifySmsResponse, "6자리 인증 코드 반환"));
    }

    @Operation(summary = "계좌 인증")
    @PostMapping("/api/join/account-verification")
    public ResponseEntity<Response> verificationAccount(
        @RequestBody JoinVerifyAccountRequest request) {
        JoinVerifyAccountResponse joinVerifyAccountResponse = joinService.verificationAccount(
            request);
        return ResponseEntity.ok(new Response(joinVerifyAccountResponse, "2자리 인증 코드 반환"));
    }
}
