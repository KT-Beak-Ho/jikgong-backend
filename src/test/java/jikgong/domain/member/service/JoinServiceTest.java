package jikgong.domain.member.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import jikgong.domain.member.dto.join.JoinWorkerRequest;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.fixture.JoinWorkerRequestFixture;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.global.sms.SmsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JoinServiceTest {

    @InjectMocks
    private JoinService joinService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private SmsService smsService;


    @Test
    @DisplayName("회원가입 성공 - 노동자")
    void joinWorkerMember_success() {
        // given
        JoinWorkerRequest request = JoinWorkerRequestFixture.createDefault();

        Member member = Member.builder().build();
        ReflectionTestUtils.setField(member, "id", 1L); // ID 설정

        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(memberRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // when
        Long memberId = joinService.joinWorkerMember(request);

        // then
        assertThat(memberId).isEqualTo(1L);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

//    @Test
//    @DisplayName("회원가입 실패 - 중복된 전화번호")
//    void joinWorkerMember_duplicatePhone() {
//        // given
//        JoinWorkerRequest request = new JoinWorkerRequest();
//        request.setLoginId("worker2");
//        request.setPhone("01012345678");
//
//        when(memberRepository.findByPhone("01012345678")).thenReturn(Optional.of(new Member()));
//
//        // when & then
//        JikgongException exception = assertThrows(JikgongException.class, () -> joinService.joinWorkerMember(request));
//        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_PHONE_EXIST);
//    }

//    @Test
//    @DisplayName("기업 회원가입 성공")
//    void joinCompanyMember_success() {
//        // given
//        JoinCompanyRequest request = new JoinCompanyRequest();
//        request.setLoginId("company1");
//        request.setPhone("01098765432");
//        request.setPassword("password123");
//
//        when(encoder.encode(anyString())).thenReturn("encodedPassword");
//        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
//            Member member = invocation.getArgument(0);
//            member.setId(2L);
//            return member;
//        });
//
//        // when
//        Long memberId = joinService.joinCompanyMember(request);
//
//        // then
//        assertThat(memberId).isEqualTo(2L);
//        verify(memberRepository, times(1)).save(any(Member.class));
//    }
//
//    @Test
//    @DisplayName("전화번호 중복 체크 실패")
//    void validationPhone_duplicate() {
//        // given
//        String phone = "01012345678";
//        when(memberRepository.findByPhone(phone)).thenReturn(Optional.of(new Member()));
//
//        // when & then
//        JikgongException exception = assertThrows(JikgongException.class, () -> joinService.validationPhone(phone));
//        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_PHONE_EXIST);
//    }
//
//    @Test
//    @DisplayName("SMS 인증 성공")
//    void verificationSms_success() {
//        // given
//        VerificationSmsRequest request = new VerificationSmsRequest();
//        request.setPhone("01012345678");
//
//        doNothing().when(smsService).sendSms(anyString(), anyString());
//
//        // when
//        VerificationSmsResponse response = joinService.verificationSms(request);
//
//        // then
//        assertThat(response.getAuthCode()).isNotNull();
//        verify(smsService, times(1)).sendSms(anyString(), anyString());
//    }
}