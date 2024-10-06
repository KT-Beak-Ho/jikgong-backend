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
import jikgong.domain.member.dto.login.LoginRequest;
import jikgong.domain.member.dto.login.LoginResponse;
import jikgong.domain.member.dto.login.RefreshTokenRequest;
import jikgong.domain.member.entity.Company;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.entity.Worker;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.workexperience.entity.WorkExperience;
import jikgong.domain.workexperience.repository.WorkExperienceRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import jikgong.global.security.filter.JwtTokenProvider;
import jikgong.global.sms.SmsService;
import jikgong.global.utils.RandomCode;
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
    private final LocationRepository locationRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final SmsService smsService;

    /**
     * 노동자 회원가입
     * 위치 정보 저장
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
            .rrn(request.getRrn())
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
            .account(request.getAccount())
            .bank(request.getBank())
            .privacyConsent((request.getPrivacyConsent()))
            .role(request.getRole())
            .deviceToken(request.getDeviceToken())
            .workerInfo(worker)
            .build();

        // 위치 정보 생성
        Location location = Location.builder()
            .address(new Address(request.getAddress(), request.getLatitude(), request.getLongitude()))
            .isMain(true)
            .member(member)
            .build();

        // 경력 정보 생성
        List<WorkExperience> workExperienceList = request.getWorkExperienceRequest().stream()
            .map(req -> WorkExperience.from(req, member))
            .collect(Collectors.toList());

        // 회원에 비자 사진 등록 및 저장
//        if (request.getHasVisa() && visaImageRequest != null) {
//            ImageDto imageDto = s3Handler.uploadImage(visaImageRequest);
//            VisaImage visaImage = VisaImage.createEntity(imageDto);
//
//            member.updateVisaImage(visaImage);
//            visaImageRepository.save(visaImage);
//        }

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
            .businessNumber(request.getBusinessNumber())
            .region(request.getRegion())
            .companyName(request.getCompanyName())
            .email(request.getEmail())
            .manager(request.getManager())
            .requestContent(request.getRequestContent())
            .isNotification(request.getIsNotification())
            .build();

        // 공통 부분
        Member member = Member.builder()
            .loginId(request.getLoginId())
            .password(encoder.encode(request.getPassword()))
            .phone(request.getPhone())
            .account(request.getAccount())
            .bank(request.getBank())
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
    public VerificationSmsResponse verificationSms(VerificationSmsRequest request) {
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
