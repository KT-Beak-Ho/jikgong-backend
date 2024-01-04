package jikgong.domain.apply.service;

import jikgong.domain.apply.dtos.ApplyRequest;
import jikgong.domain.apply.dtos.ApplyResponseForCompany;
import jikgong.domain.apply.dtos.ApplyResponseForWorker;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.Status;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.repository.JobPostRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplyService {
    private final ApplyRepository applyRepository;
    private final MemberRepository memberRepository;
    private final JobPostRepository jobPostRepository;
    public Long saveApply(Long memberId, ApplyRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        JobPost jobPost = jobPostRepository.findById(request.getJobPostId())
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

    public List<ApplyResponseForWorker> findApplyHistoryWorker(Long memberId, Status status) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<ApplyResponseForWorker> applyResponseForWorkerList = applyRepository.findByMemberIdAndStatus(member.getId(), status).stream()
                .map(ApplyResponseForWorker::from)
                .collect(Collectors.toList());

        return applyResponseForWorkerList;
    }

    public List<ApplyResponseForCompany> findApplyHistoryCompany(Long memberId, Long jobPostId, Status status) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        List<ApplyResponseForCompany> applyResponseForCompanyList = applyRepository.findByJobPostIdAndMemberIdAndStatus(member.getId(), jobPost.getId(), status).stream()
                .map(ApplyResponseForCompany::from)
                .collect(Collectors.toList());

        return applyResponseForCompanyList;
    }
}
