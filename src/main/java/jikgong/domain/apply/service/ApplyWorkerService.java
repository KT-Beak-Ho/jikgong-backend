package jikgong.domain.apply.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import jikgong.domain.apply.dto.worker.*;
import jikgong.domain.apply.dto.worker.ApplyDailyGetResponse.ApplyProgress.StatusDecisionTime;
import jikgong.domain.apply.dto.worker.ApplyDailyGetResponse.ApplyProgress;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.history.repository.HistoryRepository;
import jikgong.domain.jobpost.entity.jobpost.JobPost;
import jikgong.domain.jobpost.repository.JobPostRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.profit.repository.ProfitRepository;
import jikgong.domain.workdate.entity.WorkDate;
import jikgong.domain.workdate.repository.WorkDateRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplyWorkerService {

    private final ApplyRepository applyRepository;
    private final MemberRepository memberRepository;
    private final JobPostRepository jobPostRepository;
    private final WorkDateRepository workDateRepository;
    private final HistoryRepository historyRepository;
    private final ProfitRepository profitRepository;

    /**
     * 일자리 지원,
     * 신청 날짜 기준 최소 이틀 후 출역하는 공고에만 지원 가능
     * 같은 날 이미 승인된 요청이 있을 경우 요청 실패
     */
    public void saveApply(Long workerId, ApplySaveRequest request) {
        Member worker = memberRepository.findById(workerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        // 임시 저장이 아닌 JobPost 조회
        JobPost jobPost = jobPostRepository.findNotTemporaryJobPost(request.getJobPostId(), false)
            .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));

        // 신청할 날짜 validation 체크
        List<WorkDate> workDateList = validateWorkDate(request, jobPost);

        // 승인 가능 시각 체크
        validateApplyTime(workDateList);

        // 중복 신청 체크
        validateDuplication(request, worker, jobPost);

        // 선택한 날 중 이미 승인된 날이 있는 경우 체크
        checkAcceptedApply(workDateList, worker);

        ArrayList<Apply> applyList = new ArrayList<>();
        for (WorkDate workDate : workDateList) {
            applyList.add(new Apply(ApplyStatus.PENDING, false, worker, workDate));
        }
        applyRepository.saveAll(applyList);
    }

    // 선택한 날 중 이미 승인된 날이 있는 경우 체크
    private void checkAcceptedApply(List<WorkDate> workDateList, Member member) {
        List<LocalDate> dateList = workDateList.stream().map(WorkDate::getDate).collect(Collectors.toList());
        List<Apply> acceptedApplyInWorkDateList = applyRepository.checkAcceptedApplyForApply(member.getId(), dateList);
        if (!acceptedApplyInWorkDateList.isEmpty()) {
            throw new JikgongException(ErrorCode.APPLY_ALREADY_ACCEPTED_IN_WORK_DATE);
        }
    }

    // 중복 신청 체크
    private void validateDuplication(ApplySaveRequest request, Member member, JobPost jobPost) {
        List<Apply> findApply = applyRepository.checkDuplication(member.getId(), jobPost.getId(),
            request.getWorkDateList());
        if (!findApply.isEmpty()) {
            throw new JikgongException(ErrorCode.APPLY_ALREADY_EXIST);
        }
    }

    // 신청 가능 시각 체크
    private void validateApplyTime(List<WorkDate> workDateList) {
        // 신청한 날짜 중 가장 빠른 WorkDate 추출
        WorkDate workDate = workDateList.stream()
            .min(Comparator.comparing(WorkDate::getDate))
            .orElseThrow(() -> new JikgongException(ErrorCode.WORK_DATE_LIST_NOT_FOUND));

        JobPost jobPost = workDate.getJobPost();

        LocalTime startTime = jobPost.getStartTime(); // 출역 시각
        LocalDate date = workDate.getDate(); // 출역 날짜
        LocalDateTime now = LocalDateTime.now();

        // 출역 시각 10분 전 까지 신청 가능
        if (now.isAfter(LocalDateTime.of(date, startTime).minusMinutes(10))) {
            throw new JikgongException(ErrorCode.APPLY_CAN_TEN_MINUTE_AGO);
        }
    }

    // 신청할 날짜 validation 체크
    private List<WorkDate> validateWorkDate(ApplySaveRequest request, JobPost jobPost) {
        List<WorkDate> workDateList = workDateRepository.checkWorkDateBeforeApply(jobPost.getId(),
            request.getWorkDateList());
        if (workDateList.size() != request.getWorkDateList().size()) {
            throw new JikgongException(ErrorCode.WORK_DATE_LIST_NOT_FOUND);
        }
        return workDateList;
    }

    /**
     * 내 일자리
     * 확정된 내역 일별 조회
     */
    @Transactional(readOnly = true)
    public List<ApplyDailyGetResponse> findAppliesDaily(Long workerId, ApplyDailyGetRequest request) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        List<Apply> applies = applyRepository.findAppliesWithWorkDate(worker.getId(), request.getDate());

        Optional<Apply> acceptedApply = applies.stream()
                .filter(apply -> apply.getStatus() == ApplyStatus.ACCEPTED)
                .findFirst();

        if (acceptedApply.isPresent()) {
            return acceptedApply.stream()
                    .map(apply -> ApplyDailyGetResponse.from(
                            apply,
                            getApplyProgress(workerId, apply)))
                    .toList();
        } else {
            return applies.stream()
                    .map(apply -> ApplyDailyGetResponse.from(
                            apply,
                            null))
                    .toList();
        }
    }

    @Transactional(readOnly = true)
    public ApplyProgress getApplyProgress(Long workerId, Apply apply) {
        LocalDate workDate = apply.getWorkDate().getDate();
        ApplyProgress.ApplyProgressBuilder progressBuilder = ApplyProgress.builder();
        final AtomicReference<StatusDecisionTime> lastProgressedRef = new AtomicReference<>();

        // 1. 신청 생성 상태
        StatusDecisionTime created = new StatusDecisionTime(
                apply.getIsOffer() ? ApplyStatus.OFFERED.getDescription()
                        : "신청됨",
                apply.getCreatedDate()
        );
        progressBuilder.created(created);
        lastProgressedRef.set(created);

        // 2. 결과 확정 상태
        if (apply.getStatusDecisionTime() != null) {
            StatusDecisionTime decided = new StatusDecisionTime(
                    apply.getStatus().getDescription(),
                    apply.getStatusDecisionTime()
            );
            progressBuilder.decided(decided);
            lastProgressedRef.set(decided);
        }

        // 3. 근무 기록 상태
        historyRepository.findByWorkDate(workerId, workDate).ifPresent(history -> {
            // 출근 정보
            StatusDecisionTime workStarted = new StatusDecisionTime(
                    history.getStartStatus().getDescription(),
                    history.getStartStatusDecisionTime()
            );
            progressBuilder.workStarted(workStarted);
            lastProgressedRef.set(workStarted);

            // 퇴근 정보
            if (history.getEndStatus() != null) {
                StatusDecisionTime workEnded = new StatusDecisionTime(
                        history.getEndStatus().getDescription(),
                        history.getEndStatusDecisionTime()
                );
                progressBuilder.workEnded(workEnded);
                lastProgressedRef.set(workEnded);
            }
        });

        // 4. 근무 정산 상태
        profitRepository.findByWorkDate(workerId, workDate).ifPresent(profit -> {
            StatusDecisionTime paid = new StatusDecisionTime(
                    profit.getProfitType().getDescription(),
                    profit.getDate().atStartOfDay()
            );
            progressBuilder.paid(paid);
            lastProgressedRef.set(paid);
        });

        return progressBuilder.lastProgressed(lastProgressedRef.get()).build();
    }

    /**
     * 내 일자리
     * 달력에 표시할 데이터 반환
     * 확정된 날짜: 초록으로 표시
     * 신청한 날짜: 회색으로 표시
     */
    @Transactional(readOnly = true)
    public List<ApplyStatusGetResponse> findApplyStatus(Long workerId, ApplyGetRequest request) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        List<Apply> applyList = applyRepository.findAppliesWithWorkDate(worker.getId(), request);

        Map<LocalDate, ApplyStatus> workDateMap = getWorkDateMap(applyList);

        return workDateMap.entrySet().stream()
                .map(entry -> ApplyStatusGetResponse.from(
                        entry.getKey(),
                        entry.getValue()))
                .toList();
    }

    // 지원 날짜와 지원 결과가 담긴 map
    private Map<LocalDate, ApplyStatus> getWorkDateMap(List<Apply> applyList) {
        return applyList.stream().collect(Collectors.toMap(
                apply -> apply.getWorkDate().getDate(), // Key: 지원 날짜
                Apply::getStatus,                             // Value: 지원 상태
                (existingStatus, newStatus) -> // 중복 시 병합 규칙 (ACCEPTED 우선 결합)
                        newStatus == ApplyStatus.ACCEPTED ? newStatus : existingStatus)
        );
    }

    /**
     * 요청 취소
     * 요청 취소 가능 조건
     * 1. 확정된 지 24시간 (이내 or 이후)  미확정
     * 2. 출역 3일 전
     * 3. 수락 전인 요청 (대기 중)
     */
    public void cancelApply(Long workerId, Long applyId) {
        Member worker = memberRepository.findById(workerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Apply apply = applyRepository.findCancelApply(worker.getId(), applyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.APPLY_NOT_FOUND));

        // jobPost 조회 (fetch join)
        JobPost jobPost = apply.getWorkDate().getJobPost();

        if (apply.getStatus() == ApplyStatus.ACCEPTED) {
            // 수락된 경우 확정 난지 24시간 후 가능
            if (LocalDateTime.now().isBefore(apply.getStatusDecisionTime().plusDays(1L))) {
                throw new JikgongException(ErrorCode.APPLY_CANCEL_IMPOSSIBLE);
            }

            // 5일이 출역일이라면
            // 3일: 취소 가능  |  4일: 취소 불가
            if (LocalDate.now().isAfter(jobPost.getStartDate().minusDays(2L))) {
                throw new JikgongException(ErrorCode.APPLY_CANCEL_IMPOSSIBLE);
            }
        }

        // 수락, 대기 상태일때만 취소 가능
        if (apply.getStatus() != ApplyStatus.PENDING && apply.getStatus() != ApplyStatus.ACCEPTED) {
            throw new JikgongException(ErrorCode.APPLY_CANCEL_IMPOSSIBLE);
        }

        // status 업데이트
        apply.updateStatus(ApplyStatus.CANCELED, LocalDateTime.now());

        // 수락이었을 경우 모집된 인원 업데이트
        if (apply.getStatus() == ApplyStatus.ACCEPTED) {
            apply.getWorkDate().minusRegisteredNum();
        }
    }
}
