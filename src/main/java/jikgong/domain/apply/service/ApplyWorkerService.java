package jikgong.domain.apply.service;

import jikgong.domain.apply.dtos.worker.ApplyPendingResponse;
import jikgong.domain.apply.dtos.worker.ApplyResponseForWorker;
import jikgong.domain.apply.dtos.worker.ApplyResponseMonthly;
import jikgong.domain.apply.dtos.worker.ApplySaveRequest;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.history.repository.HistoryRepository;
import jikgong.domain.history.service.HistoryService;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.repository.JobPostRepository;
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
public class ApplyWorkerService {
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

        // 신청할 날짜 validation 체크
        List<WorkDate> workDateList = workDateRepository.findAllByWorkDateAndJobPost(jobPost.getId(), request.getWorkDateList());
        if (workDateList.size() != request.getWorkDateList().size()) {
            throw new CustomException(ErrorCode.WORK_DATE_LIST_NOT_FOUND);
        }

        // 중복 신청 조회
        List<Apply> findApply = applyRepository.findByMemberIdAndJobPostId(member.getId(), jobPost.getId(), request.getWorkDateList());
        if (!findApply.isEmpty()) {
            throw new CustomException(ErrorCode.APPLY_ALREADY_EXIST);
        }

        // 선택한 날 중 이미 승인된 날이 있는 경우
        List<LocalDate> dateList = workDateList.stream().map(WorkDate::getWorkDate).collect(Collectors.toList());
        List<Apply> acceptedApplyInWorkDateList = applyRepository.findAcceptedApplyInWorkDateList(member.getId(), dateList);
        if (!acceptedApplyInWorkDateList.isEmpty()) {
            throw new CustomException(ErrorCode.APPLY_ALREADY_ACCEPTED_IN_WORKDATE);
        }

        // 모집 기한 체크
        if (jobPost.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.JOB_POST_EXPIRED);
        }

        ArrayList<Apply> applyList = new ArrayList<>();
        for (WorkDate workDate : workDateList) {
            applyList.add(new Apply(member, workDate));
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

    public Page<ApplyPendingResponse> findPendingApply(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Page<Apply> pendingApplyPage = applyRepository.findPendingApply(member.getId(), pageable);

        List<ApplyPendingResponse> pendingApplyList = pendingApplyPage.getContent().stream()
                .map(ApplyPendingResponse::from)
                .collect(Collectors.toList());

        return new PageImpl<>(pendingApplyList, pageable, pendingApplyPage.getTotalElements());
    }
}
