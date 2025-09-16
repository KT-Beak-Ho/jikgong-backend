package jikgong.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jikgong.domain.member.dto.login.LoginRequest;
import jikgong.domain.member.dto.login.LoginResponse;
import jikgong.domain.member.dto.login.RefreshTokenRequest;
import jikgong.domain.member.service.LoginService;
import jikgong.global.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="[공통] 로그인")
@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

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
