package jikgong.domain.history.service;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.history.dtos.*;
import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.history.repository.HistoryRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 출근, 결근 여부 저장
     * 기존 출근, 결근에 대한 정보가 있다면 제거 후 저장
     */
    public int saveHistoryAtStart(Long companyId, HistoryStartSaveRequest request) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findById(request.getJobPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findById(request.getWorkDateId())
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_DATE_NOT_FOUND));

        // 지원한 데이터에 대한 처리인지 체크
        List<Apply> applyList =   applyRepository.checkApplyBeforeSaveHistory(jobPost.getId(), workDate.getId(), request.getStartWorkMemberIdList(), request.getNotWorkMemberIdList(), ApplyStatus.ACCEPTED);
        if (applyList.size() != request.getStartWorkMemberIdList().size() + request.getNotWorkMemberIdList().size()) {
            throw new CustomException(ErrorCode.HISTORY_NOT_FOUND_APPLY);
        }

        // 기존에 history 데이터가 있다면 제거
        int deleteCount = historyRepository.deleteByWorkDateAndAndMember(request.getStartWorkMemberIdList(), request.getNotWorkMemberIdList(), request.getWorkDateId());
        log.info("기존 history 제거 개수: " + deleteCount);

        List<History> saveHistoryList = new ArrayList<>();

        // JPA 1차 캐싱 처리
        getMemberList(request);

        // 출근 history
        for (Long targetMemberId : request.getStartWorkMemberIdList()) {
            Member targetMember = memberRepository.findById(targetMemberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
            saveHistoryList.add(History.createEntity(WorkStatus.START_WORK, targetMember, workDate));
        }

        // 결근 history
        for (Long targetMemberId : request.getNotWorkMemberIdList()) {
            Member targetMember = memberRepository.findById(targetMemberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
            saveHistoryList.add(History.createEntity(WorkStatus.NOT_WORK, targetMember, workDate));
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
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findById(request.getJobPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findById(request.getWorkDateId())
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_DATE_NOT_FOUND));

        // 퇴근
        int updateFinishWork = historyRepository.updateHistoryByIdList(request.getFinishWorkHistoryIdList(), WorkStatus.FINISH_WORK);
        log.info("퇴근 처리된 데이터: " + updateFinishWork);

        // 조퇴
        int updateEarlyLeave = historyRepository.updateHistoryByIdList(request.getEarlyLeaveHistoryIdList(), WorkStatus.EARLY_LEAVE);
        log.info("조퇴 처리된 데이터: " + updateFinishWork);

        // 요청한 데이터와 업데이트한 데이터 비교
        if (updateFinishWork != request.getFinishWorkHistoryIdList().size() && updateEarlyLeave != request.getEarlyLeaveHistoryIdList().size()) {
            throw new CustomException(ErrorCode.HISTORY_UPDATE_FAIL);
        }

        return updateFinishWork + updateEarlyLeave;
    }

    /**
     * 출근, 결근 조회
     * 지원한 목록 조회 후
     * 출근, 결근, 출근 전 으로 status 분류
     */
    public List<MemberAcceptedResponse> findApplyWithHistoryAtStart(Long companyId, Long jobPostId, Long workDateId) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findById(workDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_DATE_NOT_FOUND));

        // key: memberId  |  value: history
        Map<Long, History> startWorkMember = createHistoryMap(jobPost.getId(), workDate.getId(), WorkStatus.START_WORK);
        Map<Long, History> notWorkMember = createHistoryMap(jobPost.getId(), workDate.getId(), WorkStatus.NOT_WORK);

        List<Apply> applyList = applyRepository.findApplyAtStartWorkCheck(company.getId(), jobPost.getId(), workDate.getId(), ApplyStatus.ACCEPTED);

        List<MemberAcceptedResponse> memberAcceptedResponseList = applyList.stream()
                .map(MemberAcceptedResponse::from)
                .collect(Collectors.toList());

        // 현재 출근, 결근, 출근 전 status 값 세팅
        for (MemberAcceptedResponse memberAcceptedResponse : memberAcceptedResponseList) {
            if (startWorkMember.containsKey(memberAcceptedResponse.getMemberId())) {
                memberAcceptedResponse.updateHistoryInfo(startWorkMember.get(memberAcceptedResponse.getMemberId()));
            } else if (notWorkMember.containsKey(memberAcceptedResponse.getMemberId())) {
                memberAcceptedResponse.updateHistoryInfo(notWorkMember.get(memberAcceptedResponse.getMemberId()));
            }
        }

        return memberAcceptedResponseList;
    }

    // key: memberId  |  value: history  map 생성
    private Map<Long, History> createHistoryMap(Long jobId, Long workDateId, WorkStatus status) {
        return historyRepository.findHistoryAtStartWorkCheck(jobId, workDateId, status).stream()
                .collect(Collectors.toMap(history -> history.getMember().getId(), Function.identity()));
    }

    /**
     * 퇴근, 조퇴 조회
     */
    public HistoryAtFinishResponse findHistoryAtFinish(Long companyId, Long jobPostId, Long workDateId) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findById(workDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_DATE_NOT_FOUND));

        List<History> workHistoryList = historyRepository.findHistoryAtStartWorkCheck(jobPost.getId(), workDate.getId(), WorkStatus.START_WORK);
        List<History> notWorkHistoryList = historyRepository.findHistoryAtStartWorkCheck(jobPost.getId(), workDate.getId(), WorkStatus.NOT_WORK);

        List<MemberAcceptedResponse> workMemberResponse = workHistoryList.stream()
                .map(MemberAcceptedResponse::from)
                .collect(Collectors.toList());
        List<MemberAcceptedResponse> notWorkMemberResponse = notWorkHistoryList.stream()
                .map(MemberAcceptedResponse::from)
                .collect(Collectors.toList());

        return HistoryAtFinishResponse.builder()
                .workMemberResponse(workMemberResponse)
                .notWorkMemberResponse(notWorkMemberResponse)
                .build();
    }

    /**
     * 지급 내역서 확인
     */
    public PaymentStatementResponse findPaymentStatement(Long companyId, Long jobPostId, Long workDateId) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findById(workDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_DATE_NOT_FOUND));

        List<PaymentMemberInfo> paymentMemberInfoList = historyRepository.findPaymentStatementInfo(jobPost.getId(), workDate.getId()).stream()
                .map(PaymentMemberInfo::from)
                .collect(Collectors.toList());

        return PaymentStatementResponse.from(paymentMemberInfoList);
    }
}
