package jikgong.domain.member.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jikgong.domain.common.Address;
import jikgong.domain.location.entity.Location;
import jikgong.domain.location.repository.LocationRepository;
import jikgong.domain.member.dto.join.JoinCompanyRequest;
import jikgong.domain.member.dto.join.JoinWorkerRequest;
import jikgong.domain.member.dto.join.VerificationAccountRequest;
import jikgong.domain.member.dto.join.VerificationAccountResponse;
import jikgong.domain.member.dto.join.VerificationSmsRequest;
import jikgong.domain.member.dto.join.VerificationSmsResponse;
import jikgong.domain.member.entity.Company;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.entity.Worker;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.workexperience.entity.WorkExperience;
import jikgong.domain.workexperience.repository.WorkExperienceRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import jikgong.global.sms.SmsService;
import jikgong.global.utils.RandomCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JoinService {

    private final MemberRepository memberRepository;
    private final LocationRepository locationRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final PasswordEncoder encoder;
    private final SmsService smsService;

    /**
     * 노동자 회원가입 위치 정보 저장
     */
    public Long joinWorkerMember(JoinWorkerRequest request) {
        // loginId 중복 체크
        validationLoginId(request.getLoginId());
        // 휴대폰 중복 체크
        validationPhone(request.getPhone());

        // 노동자 정보
        Worker worker = Worker.builder()
            .workerName(request.getWorkerName())
            .birth(request.getBirth())
            .gender(request.getGender())
            .nationality(request.getNationality())
            .hasVisa(request.getHasVisa())
            .hasEducationCertificate(request.getHasEducationCertificate())
            .hasWorkerCard(request.getHasWorkerCard())
            .credentialLiabilityConsent(request.getCredentialLiabilityConsent())
            .isNotification(request.getIsNotification())
            .build();

        // 공통 부분
        Member member = Member.builder()
            .loginId(request.getLoginId())
            .password(encoder.encode(request.getPassword()))
            .phone(request.getPhone())
            .email(request.getEmail())
            .privacyConsent((request.getPrivacyConsent()))
            .role(request.getRole())
            .deviceToken(request.getDeviceToken())
            .workerInfo(worker)
            .build();

        // 위치 정보 생성
        Location location = Location.builder()
            .address(
                new Address(
                    request.getAddress(),
                    request.getLatitude(),
                    request.getLongitude())
            )
            .isMain(true)
            .member(member)
            .build();

        // 경력 정보 생성
        List<WorkExperience> workExperienceList = request.getWorkExperienceRequest().stream()
            .map(req -> WorkExperience.from(req, member))
            .collect(Collectors.toList());

        Member savedMember = memberRepository.save(member); // 회원 저장
        workExperienceRepository.saveAll(workExperienceList); // 경력 정보 저장
        locationRepository.save(location); // 위치 정보 저장

        log.info("노동자 회원 가입 완료");
        return savedMember.getId();
    }

    /**
     * 기업 회원가입
     */
    public Long joinCompanyMember(JoinCompanyRequest request) {
        // loginId 중복 체크
        validationLoginId(request.getLoginId());
        // 휴대폰 중복 체크
        validationPhone(request.getPhone());

        // 기업 정보
        Company company = Company.builder()
            .bank(request.getBank())
            .account(request.getAccount())
            .businessNumber(request.getBusinessNumber())
            .region(request.getRegion())
            .companyName(request.getCompanyName())
            .manager(request.getManager())
            .requestContent(request.getRequestContent())
            .isNotification(request.getIsNotification())
            .build();

        // 공통 부분
        Member member = Member.builder()
            .loginId(request.getLoginId())
            .password(encoder.encode(request.getPassword()))
            .phone(request.getPhone())
            .email(request.getEmail())
            .privacyConsent((request.getPrivacyConsent()))
            .role(request.getRole())
            .deviceToken(request.getDeviceToken())
            .companyInfo(company)
            .build();

        log.info("기업 회원 가입 완료");
        return memberRepository.save(member).getId(); // 회원 저장
    }

    /**
     * 휴대폰 중복 체크
     */
    public void validationPhone(String phone) {
        Optional<Member> member = memberRepository.findByPhone(phone);
        if (member.isPresent()) {
            throw new JikgongException(ErrorCode.MEMBER_PHONE_EXIST);
        }
    }

    /**
     * loginId 중복 체크
     */
    public void validationLoginId(String login) {
        Optional<Member> member = memberRepository.findByLoginId(login);
        if (member.isPresent()) {
            throw new JikgongException(ErrorCode.MEMBER_LOGIN_ID_EXIST);
        }
    }

    /**
     * 휴대폰 인증
     */
    public VerificationSmsResponse sendVerificationSms(VerificationSmsRequest request) {
        // 6자리 랜덤 코드 생성
        String authCode = RandomCode.createAuthCode();
        String content = "[직공]\n본인확인 인증번호: [" + authCode + "]";
        try {
            smsService.sendSms(request.getPhone(), content);
        } catch (Exception e) {
            throw new JikgongException(ErrorCode.SMS_SEND_FAIL);
        }

        return new VerificationSmsResponse(authCode);
    }

    /**
     * 계좌 인증
     */
    public VerificationAccountResponse verificationAccount(VerificationAccountRequest request) {

        // todo: 계좌 인증

        return new VerificationAccountResponse("52");
    }
}
