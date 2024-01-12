package jikgong.domain.history.service;

import jdk.jshell.Snippet;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.history.dtos.CountHistory;
import jikgong.domain.history.dtos.HistorySaveRequest;
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
import java.util.List;
import java.util.Optional;
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
    public Long saveHistory(Long memberId, HistorySaveRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Member targetMember = memberRepository.findById(request.getTargetMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findById(request.getJobPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        // 실제 신청 했는지 체크
        Optional<Apply> apply = applyRepository.checkAppliedAndAuthor(member.getId(), targetMember.getId(), jobPost.getId());
        if (apply.isEmpty()) {
            throw new CustomException(ErrorCode.HISTORY_NOT_FOUND_APPLY);
        }
        // 승인된 노동자 인지 체크
        if (apply.get().getStatus() != ApplyStatus.ACCEPTED) {
            throw new CustomException(ErrorCode.APPLY_NOT_ACCEPTED);
        }

        // 유효한 날짜 인지 체크
        Optional<WorkDate> workDate = workDateRepository.findByWorkDateAndJobPost(jobPost.getId(), request.getWorkDate());
        if (workDate.isEmpty()) {
            throw new CustomException(ErrorCode.WORK_DATE_NOT_FOUND);
        }

        // 중복 체크
        Optional<History> findHistory = historyRepository.findExistHistory(targetMember.getId(), jobPost.getId(), request.getWorkDate(), request.getIsWork());
        if (findHistory.isPresent()) {
            throw new CustomException(ErrorCode.HISTORY_ALREADY_EXIST);
        }

        // 출근 요청 시 결근 데이터가 있다면 제거
        // 결근 요청 시 출근 데이터가 있다면 제거
        Optional<History> oppositeHistory = historyRepository.findExistHistory(targetMember.getId(), jobPost.getId(), request.getWorkDate(), !request.getIsWork());
        if (oppositeHistory.isPresent()) {
            log.info("기존의 isWork:" + !request.getIsWork() + " 데이터 삭제");
            historyRepository.delete(oppositeHistory.get());
        }

        History history = History.createEntity(request, targetMember, jobPost);

        return historyRepository.save(history).getId();
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

        CountHistory countHistory = findCountHistory(member.getId(), jobPost.getId(), workDate);
        PageImpl<MemberAcceptedResponse> memberAcceptedResponsePage = new PageImpl<>(memberAcceptedResponseList, pageable, historyPage.getTotalElements());

        return JobPostManageWorkerResponse.builder()
                .countHistory(countHistory)
                .memberAcceptedResponsePage(memberAcceptedResponsePage)
                .build();
    }

    // 인력 관리: 전체, 출근, 결근 인원수 조회
    public CountHistory findCountHistory(Long memberId, Long jobPostId, LocalDate workDate) {
        Long allCount = applyRepository.findCountApply(memberId, jobPostId);
        Long countWork = historyRepository.findCountWorkOrNotWork(memberId, jobPostId, workDate, true);
        Long countNotWork = historyRepository.findCountWorkOrNotWork(memberId, jobPostId, workDate, false);
        return new CountHistory(allCount, countWork, countNotWork);
    }
}
