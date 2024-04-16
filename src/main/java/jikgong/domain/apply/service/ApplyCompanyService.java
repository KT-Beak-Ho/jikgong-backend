package jikgong.domain.apply.service;

import jikgong.domain.apply.dtos.company.ApplyManageResponse;
import jikgong.domain.apply.dtos.company.ApplyProcessRequest;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.repository.JobPostRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.workDate.entity.WorkDate;
import jikgong.domain.workDate.repository.WorkDateRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplyCompanyService {

    private final ApplyRepository applyRepository;
    private final MemberRepository memberRepository;
    private final JobPostRepository jobPostRepository;
    private final WorkDateRepository workDateRepository;

    /**
     * 인력 관리
     * 지원자 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ApplyManageResponse> findPendingApplyHistoryCompany(Long companyId, Long jobPostId, Long workDateId, Pageable pageable) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findByIdAndMember(company.getId(), jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findById(workDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_DATE_NOT_FOUND));

        Page<Apply> applyPage = applyRepository.findApplyForCompanyByApplyStatus(company.getId(), jobPost.getId(), workDate.getId(), ApplyStatus.PENDING, pageable);

        List<ApplyManageResponse> applyManageResponseList = applyPage.getContent().stream()
                .map(ApplyManageResponse::from)
                .collect(Collectors.toList());

        return new PageImpl<>(applyManageResponseList, pageable, applyPage.getTotalElements());
    }

    /**
     * 인력 관리
     * 확정 인부 조회
     */
    @Transactional(readOnly = true)
    public Page<ApplyManageResponse> findAcceptedHistoryCompany(Long companyId, Long jobPostId, Long workDateId, Pageable pageable) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findByIdAndMember(company.getId(), jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findById(workDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_DATE_NOT_FOUND));

        Page<Apply> applyPage = applyRepository.findApplyForCompanyByApplyStatus(company.getId(), jobPost.getId(), workDate.getId(), ApplyStatus.ACCEPTED, pageable);

        List<ApplyManageResponse> applyManageResponseList = applyPage.getContent().stream()
                .map(ApplyManageResponse::from)
                .collect(Collectors.toList());

        return new PageImpl<>(applyManageResponseList, pageable, applyPage.getTotalElements());
    }

    /**
     * 인력 관리
     * 인부 지원 요청 처리 (수락, 거절)
     * apply status 갱신 후 같은 날 다른 공고에 대한 지원 취소
     */
    public void processApply(Long companyId, ApplyProcessRequest request) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findByIdAndMember(company.getId(), request.getJobPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findById(request.getWorkDateId())
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_DATE_NOT_FOUND));

        // 대기 중인 요청인지 체크
        List<Apply> applyList = checkPendingApply(request, workDate, jobPost);

        // 모집 인원 초과 체크
        checkRegisteredNumOver(workDate, applyList);

        // applyStatus 갱신
        List<Long> updateApplyIdList = applyList.stream().map(Apply::getId).collect(Collectors.toList());
        ApplyStatus applyStatus = request.getIsAccept() ? ApplyStatus.ACCEPTED : ApplyStatus.REJECTED;
        int updatedCount = applyRepository.updateApplyStatus(updateApplyIdList, applyStatus, LocalDateTime.now());

        // 모집된 인원 갱신
        workDate.plusRegisteredNum(updatedCount);

        // 수락 하려는 회원이 같은 날 다른 공고에 지원 했던 요청 취소
        if (request.getIsAccept()) {
            cancelAnotherApply(applyList, workDate);
        }
    }

    // 모집 인원 초과 체크
    private void checkRegisteredNumOver(WorkDate workDate, List<Apply> applyList) {
        if (workDate.getRegisteredNum() + applyList.size() > workDate.getRecruitNum()) {
            throw new CustomException(ErrorCode.APPLY_OVER_RECRUIT_NUM);
        }
    }

    // 대기 중인 요청인지 체크
    private List<Apply> checkPendingApply(ApplyProcessRequest request, WorkDate workDate, JobPost jobPost) {
        List<Apply> applyList = applyRepository.findByIdList(request.getApplyIdList(), workDate.getId(), jobPost.getId());
        for (Apply apply : applyList) {
            if (apply.getStatus() != ApplyStatus.PENDING) {
                throw new CustomException(ErrorCode.APPLY_NEED_TO_PENDING);
            }
        }
        return applyList;
    }

    // 같은 날 다른 공고에 지원 했던 대기중인 요청 취소
    private void cancelAnotherApply(List<Apply> applyList, WorkDate workDate) {
        List<Long> memberIdList = applyList.stream().map(apply -> apply.getMember().getId()).collect(Collectors.toList());
        List<Long> cancelApplyIdList = applyRepository.deleteOtherApplyOnDate(memberIdList, workDate.getDate());
        int canceledCount = applyRepository.updateApplyStatus(cancelApplyIdList, ApplyStatus.CANCELED, LocalDateTime.now());
        log.info("취소된 요청 횟수: " + canceledCount);
    }
}
