package jikgong.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.member.dtos.*;
import jikgong.domain.member.service.MemberService;
import jikgong.global.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "회원 가입 요청")
    @PostMapping("/join")
    public ResponseEntity<Response> joinMember(@RequestBody JoinRequest request) {
        Long savedMemberId = memberService.joinMember(request);
        return ResponseEntity.ok(new Response("회원 가입 완료"));
    }

    @Operation(summary = "회원 가입: 아이디 중복 체크")
    @PostMapping("/validation-phone")
    public ResponseEntity<Response> validationPhone(@RequestBody ValidationUsernameRequest request) {
        memberService.validationPhone(request);
        return ResponseEntity.ok(new Response("사용 가능한 아이디 입니다."));
    }

    @Operation(summary = "회원 가입: 휴대폰 인증")
    @PostMapping("/sms-verification")
    public ResponseEntity<Response> verificationSms(@RequestBody VerificationSmsRequest request) {
        VerificationSmsResponse verificationSmsResponse = memberService.verificationSms(request);
        return ResponseEntity.ok(new Response(verificationSmsResponse, "6자리 인증 코드 반환"));
    }

    @Operation(summary = "회원 가입: 계좌 인증")
    @PostMapping("/account-verification")
    public ResponseEntity<Response> verificationAccount(@RequestBody VerificationAccountRequest request) {
        VerificationAccountResponse verificationAccountResponse = memberService.verificationAccount(request);
        return ResponseEntity.ok(new Response(verificationAccountResponse, "2자리 인증 코드 반환"));
    }
}
