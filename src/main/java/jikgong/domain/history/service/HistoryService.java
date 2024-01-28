package jikgong.domain.history.service;

import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.history.dtos.CountHistoryResponse;
import jikgong.domain.history.dtos.HistoryFinishSaveRequest;
import jikgong.domain.history.dtos.HistoryStartSaveRequest;
import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.history.repository.HistoryRepository;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

        // 기존에 history 데이터가 있다면 제거
        historyRepository.deleteByWorkDateAndAndMember(request.getStartWorkList(), request.getNotWorkList(), request.getWorkDateId());

        List<History> saveHistoryList = new ArrayList<>();

        // 출근 history
        for (Long targetMemberId : request.getStartWorkList()) {
            Member targetMember = memberRepository.findById(targetMemberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
            saveHistoryList.add(History.createEntity(WorkStatus.START_WORK, targetMember, workDate));
        }

        // 결근 history
        for (Long targetMemberId : request.getNotWorkList()) {
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
        int updateFinishWork = historyRepository.updateHistoryByIdList(request.getFinishWorkList(), WorkStatus.FINISH_WORK);
        log.info("퇴근 처리된 데이터: " + updateFinishWork);

        // 조퇴
        int updateEarlyLeave = historyRepository.updateHistoryByIdList(request.getFinishWorkList(), WorkStatus.EARLY_LEAVE);
        log.info("조퇴 처리된 데이터: " + updateFinishWork);

        // 요청한 데이터와 업데이트한 데이터 비교
        if (updateFinishWork != request.getFinishWorkList().size() && updateEarlyLeave != request.getEarlyLeaveList().size()) {
            throw new CustomException(ErrorCode.HISTORY_UPDATE_FAIL);
        }

        return updateFinishWork + updateEarlyLeave;
    }

    public JobPostManageWorkerResponse findHistoryMembers(Long memberId, Long jobPostId, LocalDate workDate, Boolean isWork, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        Page<History> historyPage = historyRepository.findWorkHistory(member.getId(), jobPost.getId(), workDate, isWork, pageable);

        List<MemberAcceptedResponse> memberAcceptedResponseList = historyPage.getContent().stream()
                .map(MemberAcceptedResponse::fromHistory)
                .collect(Collectors.toList());

        CountHistoryResponse countHistory = findCountHistory(member.getId(), jobPost.getId(), workDate);
        PageImpl<MemberAcceptedResponse> memberAcceptedResponsePage = new PageImpl<>(memberAcceptedResponseList, pageable, historyPage.getTotalElements());

        return JobPostManageWorkerResponse.builder()
                .countHistory(countHistory)
                .memberAcceptedResponsePage(memberAcceptedResponsePage)
                .build();
    }

    // 인력 관리: 전체, 출근, 결근 인원수 조회
    public CountHistoryResponse findCountHistory(Long memberId, Long jobPostId, LocalDate workDate) {
        Long allCount = applyRepository.findCountApply(memberId, jobPostId);
        Long countWork = historyRepository.findCountWorkOrNotWork(memberId, jobPostId, workDate, true);
        Long countNotWork = historyRepository.findCountWorkOrNotWork(memberId, jobPostId, workDate, false);
        return new CountHistoryResponse(allCount, countWork, countNotWork);
    }
}
