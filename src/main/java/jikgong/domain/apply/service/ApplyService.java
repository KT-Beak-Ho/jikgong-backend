package jikgong.domain.apply.service;

import jikgong.domain.apply.dtos.AcceptedMemberRequest;
import jikgong.domain.apply.dtos.ApplyPendingResponseForCompany;
import jikgong.domain.apply.dtos.ApplyProcessRequest;
import jikgong.domain.apply.dtos.ApplyResponseForWorker;
import jikgong.domain.history.dtos.CountHistory;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.history.entity.History;
import jikgong.domain.history.repository.HistoryRepository;
import jikgong.domain.history.service.HistoryService;
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplyService {
    private final ApplyRepository applyRepository;
    private final MemberRepository memberRepository;
    private final JobPostRepository jobPostRepository;
    private final HistoryRepository historyRepository;
    private final WorkDateRepository workDateRepository;
    private final HistoryService historyService;
    public Long saveApply(Long memberId, Long jobPostId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 임시 저장이 아닌 JobPost 조회
        JobPost jobPost = jobPostRepository.findJobPostByIdAndTemporary(member.getId(), jobPostId, false)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        // 중복 신청 조회
        Optional<Apply> findApply = applyRepository.findByMemberIdAndJobPostId(member.getId(), jobPost.getId());
        if (findApply.isPresent()) {
            throw new CustomException(ErrorCode.APPLY_ALREADY_EXIST);
        }

        // 모집 기한 체크
        if (jobPost.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.JOB_POST_EXPIRED);
        }

        Apply apply = Apply.builder()
                .member(member)
                .jobPost(jobPost)
                .member(member)
                .build();

        return applyRepository.save(apply).getId();
    }

    // 요청한 내역 조회 (노동자)
    public Page<ApplyResponseForWorker> findApplyHistoryWorker(Long memberId, ApplyStatus status, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Page<Apply> applyPage = applyRepository.findByMemberIdAndStatus(member.getId(), status, pageable);

        List<ApplyResponseForWorker> applyResponseForWorkerList = applyPage.getContent().stream()
                .map(ApplyResponseForWorker::from)
                .collect(Collectors.toList());

        return new PageImpl<>(applyResponseForWorkerList, pageable, applyPage.getTotalElements());
    }

    // 인력 관리: 인부 신청 현황 조회 (회사)
    public Page<ApplyPendingResponseForCompany> findPendingApplyHistoryCompany(Long memberId, Long jobPostId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        Page<Apply> applyPage = applyRepository.findByJobPostIdAndMemberIdAndStatus(member.getId(), jobPost.getId(), ApplyStatus.PENDING, pageable);

        List<ApplyPendingResponseForCompany> applyResponseForCompanyList = applyPage.getContent().stream()
                .map(ApplyPendingResponseForCompany::from)
                .collect(Collectors.toList());

        return new PageImpl<>(applyResponseForCompanyList, pageable, applyPage.getTotalElements());
    }

    // 인력 관리: 확정 인부 조회 (회사)
    public JobPostManageWorkerResponse findAcceptedHistoryCompany(Long memberId, Long jobPostId, LocalDate workDate, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        // 유효한 날짜 인지 체크
        Optional<WorkDate> findWorkDate = workDateRepository.findByWorkDateAndJobPost(jobPost.getId(), workDate);
        if (findWorkDate.isEmpty()) {
            throw new CustomException(ErrorCode.WORK_DATE_NOT_FOUND);
        }

        // 출석한 member, 결근한 member ID를 저장 하는 Set 생성
        Set<Long> workedMemberIds = new HashSet<>();
        Set<Long> NotWorkedMemberIds = new HashSet<>();
        List<History> workHistoryList = historyRepository.findHistoryByJobPostIdAndWork(jobPost.getId(), workDate, true);
        List<History> NotworkHistoryList = historyRepository.findHistoryByJobPostIdAndWork(jobPost.getId(), workDate, false);
        for (History history : workHistoryList) {
            workedMemberIds.add(history.getMember().getId());
        }
        for (History history : NotworkHistoryList) {
            NotWorkedMemberIds.add(history.getMember().getId());
        }

        Page<Apply> applyPage = applyRepository.findByJobPostIdAndMemberIdAndStatus(member.getId(), jobPost.getId(), ApplyStatus.ACCEPTED, pageable);

        List<MemberAcceptedResponse> memberAcceptedResponseList = applyPage.getContent().stream()
                .map(MemberAcceptedResponse::fromApply)
                .collect(Collectors.toList());

        // 현재 출근, 결근, 출근 전 status 값 세팅
        for (MemberAcceptedResponse memberAcceptedResponse : memberAcceptedResponseList) {
            if (workedMemberIds.contains(memberAcceptedResponse.getMemberId())) {
                memberAcceptedResponse.setWorkStatus(WorkStatus.WORK);
            }
            else if (NotWorkedMemberIds.contains(memberAcceptedResponse.getMemberId())) {
                memberAcceptedResponse.setWorkStatus(WorkStatus.NOT_WORK);
            }
            else {
                memberAcceptedResponse.setWorkStatus(null);
            }
        }
        PageImpl<MemberAcceptedResponse> memberAcceptedResponsePage = new PageImpl<>(memberAcceptedResponseList, pageable, applyPage.getTotalElements());

        CountHistory countHistory = historyService.findCountHistory(member.getId(), jobPost.getId(), workDate);

        return JobPostManageWorkerResponse.builder()
                .countHistory(countHistory)
                .memberAcceptedResponsePage(memberAcceptedResponsePage)
                .build();
    }

    // 인력 관리: 인부 요청 처리
    public void processApply(Long memberId, ApplyProcessRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 실제 신청한 인부가 맞는지 체크
        List<Long> applyMemberIds = applyRepository.findMemberIdListByMemberIdAndJobPostId(member.getId(), request.getJobPostId());
        Set<Long> memberIdSet = new HashSet<>(applyMemberIds);
        for (Long targetMemberId : request.getTargetMemberIdList()) {
            if (!memberIdSet.contains(targetMemberId)) {
                throw new CustomException(ErrorCode.APPLY_PROCESS_NOT_FOUND_MEMBER);
            }
        }

        // applyStatus 갱신
        ApplyStatus applyStatus = request.getIsAccept() ? ApplyStatus.ACCEPTED : ApplyStatus.REJECTED;
        applyRepository.updateApplyStatus(request.getTargetMemberIdList(), applyStatus);
    }
}
