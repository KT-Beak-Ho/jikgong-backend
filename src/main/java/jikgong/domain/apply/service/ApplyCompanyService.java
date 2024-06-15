package jikgong.domain.apply.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import jikgong.domain.apply.dto.company.ApplyManageResponse;
import jikgong.domain.apply.dto.company.ApplyProcessRequest;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.jobpost.entity.JobPost;
import jikgong.domain.jobpost.repository.JobPostRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.workdate.entity.WorkDate;
import jikgong.domain.workdate.repository.WorkDateRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplyCompanyService {

    private final ApplyRepository applyRepository;
    private final MemberRepository memberRepository;
    private final JobPostRepository jobPostRepository;
    private final WorkDateRepository workDateRepository;

    /**
     * 인력 관리, 지원자 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ApplyManageResponse> findPendingApplyHistoryCompany(Long companyId, Long jobPostId, Long workDateId,
        Pageable pageable) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findByIdAndMember(company.getId(), jobPostId)
            .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findById(workDateId)
            .orElseThrow(() -> new JikgongException(ErrorCode.WORK_DATE_NOT_FOUND));

        Page<Apply> applyPage = applyRepository.findApplyForCompanyByApplyStatus(company.getId(), jobPost.getId(),
            workDate.getId(), ApplyStatus.PENDING, pageable);

        List<ApplyManageResponse> applyManageResponseList = applyPage.getContent().stream()
            .map(ApplyManageResponse::from)
            .collect(Collectors.toList());

        return new PageImpl<>(applyManageResponseList, pageable, applyPage.getTotalElements());
    }

    /**
     * 인력 관리 확정 인부 조회
     */
    @Transactional(readOnly = true)
    public Page<ApplyManageResponse> findAcceptedHistoryCompany(Long companyId, Long jobPostId, Long workDateId,
        Pageable pageable) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findByIdAndMember(company.getId(), jobPostId)
            .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));
        WorkDate workDate = workDateRepository.findById(workDateId)
            .orElseThrow(() -> new JikgongException(ErrorCode.WORK_DATE_NOT_FOUND));

        Page<Apply> applyPage = applyRepository.findApplyForCompanyByApplyStatus(company.getId(), jobPost.getId(),
            workDate.getId(), ApplyStatus.ACCEPTED, pageable);

        List<ApplyManageResponse> applyManageResponseList = applyPage.getContent().stream()
            .map(ApplyManageResponse::from)
            .collect(Collectors.toList());

        return new PageImpl<>(applyManageResponseList, pageable, applyPage.getTotalElements());
    }

    /**
     * 인력 관리, 인부 지원 요청 처리 (수락, 거절), apply status 갱신 후 같은 날 다른 공고에 대한 지원 취소
     */
    public void processApply(Long companyId, ApplyProcessRequest request) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findByIdAndMember(company.getId(), request.getJobPostId())
            .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));

        // workDate에 대한 Lock
        WorkDate workDate = workDateRepository.findByIdWithLock(request.getWorkDateId())
            .orElseThrow(() -> new JikgongException(ErrorCode.WORK_DATE_NOT_FOUND));

        // 대기 중인 요청인지 체크
        List<Apply> applyList = checkPendingApply(request, workDate, jobPost);

        // 수락인 경우
        if (request.getIsAccept()) {
            // 이미 다른 일자리에 승인된 노동자가 있는지 체크
            checkAcceptedWorker(applyList, workDate);

            // 모집 인원 및 날짜 체크
            checkRegisteredNumAndDate(workDate);

            // applyStatus 갱신
            int updatedCount = updateApplyStatus(ApplyStatus.ACCEPTED, applyList);

            // 모집된 인원 갱신
            workDate.plusRegisteredNum(updatedCount);

            // 회원들의 같은 날 다른 공고에 지원 했던 대기중인 요청 취소
            cancelAnotherApply(applyList, workDate);
        }

        // 거절인 경우
        if (!request.getIsAccept()) {
            // applyStatus 갱신
            updateApplyStatus(ApplyStatus.REJECTED, applyList);
        }
    }

    // applyStatus 갱신
    private int updateApplyStatus(ApplyStatus status, List<Apply> applyList) {
        List<Long> updateApplyIdList = applyList.stream().map(Apply::getId).collect(Collectors.toList());
        return applyRepository.updateApplyStatus(updateApplyIdList, status, LocalDateTime.now());
    }

    // 이미 다른 일자리에 승인된 노동자가 있는지 체크
    private void checkAcceptedWorker(List<Apply> applyList, WorkDate workDate) {
        List<Long> workerIdList = applyList.stream().map(apply -> apply.getMember().getId())
            .collect(Collectors.toList());
        List<Apply> acceptedApply = applyRepository.findAcceptedMember(workerIdList, workDate.getDate());
        if (!acceptedApply.isEmpty()) {
            String acceptedWorkerNames = acceptedApply.stream()
                .map(worker -> worker.getMember().getWorkerInfo().getWorkerName())
                .collect(Collectors.joining(", "));
            throw new JikgongException(HttpStatus.BAD_REQUEST, acceptedWorkerNames + " 은 이미 확정된 일자리가 있습니다.");
        }
    }

    // 날짜 체크
    private void checkRegisteredNumAndDate(WorkDate workDate) {
        // 출역일 2일 전까지 수락 가능
        // 5일이 출역일이면
        // 3일 수락 가능  |  4일 수락 불가능
        if (LocalDate.now().isAfter(workDate.getDate().minusDays(2))) {
            throw new JikgongException(ErrorCode.WORK_DATE_NEED_TO_FUTURE);
        }
    }

    // 대기 중인 요청인지 체크
    private List<Apply> checkPendingApply(ApplyProcessRequest request, WorkDate workDate, JobPost jobPost) {
        List<Apply> applyList = applyRepository.findByIdList(request.getApplyIdList(), workDate.getId(),
            jobPost.getId());
        for (Apply apply : applyList) {
            if (apply.getStatus() != ApplyStatus.PENDING) {
                throw new JikgongException(ErrorCode.APPLY_NEED_TO_PENDING);
            }
        }
        return applyList;
    }

    // 같은 날 다른 공고에 지원 했던 대기중인 요청 취소
    private void cancelAnotherApply(List<Apply> applyList, WorkDate workDate) {
        List<Long> memberIdList = applyList.stream().map(apply -> apply.getMember().getId())
            .collect(Collectors.toList());
        List<Long> cancelApplyIdList = applyRepository.deleteOtherApplyOnDate(memberIdList, workDate.getDate());
        int canceledCount = applyRepository.updateApplyStatus(cancelApplyIdList, ApplyStatus.CANCELED,
            LocalDateTime.now());
        log.info("취소된 요청 횟수: " + canceledCount);
    }
}
