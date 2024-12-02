package jikgong.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import jikgong.domain.member.dto.login.LoginRequest;
import jikgong.domain.member.dto.login.LoginResponse;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.entity.Role;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import jikgong.global.security.filter.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        String loginId = "testLoginId";
        String password = "testPassword";
        String deviceToken = "deviceToken";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        LoginRequest request = new LoginRequest(loginId, password, deviceToken);
        Member member = Member.builder()
            .loginId("testLoginId")
            .password("encodedPassword")
            .role(Role.ROLE_WORKER)
            .build();

        when(memberRepository.findByLoginId(anyString())).thenReturn(Optional.of(member));
        when(encoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.createAccessToken(anyString())).thenReturn(accessToken);
        when(jwtTokenProvider.createRefreshToken(anyString())).thenReturn(refreshToken);

        // when
        LoginResponse response = loginService.login(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(response.getRole()).isEqualTo(Role.ROLE_WORKER);

        verify(memberRepository, times(1)).findByLoginId(loginId);
        verify(encoder, times(1)).matches(password, member.getPassword());
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 없음")
    void login_fail_userNotFound() {
        // given
        String invalidLoginId = "invalidLoginId";
        String password = "testPassword";
        String deviceToken = "deviceToken";
        LoginRequest request = new LoginRequest(invalidLoginId, password, deviceToken);

        when(memberRepository.findByLoginId(anyString())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> loginService.login(request))
            .isInstanceOf(JikgongException.class)
            .hasMessage(ErrorCode.MEMBER_NOT_FOUND.getErrorMessage());

        verify(memberRepository, times(1)).findByLoginId(invalidLoginId);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_invalidPassword() {
        // given
        String loginId = "testLoginId";
        String wrongPassword = "wrongPassword";
        String encodedPassword = "encodedPassword";
        String deviceToken = "deviceToken";
        LoginRequest request = new LoginRequest(loginId, wrongPassword, deviceToken);

        Member member = Member.builder().password(encodedPassword).build();

        when(memberRepository.findByLoginId(anyString())).thenReturn(Optional.of(member));
        when(encoder.matches(anyString(), anyString())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> loginService.login(request))
            .isInstanceOf(JikgongException.class)
            .hasMessage(ErrorCode.MEMBER_INVALID_PASSWORD.getErrorMessage());

        verify(encoder, times(1)).matches(wrongPassword, encodedPassword);
    }
}