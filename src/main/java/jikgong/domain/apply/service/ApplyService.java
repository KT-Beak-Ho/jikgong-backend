package jikgong.domain.apply.service;

import jikgong.domain.apply.dtos.company.ApplyPendingResponseForCompany;
import jikgong.domain.apply.dtos.company.ApplyProcessRequest;
import jikgong.domain.apply.dtos.worker.ApplyResponseForWorker;
import jikgong.domain.apply.dtos.worker.ApplyResponseMonthly;
import jikgong.domain.apply.dtos.worker.ApplySaveRequest;
import jikgong.domain.history.dtos.CountHistory;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.history.entity.History;
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
import jikgong.global.utils.TimeTransfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplyService {
    private final ApplyRepository applyRepository;
    private final MemberRepository memberRepository;
    private final JobPostRepository jobPostRepository;
    private final HistoryRepository historyRepository;
    private final WorkDateRepository workDateRepository;
    private final HistoryService historyService;

    public void saveApply(Long memberId, ApplySaveRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 임시 저장이 아닌 JobPost 조회
        JobPost jobPost = jobPostRepository.findJobPostByIdAndTemporary(request.getJobPostId(), false)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        // 신청할 날짜 조회
        List<WorkDate> workDateList = workDateRepository.findAllByWorkDateAndJobPost(jobPost.getId(), request.getWorkDateList());

        if (workDateList.size() != request.getWorkDateList().size()) {
            throw new CustomException(ErrorCode.WORK_DATE_LIST_NOT_FOUND);
        }

        // 중복 신청 조회
        Optional<Apply> findApply = applyRepository.findByMemberIdAndJobPostId(member.getId(), jobPost.getId());
        if (findApply.isPresent()) {
            throw new CustomException(ErrorCode.APPLY_ALREADY_EXIST);
        }

        // 모집 기한 체크
        if (jobPost.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.JOB_POST_EXPIRED);
        }

        ArrayList<Apply> applyList = new ArrayList<>();
        for (WorkDate workDate : workDateList) {
            Apply apply = Apply.builder()
                    .member(member)
                    .workDate(workDate)
                    .member(member)
                    .build();
            applyList.add(apply);
        }
        applyRepository.saveAll(applyList);
    }

    // 신청 내역: 일별 조회
    public List<ApplyResponseForWorker> findAcceptedApplyWorker(Long memberId, LocalDate workDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<ApplyResponseForWorker> applyResponseList = applyRepository.findByMemberAndWorkDate(member.getId(), workDate, ApplyStatus.ACCEPTED).stream()
                .map(ApplyResponseForWorker::from)
                .collect(Collectors.toList());

        return applyResponseList;
    }

    // 신청 내역: 월별 조회
    public List<ApplyResponseMonthly> findAcceptedApplyWorkerMonthly(Long memberId, LocalDate workMonth) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Apply> applyList = applyRepository.findByMemberAndWorkMonth(member.getId(),
                TimeTransfer.getFirstDayOfMonth(workMonth),
                TimeTransfer.getLastDayOfMonth(workMonth));

        Map<LocalDate, ApplyStatus> workDateMap = new HashMap<>();

        for (Apply apply : applyList) {
            LocalDate applyDate = apply.getWorkDate().getWorkDate();
            ApplyStatus currentStatus = apply.getStatus();

            // 매핑된 날짜가 있을 때 -> 수락된 게 있다면 수락 으로 update
            // 매핑된 날짜가 없을 때 -> 추가
            if (workDateMap.containsKey(applyDate) && currentStatus == ApplyStatus.ACCEPTED) {
                workDateMap.put(applyDate, ApplyStatus.ACCEPTED);
            } else {
                workDateMap.put(applyDate, currentStatus);
            }
        }

        List<ApplyResponseMonthly> applyResponseMonthlyList = workDateMap.entrySet().stream()
                .map(entry -> ApplyResponseMonthly.from(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return applyResponseMonthlyList;
    }

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

        CountHistory countHistory = historyService.findCountHistory(member.getId(), jobPost.getId(), workDate);

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
        int updatedCount = applyRepository.updateApplyStatus(updateApplyIdList, applyStatus);

        // 모집된 인원 갱신
        workDate.plusRegisteredNum(updatedCount);
    }


}
