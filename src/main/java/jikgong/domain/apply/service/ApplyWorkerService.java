package jikgong.domain.apply.service;

import jikgong.domain.apply.dtos.worker.ApplyPendingResponse;
import jikgong.domain.apply.dtos.worker.ApplyResponseForWorker;
import jikgong.domain.apply.dtos.worker.ApplyResponseMonthly;
import jikgong.domain.apply.dtos.worker.ApplySaveRequest;
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
import jikgong.global.utils.TimeTransfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final WorkDateRepository workDateRepository;

    /**
     * 일자리 지원
     * 신청 날짜 기준 최소 이틀 후 출역하는 공고에만 지원 가능
     * 같은 날 이미 승인된 요청이 있을 경우 요청 실패
     */
    public void saveApply(Long workerId, ApplySaveRequest request) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 임시 저장이 아닌 JobPost 조회
        JobPost jobPost = jobPostRepository.findNotTemporaryJobPost(request.getJobPostId(), false)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        // 신청할 날짜 validation 체크
        List<WorkDate> workDateList = validateWorkDate(request, jobPost);

        // 가장 빠른 신청 날짜가 2일 후인지 체크
        validateTwoDaysBefore(workDateList);

        // 중복 신청 체크
        validateDuplication(request, worker, jobPost);

        // 선택한 날 중 이미 승인된 날이 있는 경우 체크
        checkAcceptedApply(workDateList, worker);

        ArrayList<Apply> applyList = new ArrayList<>();
        for (WorkDate workDate : workDateList) {
            applyList.add(new Apply(worker, workDate));
        }
        applyRepository.saveAll(applyList);
    }

    // 선택한 날 중 이미 승인된 날이 있는 경우 체크
    private void checkAcceptedApply(List<WorkDate> workDateList, Member member) {
        List<LocalDate> dateList = workDateList.stream().map(WorkDate::getDate).collect(Collectors.toList());
        List<Apply> acceptedApplyInWorkDateList = applyRepository.checkAcceptedApplyBeforeSave(member.getId(), dateList);
        if (!acceptedApplyInWorkDateList.isEmpty()) {
            throw new CustomException(ErrorCode.APPLY_ALREADY_ACCEPTED_IN_WORKDATE);
        }
    }

    // 중복 신청 체크
    private void validateDuplication(ApplySaveRequest request, Member member, JobPost jobPost) {
        List<Apply> findApply = applyRepository.checkDuplication(member.getId(), jobPost.getId(), request.getWorkDateList());
        if (!findApply.isEmpty()) {
            throw new CustomException(ErrorCode.APPLY_ALREADY_EXIST);
        }
    }

    // 가장 빠른 신청 날짜가 2일 후인지 체크
    private void validateTwoDaysBefore(List<WorkDate> workDateList) {
        // 신청한 날짜 중 가장 빠른 날짜 추출
        LocalDate minWorkDate = workDateList.stream()
                .map(WorkDate::getDate)
                .min(LocalDate::compareTo)
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_WORK_DATE_LIST));

        // 1일날 3일 공고를 신청했을 경우 가능
        // 1일날 2일 공고를 신청했을 경우 불가능
        LocalDate twoDaysInFuture = LocalDate.now().plusDays(2);
        if (twoDaysInFuture.isAfter(minWorkDate)) {
            throw new CustomException(ErrorCode.JOB_POST_EXPIRED);
        }
    }

    private List<WorkDate> validateWorkDate(ApplySaveRequest request, JobPost jobPost) {
        List<WorkDate> workDateList = workDateRepository.findAllByWorkDateAndJobPost(jobPost.getId(), request.getWorkDateList());
        if (workDateList.size() != request.getWorkDateList().size()) {
            throw new CustomException(ErrorCode.WORK_DATE_LIST_NOT_FOUND);
        }
        return workDateList;
    }

    /**
     * 내 일자리
     * 확정된 내역 일별 조회
     */
    public List<ApplyResponseForWorker> findAcceptedApplyWorker(Long workerId, LocalDate date) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<ApplyResponseForWorker> applyResponseList = applyRepository.findApplyPerDay(worker.getId(), date, ApplyStatus.ACCEPTED).stream()
                .map(ApplyResponseForWorker::from)
                .collect(Collectors.toList());

        return applyResponseList;
    }

    /**
     * 내 일자리
     * 달력에 표시할 데이터 반환
     * 확정된 날짜: 초록으로 표시
     * 신청한 날짜: 회색으로 표시
     */
    public List<ApplyResponseMonthly> findAcceptedApplyWorkerMonthly(Long workerId, LocalDate workMonth) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 한달 간의 신청 내역
        List<Apply> applyList = applyRepository.findApplyPerMonth(worker.getId(),
                TimeTransfer.getFirstDayOfMonth(workMonth),
                TimeTransfer.getLastDayOfMonth(workMonth));

        // 지원 날짜와 지원 결과가 담긴 map
        Map<LocalDate, ApplyStatus> workDateMap = getWorkDateMap(applyList);

        List<ApplyResponseMonthly> applyResponseMonthlyList = workDateMap.entrySet().stream()
                .map(entry -> ApplyResponseMonthly.from(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return applyResponseMonthlyList;
    }

    @NotNull
    private Map<LocalDate, ApplyStatus> getWorkDateMap(List<Apply> applyList) {
        Map<LocalDate, ApplyStatus> workDateMap = new HashMap<>();

        for (Apply apply : applyList) {
            LocalDate applyDate = apply.getWorkDate().getDate();
            ApplyStatus currentStatus = apply.getStatus();

            // 매핑된 날짜가 있을 때 -> 수락된 게 있다면 수락 으로 update
            // 매핑된 날짜가 없을 때 -> 추가
            if (workDateMap.containsKey(applyDate) && currentStatus == ApplyStatus.ACCEPTED) {
                workDateMap.put(applyDate, ApplyStatus.ACCEPTED);
            } else {
                workDateMap.put(applyDate, currentStatus);
            }
        }
        return workDateMap;
    }

    /**
     * 신청 진행 중인 내역 조회
     */
    public Page<ApplyPendingResponse> findPendingApply(Long workerId, Pageable pageable) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Page<Apply> pendingApplyPage = applyRepository.findPendingApply(worker.getId(), pageable);

        List<ApplyPendingResponse> pendingApplyList = pendingApplyPage.getContent().stream()
                .map(ApplyPendingResponse::from)
                .collect(Collectors.toList());

        return new PageImpl<>(pendingApplyList, pageable, pendingApplyPage.getTotalElements());
    }
}
