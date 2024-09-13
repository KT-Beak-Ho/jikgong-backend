package jikgong.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.member.dto.join.JoinCompanyRequest;
import jikgong.domain.member.dto.join.JoinWorkerRequest;
import jikgong.domain.member.dto.join.ValidationLoginIdRequest;
import jikgong.domain.member.dto.join.ValidationPhoneRequest;
import jikgong.domain.member.dto.join.VerificationAccountRequest;
import jikgong.domain.member.dto.join.VerificationAccountResponse;
import jikgong.domain.member.dto.join.VerificationSmsRequest;
import jikgong.domain.member.dto.join.VerificationSmsResponse;
import jikgong.domain.member.dto.login.LoginRequest;
import jikgong.domain.member.dto.login.LoginResponse;
import jikgong.domain.member.dto.login.RefreshTokenRequest;
import jikgong.domain.member.service.LoginService;
import jikgong.global.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "회원 가입: 노동자")
    @PostMapping(value = "/api/join/worker/join", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Response> joinWorkerMember(@RequestPart JoinWorkerRequest request,
        @RequestPart(required = false) MultipartFile visaImage) {
        Long savedMemberId = loginService.joinWorkerMember(request, visaImage);
        return ResponseEntity.ok(new Response("노동자 회원 가입 완료"));
    }

    @Operation(summary = "회원 가입: 기업")
    @PostMapping("/api/join/company/join")
    public ResponseEntity<Response> joinCompanyMember(@RequestBody JoinCompanyRequest request) {
        Long savedMemberId = loginService.joinCompanyMember(request);
        return ResponseEntity.ok(new Response("기업 회원 가입 완료"));
    }

    @Operation(summary = "회원 가입: 아이디 중복 체크")
    @PostMapping("/api/join/validation-loginId")
    public ResponseEntity<Response> validationPhone(@RequestBody ValidationLoginIdRequest request) {
        loginService.validationLoginId(request.getLoginId());
        return ResponseEntity.ok(new Response("사용 가능한 아이디 입니다."));
    }

    @Operation(summary = "회원 가입: 휴대폰 중복 체크")
    @PostMapping("/api/join/validation-phone")
    public ResponseEntity<Response> validationPhone(@RequestBody ValidationPhoneRequest request) {
        loginService.validationPhone(request.getPhone());
        return ResponseEntity.ok(new Response("사용 가능한 휴대폰 입니다."));
    }

    @Operation(summary = "회원 가입: 휴대폰 인증")
    @PostMapping("/api/join/sms-verification")
    public ResponseEntity<Response> verificationSms(@RequestBody VerificationSmsRequest request) {
        VerificationSmsResponse verificationSmsResponse = loginService.verificationSms(request);
        return ResponseEntity.ok(new Response(verificationSmsResponse, "6자리 인증 코드 반환"));
    }

    @Operation(summary = "회원 가입: 계좌 인증")
    @PostMapping("/api/join/account-verification")
    public ResponseEntity<Response> verificationAccount(@RequestBody VerificationAccountRequest request) {
        VerificationAccountResponse verificationAccountResponse = loginService.verificationAccount(request);
        return ResponseEntity.ok(new Response(verificationAccountResponse, "2자리 인증 코드 반환"));
    }

    @Operation(summary = "로그인 요청")
    @PostMapping("/api/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = loginService.login(request);
        return ResponseEntity.ok(new Response(loginResponse, "로그인 완료"));
    }

    @Operation(summary = "refresh 토큰 요청")
    @PostMapping("/api/login/reissue")
    public ResponseEntity<Response> regenerateToken(@RequestBody RefreshTokenRequest request) {
        LoginResponse loginResponse = loginService.regenerateToken(request);
        return ResponseEntity.ok(new Response(loginResponse, "토큰 재발행 완료"));
    }
}
