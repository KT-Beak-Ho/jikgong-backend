package jikgong.domain.history.service;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.history.dtos.HistorySaveRequest;
import jikgong.domain.history.entity.History;
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

import java.util.List;
import java.util.Optional;

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
}
