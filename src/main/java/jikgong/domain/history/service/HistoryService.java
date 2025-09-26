package jikgong.domain.history.service;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.history.dto.*;
import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.history.repository.HistoryRepository;
import jikgong.domain.jobpost.entity.jobpost.JobPost;
import jikgong.domain.jobpost.repository.JobPostRepository;
import jikgong.domain.history.dto.HistoryManageResponse;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.workdate.entity.WorkDate;
import jikgong.domain.workdate.repository.WorkDateRepository;
import jikgong.global.exception.JikgongException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final MemberRepository memberRepository;
    private final ApplyRepository applyRepository;
    private final JobPostRepository jobPostRepository;
    private final WorkDateRepository workDateRepository;

    public void createHistoriesByApplies(Long companyId, List<Apply> applies) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        List<History> histories = applies.stream()
                .map(History::from)
                .toList();

        historyRepository.saveAll(histories);
    }

    /**
     * 출근, 결근 여부 저장
     * 기존 출근, 결근에 대한 정보가 있다면 제거 후 저장
     */
    public int saveHistoryAtStart(Long companyId, HistoryStartSaveRequest request) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findByIdAndMember(company.getId(), request.getJobPostId())
            .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findByIdAndJobPost(jobPost.getId(), request.getWorkDateId())
            .orElseThrow(() -> new JikgongException(ErrorCode.WORK_DATE_NOT_FOUND));

        // 승인된 지원인지 체크
        List<Apply> applyList = applyRepository.checkApplyBeforeSaveHistory(workDate.getId(),
            request.getStartWorkMemberIdList(), request.getNotWorkMemberIdList(), ApplyStatus.ACCEPTED);
        if (applyList.size() != request.getStartWorkMemberIdList().size() + request.getNotWorkMemberIdList().size()) {
            throw new JikgongException(ErrorCode.HISTORY_NOT_FOUND_APPLY);
        }

        // 기존에 history 데이터가 있다면 제거
        // 출근, 결근 데이터를 변경하면 기존의 퇴근, 조퇴 데이터 사라짐
        int deleteCount = historyRepository.deleteByWorkDateAndAndMember(request.getStartWorkMemberIdList(),
            request.getNotWorkMemberIdList(), request.getWorkDateId());
        log.info("기존 history 제거 개수: " + deleteCount);

        List<History> saveHistoryList = new ArrayList<>();

        // JPA 1차 캐싱 처리
        getMemberList(request);

        LocalDateTime now = LocalDateTime.now();
        // 출근 history
        for (Long targetMemberId : request.getStartWorkMemberIdList()) {
            Member worker = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
            saveHistoryList.add(History.createEntity(WorkStatus.START_WORK, now, worker, workDate));
        }

        // 결근 history
        for (Long targetMemberId : request.getNotWorkMemberIdList()) {
            Member worker = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
            saveHistoryList.add(History.createEntity(WorkStatus.NOT_WORK, now, worker, workDate));
        }

        return historyRepository.saveAll(saveHistoryList).size();
    }

    // JPA 1차 캐싱 처리
    private void getMemberList(HistoryStartSaveRequest request) {
        List<Long> memberIdList = new ArrayList<>();
        memberIdList.addAll(request.getStartWorkMemberIdList());
        memberIdList.addAll(request.getNotWorkMemberIdList());
        memberRepository.findByIdList(memberIdList);
    }

    /**
     * 결근, 조퇴 여부 저장
     */
    public int updateHistoryAtFinish(Long companyId, HistoryFinishSaveRequest request) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findByIdAndMember(company.getId(), request.getJobPostId())
            .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findByIdAndJobPost(jobPost.getId(), request.getWorkDateId())
            .orElseThrow(() -> new JikgongException(ErrorCode.WORK_DATE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        // 퇴근
        int updateFinishWork = historyRepository.updateHistoryByIdList(workDate.getId(),
            request.getFinishWorkHistoryIdList(), WorkStatus.FINISH_WORK, now);
        log.info("퇴근 처리된 데이터: " + updateFinishWork);

        // 조퇴
        int updateEarlyLeave = historyRepository.updateHistoryByIdList(workDate.getId(),
            request.getEarlyLeaveHistoryIdList(), WorkStatus.EARLY_LEAVE, now);
        log.info("조퇴 처리된 데이터: " + updateFinishWork);

        // 요청한 데이터와 업데이트한 데이터 비교
        if (updateFinishWork != request.getFinishWorkHistoryIdList().size()
            && updateEarlyLeave != request.getEarlyLeaveHistoryIdList().size()) {
            throw new JikgongException(ErrorCode.HISTORY_UPDATE_FAIL);
        }

        return updateFinishWork + updateEarlyLeave;
    }


    @Transactional(readOnly = true)
    public List<HistoryManageResponse> findHistories(Long companyId, Long jobPostId, Long workDateId) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findByIdAndMember(company.getId(), jobPostId)
                .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findByIdAndJobPost(jobPost.getId(), workDateId)
                .orElseThrow(() -> new JikgongException(ErrorCode.WORK_DATE_NOT_FOUND));

        return historyRepository.findByWorkDateForCompany(company.getId(), workDate.getDate()).stream()
                .map(HistoryManageResponse::from)
                .toList();
    }

    /**
     * 출근, 결근 조회
     * 지원한 목록 조회 후
     * 해당 일짜에 이미 history 있는 지원자일 경우으로 status 분류
     * status: [출근, 결근, 출근 전]
     * 지원이 확정된 사람 중, 몇몇은 이미 history 데이터가 있을 것이고, 몇몇은 없기 때문에 위와 같이 작성
     */
    @Transactional(readOnly = true)
    public List<HistoryManageResponse> findHistoryAtStart(Long companyId, Long jobPostId, Long workDateId) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findByIdAndMember(company.getId(), jobPostId)
            .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findByIdAndJobPost(jobPost.getId(), workDateId)
            .orElseThrow(() -> new JikgongException(ErrorCode.WORK_DATE_NOT_FOUND));

        // key: memberId  |  value: history
        // 출근 기록된 member, 결근 기록된 member
        Map<Long, History> startWorkMember = createHistoryMap(workDate.getId(), WorkStatus.START_WORK);
        Map<Long, History> notWorkMember = createHistoryMap(workDate.getId(), WorkStatus.NOT_WORK);

        // 지원 내역 조회
        List<Apply> applyList = applyRepository.findApplyBeforeHistoryProcess(workDate.getId(), ApplyStatus.ACCEPTED);

        List<HistoryManageResponse> historyManageResponseList = applyList.stream()
            .map(HistoryManageResponse::from)
            .collect(Collectors.toList());

        // 현재 출근, 결근, 출근 전 status 값 세팅
        for (HistoryManageResponse historyManageResponse : historyManageResponseList) {
            HistoryManageResponse.MemberResponse memberResponse = historyManageResponse.getMemberResponse();
            if (startWorkMember.containsKey(memberResponse.getMemberId())) {
                historyManageResponse.updateHistoryInfo(startWorkMember.get(memberResponse.getMemberId()));
            } else if (notWorkMember.containsKey(memberResponse.getMemberId())) {
                historyManageResponse.updateHistoryInfo(notWorkMember.get(memberResponse.getMemberId()));
            }
        }

        return historyManageResponseList;
    }

    // key: memberId  |  value: history  map 생성
    private Map<Long, History> createHistoryMap(Long workDateId, WorkStatus status) {
        return historyRepository.findHistoryBeforeProcess(workDateId, status).stream()
            .collect(Collectors.toMap(history -> history.getMember().getId(), Function.identity()));
    }

    /**
     * 출근, 결근 기록이 있는 노동자 기록만 조회
     * 퇴근, 조퇴 조회
     * 퇴근, 조퇴 조회지만 결근했던 노동자도 함께 반환
     */
    @Transactional(readOnly = true)
    public HistoryAtFinishResponse findHistoryAtFinish(Long companyId, Long jobPostId, Long workDateId) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findByIdAndMember(company.getId(), jobPostId)
            .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findByIdAndJobPost(jobPost.getId(), workDateId)
            .orElseThrow(() -> new JikgongException(ErrorCode.WORK_DATE_NOT_FOUND));

        // 출근한 기록, 결근한 기록 조회
        List<History> workHistoryList = historyRepository.findHistoryBeforeProcess(workDate.getId(),
            WorkStatus.START_WORK);
        List<History> notWorkHistoryList = historyRepository.findHistoryBeforeProcess(workDate.getId(),
            WorkStatus.NOT_WORK);

        List<HistoryManageResponse> workMemberResponse = workHistoryList.stream()
            .map(HistoryManageResponse::from)
            .collect(Collectors.toList());
        List<HistoryManageResponse> notWorkMemberResponse = notWorkHistoryList.stream()
            .map(HistoryManageResponse::from)
            .collect(Collectors.toList());

        return HistoryAtFinishResponse.builder()
            .workMemberResponse(workMemberResponse)
            .notWorkMemberResponse(notWorkMemberResponse)
            .build();
    }

    /**
     * 지급 내역서 확인
     * 해당 일짜의 지급 내역서 확인
     */
    @Transactional(readOnly = true)
    public PaymentStatementResponse findPaymentStatement(Long companyId, Long jobPostId, Long workDateId) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findByIdAndMember(company.getId(), jobPostId)
            .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findByIdAndJobPost(jobPost.getId(), workDateId)
            .orElseThrow(() -> new JikgongException(ErrorCode.WORK_DATE_NOT_FOUND));

        List<PaymentMemberInfo> paymentMemberInfoList = historyRepository.findPaymentStatementInfo(workDate.getId())
            .stream()
            .map(PaymentMemberInfo::from)
            .collect(Collectors.toList());

        return PaymentStatementResponse.from(paymentMemberInfoList);
    }
}
