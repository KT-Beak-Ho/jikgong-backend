package jikgong.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.member.dtos.join.*;
import jikgong.domain.member.dtos.login.LoginRequest;
import jikgong.domain.member.dtos.login.LoginResponse;
import jikgong.domain.member.dtos.login.RefreshTokenRequest;
import jikgong.domain.member.service.LoginService;
import jikgong.global.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final LoginService loginService;

    @Operation(summary = "회원 가입: 노동자")
    @PostMapping("/api/join/worker/join")
    public ResponseEntity<Response> joinWorkerMember(@RequestBody JoinWorkerRequest request) {
        Long savedMemberId = loginService.joinWorkerMember(request);
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
