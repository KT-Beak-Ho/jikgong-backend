package jikgong.domain.apply.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import jikgong.domain.apply.dto.worker.ApplyAcceptedResponse;
import jikgong.domain.apply.dto.worker.ApplyHistoryResponse;
import jikgong.domain.apply.dto.worker.ApplyResponseMonthly;
import jikgong.domain.apply.dto.worker.ApplySaveRequest;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.history.entity.History;
import jikgong.domain.history.repository.HistoryRepository;
import jikgong.domain.jobpost.entity.jobpost.JobPost;
import jikgong.domain.jobpost.repository.JobPostRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.workdate.entity.WorkDate;
import jikgong.domain.workdate.repository.WorkDateRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import jikgong.global.utils.TimeTransfer;
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
    public ApplyAcceptedResponse findApplyHistoryPerDay(Long workerId, LocalDate date) {
        Member worker = memberRepository.findById(workerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Optional<Apply> optionalApply = applyRepository.findApplyPerDay(worker.getId(), date, ApplyStatus.ACCEPTED);

        // 신청 내역이 없는 경우 null 값 반환
        if (optionalApply.isPresent()) {
            return null;
        }

        // 확정된 일자리의 출퇴근 기록
        Apply apply = optionalApply.get();
        Optional<History> history = historyRepository.findByMemberAndApply(worker.getId(), apply.getWorkDate().getId());

        return ApplyAcceptedResponse.from(apply, history);
    }

    /**
     * 내 일자리
     * 달력에 표시할 데이터 반환
     * 확정된 날짜: 초록으로 표시
     * 신청한 날짜: 회색으로 표시
     */
    @Transactional(readOnly = true)
    public List<ApplyResponseMonthly> findApplyHistoryPerMonth(Long workerId, LocalDate workMonth) {
        Member worker = memberRepository.findById(workerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        // 한달 간의 신청 내역
        List<Apply> applyList = applyRepository.findApplyPerMonth(worker.getId(),
            TimeTransfer.getFirstDayOfMonth(workMonth),
            TimeTransfer.getLastDayOfMonth(workMonth));

        // 지원 날짜와 지원 결과가 담긴 map
        Map<LocalDate, ApplyStatus> workDateMap = getWorkDateMap(applyList);

        return workDateMap.entrySet().stream()
            .map(entry -> ApplyResponseMonthly.from(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    // 지원 날짜와 지원 결과가 담긴 map
    private Map<LocalDate, ApplyStatus> getWorkDateMap(List<Apply> applyList) {
        Map<LocalDate, ApplyStatus> workDateMap = new HashMap<>();

        for (Apply apply : applyList) {
            LocalDate applyDate = apply.getWorkDate().getDate();
            ApplyStatus currentStatus = apply.getStatus();

            // Map에 해당 날짜의 키가 없거나,
            // 현재 저장된 값이 'ACCEPTED'가 아니며, 새로운 상태가 'ACCEPTED'인 경우 값을 업데이트
            if (!workDateMap.containsKey(applyDate) ||
                (workDateMap.get(applyDate) != ApplyStatus.ACCEPTED && currentStatus == ApplyStatus.ACCEPTED)) {
                workDateMap.put(applyDate, currentStatus);
            }
        }
        return workDateMap;
    }

    /**
     * 신청 진행 중인 내역 조회
     */
    @Transactional(readOnly = true)
    public List<ApplyHistoryResponse> findPendingApply(Long workerId) {
        Member worker = memberRepository.findById(workerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        return applyRepository.findPendingApply(worker.getId()).stream()
            .map(ApplyHistoryResponse::from)
            .collect(Collectors.toList());
    }

    /**
     * 근무 일이 지나지 않은 신청 내역 조회 (대기, 거절, 수락)
     * 웹에서 보여주기 위함
     */
    @Transactional(readOnly = true)
    public List<ApplyHistoryResponse> findApplyFutureHistory(Long workerId) {
        Member worker = memberRepository.findById(workerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        // 조회하고자 하는 상태
        List<ApplyStatus> statusList = Arrays.asList(
            ApplyStatus.PENDING,
            ApplyStatus.REJECTED,
            ApplyStatus.ACCEPTED
        );

        return applyRepository.findFutureApply(worker.getId(), statusList, LocalDate.now()).stream()
                .map(ApplyHistoryResponse::from)
                .toList();
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
