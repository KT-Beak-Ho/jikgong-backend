package jikgong.domain.member.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import jikgong.domain.member.dto.company.CompanySearchResponse;
import jikgong.domain.member.dto.info.CompanyInfoRequest;
import jikgong.domain.member.dto.info.CompanyInfoResponse;
import jikgong.domain.member.dto.info.PasswordRequest;
import jikgong.domain.member.dto.info.WorkerInfoRequest;
import jikgong.domain.member.dto.info.WorkerInfoResponse;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.workexperience.dto.WorkExperienceRequest;
import jikgong.domain.workexperience.entity.WorkExperience;
import jikgong.domain.workexperience.repository.WorkExperienceRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void validationPassword(Long memberId, PasswordRequest request) {
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
}
