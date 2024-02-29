package jikgong.domain.offer.service;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.offer.dtos.*;
import jikgong.domain.offer.entity.OfferStatus;
import jikgong.domain.offer.repository.OfferRepository;
import jikgong.domain.offer.entity.Offer;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.repository.JobPostRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.notification.entity.NotificationType;
import jikgong.domain.offerWorkDate.entity.OfferWorkDate;
import jikgong.domain.offerWorkDate.repository.OfferWorkDateRepository;
import jikgong.domain.project.entity.Project;
import jikgong.domain.project.repository.ProjectRepository;
import jikgong.domain.resume.entity.Resume;
import jikgong.domain.resume.repository.ResumeRepository;
import jikgong.domain.workDate.entity.WorkDate;
import jikgong.domain.workDate.repository.WorkDateRepository;
import jikgong.global.event.dtos.NotificationEvent;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import jikgong.global.utils.TimeTransfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OfferService {
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final OfferRepository offerRepository;
    private final ApplyRepository applyRepository;
    private final ApplicationEventPublisher publisher;
    private final JobPostRepository jobPostRepository;
    private final ResumeRepository resumeRepository;
    private final WorkDateRepository workDateRepository;
    private final OfferWorkDateRepository offerWorkDateRepository;

    public void offerJobPost(Long companyId, OfferRequest request) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Resume resume = resumeRepository.findByIdWithMember(request.getResumeId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));
        Member worker = resume.getMember();

        List<Long> jobPostIdList = new ArrayList<>();
        List<Long> workDateIdList = new ArrayList<>();

        for (OfferJobPostRequest offerJobPostRequest : request.getOfferJobPostRequest()) {
            jobPostIdList.add(offerJobPostRequest.getJobPostId());
            workDateIdList.addAll(offerJobPostRequest.getWorkDateIdList());
        }

        // jpa 1차 캐싱을 위한 조회 & workDate 임시 캐시 map 생성
        Map<Long, List<WorkDate>> workDateMap = getWorkDateMap(jobPostIdList, workDateIdList);

        List<Offer> offerList = new ArrayList<>();
        List<OfferWorkDate> offerWorkDateList = new ArrayList<>();

        for (OfferJobPostRequest offerJobPostRequest : request.getOfferJobPostRequest()) {
            JobPost jobPost = jobPostRepository.findById(offerJobPostRequest.getJobPostId())
                    .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

            List<LocalDate> LocalDateList = new ArrayList<>();

            List<WorkDate> workDateEntityList = workDateMap.get(jobPost.getId());
            if (workDateEntityList.size() != offerJobPostRequest.getWorkDateIdList().size()) {
                throw new CustomException(ErrorCode.WORK_DATE_NOT_MATCH);
            }
            for (WorkDate workDate : workDateEntityList) {
                if (!LocalDate.now().isBefore(workDate.getWorkDate())) {
                    throw new CustomException(ErrorCode.WORK_DATE_NEED_TO_FUTURE);
                }
                LocalDateList.add(workDate.getWorkDate());
            }

            // offer 엔티티 생성
            // offerWorkDate 엔티티 리스트 생성
            Offer offer = Offer.createEntity(company, worker);
            offerList.add(offer);
            offerWorkDateList.addAll(OfferWorkDate.createEntityList(offer, workDateEntityList));
            publisher.publishEvent(new NotificationEvent(company.getCompanyInfo().getCompanyName(), LocalDateList, offerJobPostRequest.getJobPostId(), NotificationType.OFFER, worker.getId()));
        }

        offerRepository.saveAll(offerList);
        offerWorkDateRepository.saveAll(offerWorkDateList);
    }

    private Map<Long, List<WorkDate>> getWorkDateMap(List<Long> jobPostIdList, List<Long> workDateIdList) {
        jobPostRepository.findByIdList(jobPostIdList);
        List<WorkDate> workDateList = workDateRepository.findByIdList(workDateIdList);

        Map<Long, List<WorkDate>> workDateMap = new HashMap<>();
        for (WorkDate workDate : workDateList) {
            Long jobPostId = workDate.getJobPost().getId();
            // Key가 jobPostId인 List 가져오거나, 새로 생성
            List<WorkDate> innerList = workDateMap.computeIfAbsent(jobPostId, k -> new ArrayList<>());
            // innerMap에 workDateId와 workDate를 추가
            innerList.add(workDate);
        }
        return workDateMap;
    }

    public WorkerInfoResponse findWorkerInfo(Long companyId, Long resumeId, LocalDate selectMonth) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Resume resume = resumeRepository.findByIdWithMember(resumeId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        List<Apply> findCantWorkDate = applyRepository.findCantWorkDate(
                resume.getMember().getId(),
                TimeTransfer.getFirstDayOfMonth(selectMonth),
                TimeTransfer.getLastDayOfMonth(selectMonth));

        return WorkerInfoResponse.from(resume, findCantWorkDate);
    }

    public SelectOfferJobPostResponse findAvailableJobPosts(Long companyId, Long workerId, Long projectId) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));


        List<LocalDate> cantWorkDate = applyRepository.findAllCantWorkDate(worker.getId()).stream()
                .map(apply -> apply.getWorkDate().getWorkDate())
                .collect(Collectors.toList());
        Set<LocalDate> cantWorkDateSet = new HashSet<>(cantWorkDate);

        List<JobPost> jobPostList = jobPostRepository.findByProject(project.getId());

        return SelectOfferJobPostResponse.from(jobPostList, worker, cantWorkDateSet);
    }

    public Page<OfferHistoryResponse> findOfferHistory(Long companyId, OfferStatus offerStatus, Pageable pageable) {
        Page<Offer> offerHistoryPage = offerRepository.findOfferHistory(companyId, offerStatus, pageable);
        List<OfferHistoryResponse> offerHistoryResponseList = offerHistoryPage.getContent().stream()
                .map(OfferHistoryResponse::from)
                .collect(Collectors.toList());
        return new PageImpl<>(offerHistoryResponseList, pageable, offerHistoryPage.getTotalElements());
    }
}
