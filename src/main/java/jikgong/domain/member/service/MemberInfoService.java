package jikgong.domain.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import jikgong.domain.member.dto.company.CompanySearchResponse;
import jikgong.domain.member.dto.info.AuthCodeForFindRequest;
import jikgong.domain.member.dto.info.CompanyInfoRequest;
import jikgong.domain.member.dto.info.CompanyInfoResponse;
import jikgong.domain.member.dto.info.LoginIdFindRequest;
import jikgong.domain.member.dto.info.LoginIdFindResponse;
import jikgong.domain.member.dto.info.PasswordFindRequest;
import jikgong.domain.member.dto.info.PasswordFindResponse;
import jikgong.domain.member.dto.info.PasswordUpdateRequest;
import jikgong.domain.member.dto.info.StayExpirationRequest;
import jikgong.domain.member.dto.info.StayExpirationResponse;
import jikgong.domain.member.dto.info.WorkerInfoRequest;
import jikgong.domain.member.dto.info.WorkerInfoResponse;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.workexperience.dto.WorkExperienceRequest;
import jikgong.domain.workexperience.entity.WorkExperience;
import jikgong.domain.workexperience.repository.WorkExperienceRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
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
public class MemberInfoService {

    private final WorkExperienceRepository workExperienceRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final SmsService smsService;
    private final StayExpirationService stayExpirationService;

    /**
     * 노동자: 회원 정보 조회
     */
    @Transactional(readOnly = true)
    public WorkerInfoResponse findWorkerInfo(Long workerId) {
        Member worker = memberRepository.findById(workerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        return WorkerInfoResponse.from(worker);
    }

    /**
     * 노동자: 회원 정보 수정
     */
    public void updateWorkerInfo(Long workerId, WorkerInfoRequest request) {
        Member worker = memberRepository.findById(workerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        // 회원 정보 업데이트
        worker.updateWorkerInfo(request);

        // 경력 정보 업데이트
        updateWorkExperiences(request.getWorkExperienceRequestList(), worker);
    }

    /**
     * 경력 정보 업데이트 로직
     */
    private void updateWorkExperiences(List<WorkExperienceRequest> workExperienceRequestList, Member worker) {
        List<WorkExperience> currentWorkExperiences = worker.getWorkExperienceList();
        List<WorkExperience> newWorkExperiences = new ArrayList<>();

        // 수정 요청으로 온 WorkExperienceRequest에서 해당 ID가 있으면 수정, null 이면 새로 추가
        for (WorkExperienceRequest workExperienceRequest : workExperienceRequestList) {
            Optional<WorkExperience> existingExperience = currentWorkExperiences.stream()
                .filter(we -> we.getId().equals(workExperienceRequest.getWorkExperienceId()))
                .findFirst();

            if (existingExperience.isPresent()) {
                // 경력 수정
                WorkExperience workExperience = existingExperience.get();
                workExperience.updateExperienceMonths(workExperienceRequest); // 필요한 필드 업데이트
            } else {
                // 경력 추가
                newWorkExperiences.add(WorkExperience.from(workExperienceRequest, worker));
            }
        }
        // 수정 요청이 오지 않은 경력에 대해선 제거
        List<Long> workExperienceIdList = workExperienceRequestList.stream()
            .map(WorkExperienceRequest::getWorkExperienceId)
            .filter(Objects::nonNull) // null 값 필터링
            .collect(Collectors.toList());
        workExperienceRepository.deleteWorkExperienceNotInIdList(worker.getId(), workExperienceIdList);

        // 경력 추가
        workExperienceRepository.saveAll(newWorkExperiences);
    }

    /**
     * 기업: 회원 정보 조회
     */
    @Transactional(readOnly = true)
    public CompanyInfoResponse findCompanyInfo(Long companyId) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        return CompanyInfoResponse.from(company);
    }

    /**
     * 기업: 회원 정보 수정
     */
    public void updateCompanyInfo(Long companyId, CompanyInfoRequest request) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        company.updateCompanyInfo(request);
    }

    /**
     * 비밀번호 확인 후 변경
     */
    public void updatePassword(Long memberId, PasswordUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        if (!encoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new JikgongException(ErrorCode.MEMBER_INVALID_PASSWORD);
        }

        member.updatePassword(encoder.encode(request.getNewPassword()));
    }

    /**
     * 기업 검색
     */
    @Transactional(readOnly = true)
    public List<CompanySearchResponse> searchCompany(String keyword) {
        return memberRepository.findByCompanyName(keyword).stream()
            .map(CompanySearchResponse::from)
            .collect(Collectors.toList());
    }

    /**
     * 비밀번호 찾기 전 문자 인증
     */
    public void verificationBeforeFindPassword(PasswordFindRequest request) throws Exception {
        Member member = memberRepository.findMemberForForgottenPassword(request.getLoginId(), request.getPhone())
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        // 6자리 랜덤 코드 생성
        String authCode = RandomCode.createAuthCode();
        String content = "[직공]\n비밀번호 찾기 본인확인 인증번호: [" + authCode + "]";
        try {
            smsService.sendSms(member.getPhone(), content);
        } catch (Exception e) {
            throw new JikgongException(ErrorCode.SMS_SEND_FAIL);
        }

        // Redis에 인증 코드와 회원 정보를 저장 (TTL 5분)
        redisTemplate.opsForValue().set(member.getPhone(), authCode, 5, TimeUnit.MINUTES);
    }

    /**
     * 임시 비밀번호 발급
     */
    public PasswordFindResponse updateTemporaryPassword(AuthCodeForFindRequest request) {
        // Redis에 저장된 인증 코드 가져오기
        String savedAuthCode = redisTemplate.opsForValue().get(request.getPhone());

        // 인증 코드가 일치하는지 체크
        if (savedAuthCode == null || !savedAuthCode.equals(request.getAuthCode())) {
            throw new JikgongException(ErrorCode.MEMBER_INVALID_AUTH_CODE);  // 인증 코드 불일치
        }

        // 임시 번호 생성, 업데이트 및 반환
        String temporaryPassword = RandomCode.createTemporaryPassword();
        Member member = memberRepository.findByPhone(request.getPhone())
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        member.updatePassword(encoder.encode(temporaryPassword));

        return PasswordFindResponse.from(temporaryPassword);
    }

    /**
     * 아이디 찾기 전 문자 인증
     */
    public void verificationBeforeFindLoginId(LoginIdFindRequest request) {
        Member member = memberRepository.findMemberForForgottenLoginId(request.getPhone(), request.getName())
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        // 6자리 랜덤 코드 생성
        String authCode = RandomCode.createAuthCode();
        String content = "[직공]\n아이디 찾기 본인확인 인증번호: [" + authCode + "]";
        try {
            smsService.sendSms(member.getPhone(), content);
        } catch (Exception e) {
            throw new JikgongException(ErrorCode.SMS_SEND_FAIL);
        }

        // Redis에 인증 코드와 회원 정보를 저장 (TTL 5분)
        redisTemplate.opsForValue().set(member.getPhone(), authCode, 5, TimeUnit.MINUTES);
    }

    /**
     * 문자로 인증된 코드로 아이디 찾기
     */
    public LoginIdFindResponse findLoginId(AuthCodeForFindRequest request) {
        // Redis에 저장된 인증 코드 가져오기
        String savedAuthCode = redisTemplate.opsForValue().get(request.getPhone());

        // 인증 코드가 일치하는지 체크
        if (savedAuthCode == null || !savedAuthCode.equals(request.getAuthCode())) {
            throw new JikgongException(ErrorCode.MEMBER_INVALID_AUTH_CODE);  // 인증 코드 불일치
        }

        Member member = memberRepository.findByPhone(request.getPhone())
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        // 로그인 아이디 반환
        return LoginIdFindResponse.from(member);
    }

    /**
     * 체류 만료일 조회 api 호출
     * 체류 만료일 정보 업데이트
     */
    public void updateVisaExpiryDate(Long workerId, StayExpirationRequest request) throws JsonProcessingException {
        // codef api 호출
        StayExpirationResponse stayExpirationResponse = stayExpirationService.checkStayExpiration(request);

        if (!"CF-00000".equals(stayExpirationResponse.getResult().getCode())) {
            throw new JikgongException(ErrorCode.CODEF_UNKNOWN_ERROR);
        }

        Member member = memberRepository.findById(workerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        member.updateVisaExpiryDate(stayExpirationResponse);
    }
}
