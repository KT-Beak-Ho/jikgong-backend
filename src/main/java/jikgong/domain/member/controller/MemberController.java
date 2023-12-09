package jikgong.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.member.dtos.join.*;
import jikgong.domain.member.dtos.login.LoginRequest;
import jikgong.domain.member.dtos.login.LoginResponse;
import jikgong.domain.member.dtos.login.RefreshTokenRequest;
import jikgong.domain.member.service.MemberService;
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
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "회원 가입: 노동자")
    @PostMapping("/api/member/join-worker")
    public ResponseEntity<Response> joinWorkerMember(@RequestBody JoinWorkerRequest request) {
        Long savedMemberId = memberService.joinWorkerMember(request);
        return ResponseEntity.ok(new Response("노동자 회원 가입 완료"));
    }

    @Operation(summary = "회원 가입: 기업")
    @PostMapping("/api/member/join-company")
    public ResponseEntity<Response> joinCompanyMember(@RequestBody JoinCompanyRequest request) {
        Long savedMemberId = memberService.joinCompanyMember(request);
        return ResponseEntity.ok(new Response("기업 회원 가입 완료"));
    }

    @Operation(summary = "회원 가입: 휴대폰 중복 체크")
    @PostMapping("/api/member/validation-phone")
    public ResponseEntity<Response> validationPhone(@RequestBody ValidationUsernameRequest request) {
        memberService.validationPhone(request.getPhone());
        return ResponseEntity.ok(new Response("사용 가능한 아이디 입니다."));
    }

    @Operation(summary = "회원 가입: 휴대폰 인증")
    @PostMapping("/api/member/sms-verification")
    public ResponseEntity<Response> verificationSms(@RequestBody VerificationSmsRequest request) {
        VerificationSmsResponse verificationSmsResponse = memberService.verificationSms(request);
        return ResponseEntity.ok(new Response(verificationSmsResponse, "6자리 인증 코드 반환"));
    }

    @Operation(summary = "회원 가입: 계좌 인증")
    @PostMapping("/api/member/account-verification")
    public ResponseEntity<Response> verificationAccount(@RequestBody VerificationAccountRequest request) {
        VerificationAccountResponse verificationAccountResponse = memberService.verificationAccount(request);
        return ResponseEntity.ok(new Response(verificationAccountResponse, "2자리 인증 코드 반환"));
    }

    @Operation(summary = "로그인 요청")
    @PostMapping("/api/member/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = memberService.login(request);
        return ResponseEntity.ok(new Response(loginResponse, "로그인 완료"));
    }

    @Operation(summary = "refresh 토큰 요청")
    @PostMapping("/api/member/reissue")
    public ResponseEntity<Response> regenerateToken(@RequestBody RefreshTokenRequest request) {
        LoginResponse loginResponse = memberService.regenerateToken(request);
        return ResponseEntity.ok(new Response(loginResponse, "토큰 재발행 완료"));
    }
}
