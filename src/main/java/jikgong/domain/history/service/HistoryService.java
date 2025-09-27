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

    @Transactional(readOnly = true)
    public List<HistoryManageResponse> findHistories(Long companyId, Long jobPostId, Long workDateId) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findByIdAndMember(company.getId(), jobPostId)
                .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findByIdAndJobPost(jobPost.getId(), workDateId)
                .orElseThrow(() -> new JikgongException(ErrorCode.WORK_DATE_NOT_FOUND));

        return historyRepository.findByWorkDateId(workDate.getId()).stream()
                .map(HistoryManageResponse::from)
                .toList();
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

    public void updateHistory(Long companyId, Long historyId, HistoryPutRequest request) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new JikgongException(ErrorCode.HISTORY_NOT_FOUND));

        history.update(request);

        historyRepository.save(history);
    }
}
