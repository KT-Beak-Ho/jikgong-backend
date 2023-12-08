package jikgong.domain.member.service;

import jikgong.domain.location.entity.Location;
import jikgong.domain.location.repository.LocationRepository;
import jikgong.domain.member.dtos.join.*;
import jikgong.domain.member.dtos.login.LoginRequest;
import jikgong.domain.member.dtos.login.LoginResponse;
import jikgong.domain.member.dtos.login.RefreshTokenRequest;
import jikgong.domain.member.entity.Company;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.entity.Worker;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import jikgong.global.security.filter.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public Long joinWorkerMember(JoinWorkerRequest request) {
        // 휴대폰 중복 체크
        validationPhone(request.getPhone());

        // 노동자 정보
        Worker worker = Worker.builder()
                    .workerName(request.getWorkerName())
                    .rrnPrefix(request.getRrnPrefix())
                    .gender(request.getGender())
                    .nationality(request.getNationality())
                    .build();
        // 공통 부분
        Member member = Member.builder()
                .phone(request.getPhone())
                .authCode(encoder.encode(request.getAuthCode()))
                .account(request.getAccount())
                .bank(request.getBank())
                .role(request.getRole())
                .deviceToken(request.getDeviceToken())
                .workerInfo(worker)
                .build();

        Location location = Location.builder()
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isMain(true)
                .member(member)
                .build();

        Member savedMember = memberRepository.save(member); // 회원 저장
        locationRepository.save(location); // 위치 정보 저장
        log.info("노동자 회원 가입 완료");
        return savedMember.getId();
    }

    public Long joinCompanyMember(JoinCompanyRequest request) {
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
                .build();

        // 공통 부분
        Member member = Member.builder()
                .phone(request.getPhone())
                .authCode(encoder.encode(request.getAuthCode()))
                .account(request.getAccount())
                .bank(request.getBank())
                .role(request.getRole())
                .deviceToken(request.getDeviceToken())
                .companyInfo(company)
                .build();

        log.info("기업 회원 가입 완료");
        return memberRepository.save(member).getId(); // 회원 저장
    }

    public void validationPhone(String phone) {
        Optional<Member> member = memberRepository.findByPhone(phone);
        if (member.isPresent()) {
            throw new CustomException(ErrorCode.MEMBER_PHONE_EXIST);
        }
    }

    public VerificationSmsResponse verificationSms(VerificationSmsRequest request) {
        String authCode = createAuthCode();

        // todo: 문자 발송

        return new VerificationSmsResponse(authCode);
    }

    private static String createAuthCode() {
        String characters = "0123456789";

        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder(6); // 6자리 랜덤 코드

        // 지정된 길이만큼 랜덤 코드 생성
        for (int i = 0; i < 6; i++) {
            // 랜덤으로 문자 선택
            char randomChar = characters.charAt(random.nextInt(characters.length()));
            codeBuilder.append(randomChar);
        }
        return codeBuilder.toString();
    }

    public VerificationAccountResponse verificationAccount(VerificationAccountRequest request) {

        // todo: 계좌 인증

        return new VerificationAccountResponse("52");
    }

    public LoginResponse login(LoginRequest request) {
        // id 체크
        Member member = memberRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // authCode 체크
        if (!encoder.matches(request.getAuthCode(), member.getAuthCode())) {
            throw new CustomException(ErrorCode.MEMBER_INVALID_PASSWORD);
        }

        // accessToken & refreshToken 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getPhone());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getPhone());

        return new LoginResponse(accessToken, refreshToken, member.getRole());
    }

    public LoginResponse regenerateToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        // Refresh Token 검증
        if (jwtTokenProvider.isExpiration(refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        String phone = (String) jwtTokenProvider.get(refreshToken).get("phone");

        Member member = memberRepository.findByPhone(phone)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        // phone 값으로 redis 에 저장된 refreshToken 추출
        String findRefreshToken = redisTemplate.opsForValue().get(phone);
        if (!refreshToken.equals(findRefreshToken)) {
            // 리프레쉬 토큰 두 개가 안 맞음
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_MATCH);
        }

        // 토큰 재발행
        String new_refresh_token = jwtTokenProvider.createRefreshToken(phone);
        String accessToken = jwtTokenProvider.createAccessToken(phone);

        return new LoginResponse(accessToken, new_refresh_token, member.getRole());
    }
}
