package jikgong.domain.apply.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import jikgong.domain.apply.dto.company.ApplyManageResponse;
import jikgong.domain.apply.dto.company.ApplyProcessRequest;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.history.service.HistoryService;
import jikgong.domain.jobpost.entity.jobpost.JobPost;
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

    private final HistoryService historyService;

    private final ApplyRepository applyRepository;
    private final MemberRepository memberRepository;
    private final JobPostRepository jobPostRepository;
    private final WorkDateRepository workDateRepository;

    /**
     * 인력 관리, 지원자 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ApplyManageResponse> findApplyHistoryCompany(Long companyId, Long jobPostId, ApplyStatus status, Pageable pageable) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findByIdAndMember(company.getId(), jobPostId)
            .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));

        Page<Apply> applyPage;
        if(status == null) {
            applyPage = applyRepository.findApplyForCompany(
                    company.getId(),
                    jobPost.getId(),
                    pageable
            );
        } else {
            applyPage = applyRepository.findApplyForCompanyByApplyStatus(
                    company.getId(),
                    jobPost.getId(),
                    status,
                    pageable
            );
        }

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
        WorkDate workDate = workDateRepository.findByIdWithLock(jobPost.getId(), request.getWorkDateId())
            .orElseThrow(() -> new JikgongException(ErrorCode.WORK_DATE_NOT_FOUND));

        // 회원들의 apply에 대한 Lock
        // 하루에 확정된 일자리는 하나임을 만족하기 위함
        applyRepository.findByMemberForLock(request.getMemberIdList(), workDate.getDate());

        // 대기 중인 요청인지 체크
        List<Apply> applyList = checkPendingApply(request, workDate, jobPost);

        List<Long> workerIdList = applyList.stream().map(apply -> apply.getMember().getId())
            .collect(Collectors.toList());

        // 수락인 경우
        if (request.getIsAccept()) {
            // 이미 다른 일자리에 승인된 노동자가 있는지 체크
            checkAcceptedWorker(workerIdList, workDate);

            // 승인 가능 시각 체크
            validateAcceptTime(workDate);

            // applyStatus 갱신
            int updatedCount = updateApplyStatus(ApplyStatus.ACCEPTED, applyList);
            
            // history 생성
            historyService.createHistoriesByAcceptedApplies(company.getId(), applyList);
            
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
    private void checkAcceptedWorker(List<Long> workerIdList, WorkDate workDate) {
        List<Apply> acceptedApply = applyRepository.findAcceptedMember(workerIdList, workDate.getDate());
        if (!acceptedApply.isEmpty()) {
            String acceptedWorkerNames = acceptedApply.stream()
                .map(worker -> worker.getMember().getWorkerInfo().getWorkerName())
                .collect(Collectors.joining(", "));
            throw new JikgongException(HttpStatus.BAD_REQUEST, "APPLY-004",
                acceptedWorkerNames + " 은 이미 확정된 일자리가 있습니다.");
        }
    }

    // 승인 가능 시각 체크
    private void validateAcceptTime(WorkDate workDate) {
        LocalTime startTime = workDate.getJobPost().getStartTime(); // 출역 시각
        LocalDate date = workDate.getDate(); // 출역 날짜
        LocalDateTime now = LocalDateTime.now();

        // 출역 시각 전 까지 수락 가능
        if (now.isAfter(LocalDateTime.of(date, startTime))) {
            throw new JikgongException(ErrorCode.APPLY_ACCEPT_NEED_TO_PAST);
        }
    }

    // 대기 중인 요청인지 체크
    private List<Apply> checkPendingApply(ApplyProcessRequest request, WorkDate workDate, JobPost jobPost) {
        List<Apply> applyList = applyRepository.findByIdList(request.getApplyIdList(), workDate.getId(),
            jobPost.getId());

        // 조회된 apply 내역이 없을 경우
        if (applyList.isEmpty()) {
            throw new JikgongException(ErrorCode.APPLY_NOT_FOUND);
        }

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
