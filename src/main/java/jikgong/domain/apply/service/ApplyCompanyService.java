package jikgong.domain.apply.service;

import jikgong.domain.apply.dtos.company.ApplyPendingResponseForCompany;
import jikgong.domain.apply.dtos.company.ApplyProcessRequest;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.history.dtos.CountHistoryResponse;
import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.history.repository.HistoryRepository;
import jikgong.domain.history.service.HistoryService;
import jikgong.domain.jobPost.dtos.JobPostManageWorkerResponse;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.repository.JobPostRepository;
import jikgong.domain.member.dtos.MemberAcceptedResponse;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplyCompanyService {

    private final ApplyRepository applyRepository;
    private final MemberRepository memberRepository;
    private final JobPostRepository jobPostRepository;
    private final HistoryRepository historyRepository;
    private final WorkDateRepository workDateRepository;
    private final HistoryService historyService;

    // 인력 관리: 인부 신청 현황 조회 (회사)
    public Page<ApplyPendingResponseForCompany> findPendingApplyHistoryCompany(Long memberId, Long jobPostId, LocalDate workDate, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        Page<Apply> applyPage = applyRepository.findByJobPostIdAndMemberIdAndStatus(member.getId(), jobPost.getId(), ApplyStatus.PENDING, workDate, pageable);

        List<ApplyPendingResponseForCompany> applyResponseForCompanyList = applyPage.getContent().stream()
                .map(ApplyPendingResponseForCompany::from)
                .collect(Collectors.toList());

        return new PageImpl<>(applyResponseForCompanyList, pageable, applyPage.getTotalElements());
    }

    // 인력 관리: 확정 인부 조회 (회사)
    public JobPostManageWorkerResponse findAcceptedHistoryCompany(Long memberId, Long jobPostId, LocalDate workDate, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        // 유효한 날짜 인지 체크
        Optional<WorkDate> findWorkDate = workDateRepository.findByMemberAndJobPostAndWorkDate(member.getId(), jobPost.getId(), workDate);
        if (findWorkDate.isEmpty()) {
            throw new CustomException(ErrorCode.WORK_DATE_NOT_FOUND);
        }

        // 출석한 member, 결근한 member ID를 저장 하는 Set 생성
        Set<Long> workedMemberIds = new HashSet<>();
        Set<Long> NotWorkedMemberIds = new HashSet<>();
        List<History> workHistoryList = historyRepository.findHistoryByJobPostIdAndWork(jobPost.getId(), workDate, true);
        List<History> NotworkHistoryList = historyRepository.findHistoryByJobPostIdAndWork(jobPost.getId(), workDate, false);
        for (History history : workHistoryList) {
            workedMemberIds.add(history.getMember().getId());
        }
        for (History history : NotworkHistoryList) {
            NotWorkedMemberIds.add(history.getMember().getId());
        }

        Page<Apply> applyPage = applyRepository.findByJobPostIdAndMemberIdAndStatus(member.getId(), jobPost.getId(), ApplyStatus.ACCEPTED, workDate, pageable);

        List<MemberAcceptedResponse> memberAcceptedResponseList = applyPage.getContent().stream()
                .map(MemberAcceptedResponse::fromApply)
                .collect(Collectors.toList());

        // 현재 출근, 결근, 출근 전 status 값 세팅
        for (MemberAcceptedResponse memberAcceptedResponse : memberAcceptedResponseList) {
            if (workedMemberIds.contains(memberAcceptedResponse.getMemberId())) {
                memberAcceptedResponse.setWorkStatus(WorkStatus.WORK);
            } else if (NotWorkedMemberIds.contains(memberAcceptedResponse.getMemberId())) {
                memberAcceptedResponse.setWorkStatus(WorkStatus.NOT_WORK);
            } else {
                memberAcceptedResponse.setWorkStatus(null);
            }
        }
        PageImpl<MemberAcceptedResponse> memberAcceptedResponsePage = new PageImpl<>(memberAcceptedResponseList, pageable, applyPage.getTotalElements());

        CountHistoryResponse countHistory = historyService.findCountHistory(member.getId(), jobPost.getId(), workDate);

        return JobPostManageWorkerResponse.builder()
                .countHistory(countHistory)
                .memberAcceptedResponsePage(memberAcceptedResponsePage)
                .build();
    }

    // 인력 관리: 인부 요청 처리
    public void processApply(Long memberId, ApplyProcessRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findById(request.getJobPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findByMemberAndJobPostAndWorkDate(member.getId(), jobPost.getId(), request.getWorkDate())
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_DATE_NOT_FOUND));

        // 대기 중인 요청인지 체크
        List<Apply> applyList = applyRepository.findByIdList(request.getApplyIdList(), request.getWorkDate());
        for (Apply apply : applyList) {
            if (apply.getStatus() != ApplyStatus.PENDING) {
                throw new CustomException(ErrorCode.APPLY_NEED_TO_PENDING);
            }
        }


        // 모집 인원 초과 체크
        if (workDate.getRegisteredNum() + applyList.size() > workDate.getRecruitNum()) {
            throw new CustomException(ErrorCode.APPLY_OVER_RECRUIT_NUM);
        }

        // applyStatus 갱신
        List<Long> updateApplyIdList = applyList.stream().map(Apply::getId).collect(Collectors.toList());
        ApplyStatus applyStatus = request.getIsAccept() ? ApplyStatus.ACCEPTED : ApplyStatus.REJECTED;
        int updatedCount = applyRepository.updateApplyStatus(updateApplyIdList, applyStatus, LocalDateTime.now());

        // 모집된 인원 갱신
        workDate.plusRegisteredNum(updatedCount);

        // todo: workDate 에 다른 apply 들은 전부 자동 취소
        if (request.getIsAccept()) {
            // 수락 하려는 회원이 같은 날 다른 공고에 지원 했던 요청 취소
            List<Long> memberIdList = applyList.stream().map(apply -> apply.getMember().getId()).collect(Collectors.toList());
            List<Apply> deleteApply = applyRepository.deleteOtherApplyByWorkDate(request.getWorkDate(), request.getApplyIdList(), memberIdList);
            List<Long> cancelApplyIdList = deleteApply.stream().map(Apply::getId).collect(Collectors.toList());
            int canceledCount = applyRepository.updateApplyStatus(cancelApplyIdList, ApplyStatus.CANCELED, LocalDateTime.now());
            log.info("취소된 요청 횟수: " + canceledCount);
        }
    }
}
