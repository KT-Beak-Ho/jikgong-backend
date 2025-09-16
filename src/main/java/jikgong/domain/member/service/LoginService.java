package jikgong.domain.member.service;

import java.util.Optional;
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
     * 로그인 (LoginId 혹은 전화번호로 로그인)
     * device Token 변경 시 update
     */
    public LoginResponse login(LoginRequest request) {
        String loginIdOrPhone = request.getLoginIdOrPhone();

        Optional<Member> optionalMember = isPhoneNumber(loginIdOrPhone)
            ? memberRepository.findByPhone(loginIdOrPhone)
            : memberRepository.findByLoginId(loginIdOrPhone);

        // 회원 존재 여부 확인
        Member member = optionalMember.orElseThrow(() ->
            new JikgongException(ErrorCode.MEMBER_INVALID_ID_OR_PASSWORD));

        // authCode 체크
        if (!encoder.matches(request.getPassword(), member.getPassword())) {
            throw new JikgongException(ErrorCode.MEMBER_INVALID_ID_OR_PASSWORD);
        }

        // accessToken & refreshToken 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getLoginId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getLoginId());

        // device token update
        member.updateDeviceToken(request.getDeviceToken());

        return new LoginResponse(accessToken, refreshToken, member.getRole());
    }

    private boolean isPhoneNumber(String identifier) {
        return identifier.matches("^01[0-9]{8,9}$"); // 간단한 전화번호 정규식
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
