package jikgong.domain.history.service;

import jikgong.domain.apply.dtos.company.ApplyResponseForCompany;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.history.dtos.HistoryAtFinishResponse;
import jikgong.domain.history.dtos.HistoryFinishSaveRequest;
import jikgong.domain.history.dtos.HistoryStartSaveRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    // todo: 나중에 O(1) 이 되도록 리펙토링
    // 출근, 결근 저장
    public int saveHistoryAtStart(Long memberId, HistoryStartSaveRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findById(request.getJobPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findById(request.getWorkDateId())
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_DATE_NOT_FOUND));

        // 지원한 데이터에 대한 처리인지 체크
        List<Apply> applyList = applyRepository.checkApplyBeforeSaveHistory(jobPost.getId(), workDate.getId(), request.getStartWorkMemberIdList(), request.getNotWorkMemberIdList(), ApplyStatus.ACCEPTED);
        if (applyList.size() != request.getStartWorkMemberIdList().size() + request.getNotWorkMemberIdList().size()) {
            throw new CustomException(ErrorCode.HISTORY_NOT_FOUND_APPLY);
        }

        // 기존에 history 데이터가 있다면 제거
        int deleteCount = historyRepository.deleteByWorkDateAndAndMember(request.getStartWorkMemberIdList(), request.getNotWorkMemberIdList(), request.getWorkDateId());
        log.info("기존 history 제거 개수: " + deleteCount);

        List<History> saveHistoryList = new ArrayList<>();

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

    public int updateHistoryAtFinish(Long memberId, HistoryFinishSaveRequest request) {
        Member member = memberRepository.findById(memberId)
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

    public List<MemberAcceptedResponse> findApplyWithHistoryAtStart(Long memberId, Long jobPostId, Long workDateId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findById(workDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_DATE_NOT_FOUND));

        // 출석한 member, 결근한 member ID를 저장 하는 Set 생성
        Set<Long> startWorkMember = new HashSet<>();
        Set<Long> notWorkMember = new HashSet<>();
        List<History> workHistoryList = historyRepository.findHistoryAtStartWorkCheck(jobPost.getId(), workDate.getId(), WorkStatus.START_WORK);
        List<History> NotworkHistoryList = historyRepository.findHistoryAtStartWorkCheck(jobPost.getId(), workDate.getId(), WorkStatus.NOT_WORK);
        for (History history : workHistoryList) {
            startWorkMember.add(history.getMember().getId());
        }
        for (History history : NotworkHistoryList) {
            notWorkMember.add(history.getMember().getId());
        }

        List<Apply> applyList = applyRepository.findApplyAtStartWorkCheck(member.getId(), jobPost.getId(), workDate.getId(), ApplyStatus.ACCEPTED);

        List<MemberAcceptedResponse> memberAcceptedResponseList = applyList.stream()
                .map(MemberAcceptedResponse::from)
                .collect(Collectors.toList());

        // 현재 출근, 결근, 출근 전 status 값 세팅
        for (MemberAcceptedResponse memberAcceptedResponse : memberAcceptedResponseList) {
            if (startWorkMember.contains(memberAcceptedResponse.getMemberId())) {
                memberAcceptedResponse.setWorkStatus(WorkStatus.START_WORK);
            } else if (notWorkMember.contains(memberAcceptedResponse.getMemberId())) {
                memberAcceptedResponse.setWorkStatus(WorkStatus.NOT_WORK);
            } else {
                memberAcceptedResponse.setWorkStatus(null);
            }
        }

        return memberAcceptedResponseList;
    }

    public HistoryAtFinishResponse findHistoryAtFinish(Long memberId, Long jobPostId, Long workDateId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findById(workDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_DATE_NOT_FOUND));

        // 퇴근한 member, 조퇴한 member ID를 저장 하는 Set 생성
        List<History> workHistoryList = historyRepository.findHistoryAtFinishWorkCheck(jobPost.getId(), workDate.getId(), WorkStatus.NOT_WORK);
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
}
