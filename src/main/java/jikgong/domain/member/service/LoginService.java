package jikgong.domain.member.service;

import jikgong.domain.member.dto.login.LoginRequest;
import jikgong.domain.member.dto.login.LoginResponse;
import jikgong.domain.member.dto.login.RefreshTokenRequest;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import jikgong.global.security.filter.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LoginService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 로그인
     * device Token 변경 시 update
     */
    public LoginResponse login(LoginRequest request) {
        // id 체크
        Member member = memberRepository.findByLoginId(request.getLoginId())
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        // authCode 체크
        if (!encoder.matches(request.getPassword(), member.getPassword())) {
            throw new JikgongException(ErrorCode.MEMBER_INVALID_PASSWORD);
        }

        // accessToken & refreshToken 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getLoginId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getLoginId());

        // device token update
        member.updateDeviceToken(request.getDeviceToken());

        return new LoginResponse(accessToken, refreshToken, member.getRole());
    }

    /**
     * refresh token 으로 access token 재발행
     */
    public LoginResponse regenerateToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        // Refresh Token 검증
        if (jwtTokenProvider.isExpiration(refreshToken)) {
            throw new JikgongException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        String loginId = (String) jwtTokenProvider.get(refreshToken).get("loginId");

        Member member = memberRepository.findByLoginId(loginId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        // phone 값으로 redis 에 저장된 refreshToken 추출
        String findRefreshToken = redisTemplate.opsForValue().get(loginId);
        if (!refreshToken.equals(findRefreshToken)) {
            // 리프레쉬 토큰 두 개가 안 맞음
            throw new JikgongException(ErrorCode.REFRESH_TOKEN_NOT_MATCH);
        }

        // 토큰 재발행
        String new_refresh_token = jwtTokenProvider.createRefreshToken(loginId);
        String accessToken = jwtTokenProvider.createAccessToken(loginId);

        return new LoginResponse(accessToken, new_refresh_token, member.getRole());
    }
}
