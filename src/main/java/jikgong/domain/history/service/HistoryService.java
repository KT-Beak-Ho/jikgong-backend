package jikgong.domain.history.service;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.history.dto.*;
import jikgong.domain.history.entity.History;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final MemberRepository memberRepository;
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
    public List<HistoryManageResponse> findHistoriesByWorkDate(Long companyId, Long workDateId) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        return historyRepository.findByWorkDateId(workDateId).stream()
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

    public void updateHistory(Long companyId, Long historyId, HistoryPutRequest request) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        History history = historyRepository.findByIdAndCompanyId(historyId, companyId)
                .orElseThrow(() -> new JikgongException(ErrorCode.HISTORY_NOT_FOUND));

        history.update(request);
    }
}
