package jikgong.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import jikgong.domain.location.entity.Location;
import jikgong.domain.location.repository.LocationRepository;
import jikgong.domain.member.dto.join.JoinCompanyRequest;
import jikgong.domain.member.dto.join.JoinWorkerRequest;
import jikgong.domain.member.entity.ImgType;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.fixture.JoinCompanyRequestFixture;
import jikgong.domain.member.fixture.JoinWorkerRequestFixture;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.workexperience.repository.WorkExperienceRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import jikgong.global.s3.ImageDto;
import jikgong.global.s3.S3Handler;
import jikgong.global.sms.SmsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class JoinServiceTest {

    @InjectMocks
    private JoinService joinService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private WorkExperienceRepository workExperienceRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private S3Handler s3Handler;
    @Mock
    private SmsService smsService;


    @Test
    @DisplayName("회원가입 성공 - 노동자")
    void joinWorkerMember_success() {
        // given
        JoinWorkerRequest request = JoinWorkerRequestFixture.createDefault();
        MockMultipartFile educationCertificateImage = new MockMultipartFile(
            "educationCertificateImage", "education.jpg", "image/jpeg", new byte[0]);
        MockMultipartFile workerCardImage = new MockMultipartFile(
            "workerCardImage", "worker.jpg", "image/jpeg", new byte[0]);
        ImageDto imageDto = ImageDto.builder().s3Url("s3Url").build();

        Member member = Member.builder().build();
        ReflectionTestUtils.setField(member, "id", 1L); // ID 설정

        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(memberRepository.findByLoginId(any(String.class))).thenReturn(Optional.empty());
        when(memberRepository.findByPhone(any(String.class))).thenReturn(Optional.empty());
        when(s3Handler.uploadImageWithImgType(any(MultipartFile.class),
            any(ImgType.class))).thenReturn(imageDto);

        // when
        Long memberId = joinService.joinWorkerMember(request, educationCertificateImage,
            workerCardImage);

        // then
        assertThat(memberId).isEqualTo(1L);
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(workExperienceRepository, times(1)).saveAll(anyList());
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 노동자 - 중복된 아이디")
    void joinWorkerMember_fail_duplicateLoginId() {
        // given
        JoinWorkerRequest request = JoinWorkerRequestFixture.createDefault();
        MockMultipartFile educationCertificateImage = new MockMultipartFile(
            "educationCertificateImage", "education.jpg", "image/jpeg", new byte[0]);
        MockMultipartFile workerCardImage = new MockMultipartFile(
            "workerCardImage", "worker.jpg", "image/jpeg", new byte[0]);

        // 중복된 아이디가 존재하도록 설정
        when(memberRepository.findByLoginId(anyString()))
            .thenReturn(Optional.of(Member.builder().loginId(request.getLoginId()).build()));

        // when & then
        assertThatThrownBy(
            () -> joinService.joinWorkerMember(request, educationCertificateImage, workerCardImage))
            .isInstanceOf(JikgongException.class)
            .hasMessage(ErrorCode.MEMBER_LOGIN_ID_EXIST.getErrorMessage());

        verify(memberRepository, times(1)).findByLoginId(request.getLoginId());
        verify(memberRepository, times(0)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 노동자 - 중복된 휴대폰 번호")
    void joinWorkerMember_fail_duplicatePhone() {
        // given
        JoinWorkerRequest request = JoinWorkerRequestFixture.createDefault();
        MockMultipartFile educationCertificateImage = new MockMultipartFile(
            "educationCertificateImage", "education.jpg", "image/jpeg", new byte[0]);
        MockMultipartFile workerCardImage = new MockMultipartFile(
            "workerCardImage", "worker.jpg", "image/jpeg", new byte[0]);

        // 중복된 휴대폰 번호가 존재하도록 설정
        when(memberRepository.findByPhone(anyString()))
            .thenReturn(Optional.of(Member.builder().phone(request.getPhone()).build()));

        // when & then
        assertThatThrownBy(
            () -> joinService.joinWorkerMember(request, educationCertificateImage, workerCardImage))
            .isInstanceOf(JikgongException.class)
            .hasMessage(ErrorCode.MEMBER_PHONE_EXIST.getErrorMessage());

        verify(memberRepository, times(1)).findByPhone(request.getPhone());
        verify(memberRepository, times(0)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 성공 - 기업")
    void joinCompanyMember_success() {
        // given
        JoinCompanyRequest request = JoinCompanyRequestFixture.createDefault();

        Member member = Member.builder().build();
        ReflectionTestUtils.setField(member, "id", 1L); // ID 설정

        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(memberRepository.findByLoginId(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByPhone(anyString())).thenReturn(Optional.empty());

        // when
        Long memberId = joinService.joinCompanyMember(request);

        // then
        assertThat(memberId).isEqualTo(1L);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 기업 - 중복된 로그인 아이디")
    void joinCompanyMember_fail_dueToDuplicateLoginId() {
        // given
        JoinCompanyRequest request = JoinCompanyRequestFixture.createDefault();

        // 로그인 아이디 중복 설정
        when(memberRepository.findByLoginId(anyString()))
            .thenReturn(Optional.of(Member.builder().loginId(request.getLoginId()).build()));

        // when & then
        assertThatThrownBy(() -> joinService.joinCompanyMember(request))
            .isInstanceOf(JikgongException.class)
            .hasMessage(ErrorCode.MEMBER_LOGIN_ID_EXIST.getErrorMessage());

        verify(memberRepository, times(1)).findByLoginId(request.getLoginId());
        verify(memberRepository, times(0)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 기업 - 중복된 휴대폰 번호")
    void joinCompanyMember_fail_dueToDuplicatePhone() {
        // given
        JoinCompanyRequest request = JoinCompanyRequestFixture.createDefault();

        // 휴대폰 번호 중복 설정
        when(memberRepository.findByPhone(anyString()))
            .thenReturn(Optional.of(Member.builder().phone(request.getPhone()).build()));

        // when & then
        assertThatThrownBy(() -> joinService.joinCompanyMember(request))
            .isInstanceOf(JikgongException.class)
            .hasMessage(ErrorCode.MEMBER_PHONE_EXIST.getErrorMessage());

        verify(memberRepository, times(1)).findByPhone(request.getPhone());
        verify(memberRepository, times(0)).save(any(Member.class));
    }
}