package jikgong.domain.offer.service;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.offer.dtos.company.*;
import jikgong.domain.offer.entity.OfferStatus;
import jikgong.domain.offer.repository.OfferRepository;
import jikgong.domain.offer.entity.Offer;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.repository.JobPostRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.notification.entity.NotificationType;
import jikgong.domain.offerWorkDate.entity.OfferWorkDate;
import jikgong.domain.offerWorkDate.entity.OfferWorkDateStatus;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OfferCompanyService {
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final OfferRepository offerRepository;
    private final ApplyRepository applyRepository;
    private final ApplicationEventPublisher publisher;
    private final JobPostRepository jobPostRepository;
    private final ResumeRepository resumeRepository;
    private final WorkDateRepository workDateRepository;
    private final OfferWorkDateRepository offerWorkDateRepository;

    // todo: 기획 변경 요청 해볼까?

    /**
     * 일자리 제안
     * 한 노동자에게 A공고의 여러 날짜, B공고의 여러 날짜 제안 가능
     */
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
        // key: jobPostId  |  value: WorkDate
        Map<Long, List<WorkDate>> workDateMap = getWorkDateMap(jobPostIdList, workDateIdList);

        List<Offer> offerList = new ArrayList<>();
        List<OfferWorkDate> offerWorkDateList = new ArrayList<>();

        for (OfferJobPostRequest offerJobPostRequest : request.getOfferJobPostRequest()) {
            JobPost jobPost = jobPostRepository.findById(offerJobPostRequest.getJobPostId())
                    .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

            List<LocalDate> dateList = new ArrayList<>();

            // jobPostId 에 해당하는 workDateList 조회
            List<WorkDate> workDateEntityList = workDateMap.get(jobPost.getId());

            // 예외 체크 및 dateList 에 date 추가
            validationAndAddDateList(offerJobPostRequest, workDateEntityList, dateList);

            // offer 엔티티 생성
            // offerWorkDate 엔티티 리스트 생성
            Offer offer = Offer.createEntity(company, worker, jobPost);
            offerList.add(offer);
            offerWorkDateList.addAll(OfferWorkDate.createEntityList(offer, workDateEntityList));
            publisher.publishEvent(new NotificationEvent(company.getCompanyInfo().getCompanyName(), dateList, offerJobPostRequest.getJobPostId(), NotificationType.OFFER, worker.getId()));
        }

        offerRepository.saveAll(offerList);
        offerWorkDateRepository.saveAll(offerWorkDateList);
    }

    // 예외 체크 및 dateList 에 date 추가
    private static void validationAndAddDateList(OfferJobPostRequest offerJobPostRequest, List<WorkDate> workDateEntityList, List<LocalDate> dateList) {
        // 캐시 map 에서 조회한 workDateList 와 요청한 workDateIdList 와 크기가 다를 때
        if (workDateEntityList.size() != offerJobPostRequest.getWorkDateIdList().size()) {
            throw new CustomException(ErrorCode.WORK_DATE_NOT_MATCH);
        }

        // 이미 지난 공고 인지 체크
        for (WorkDate workDate : workDateEntityList) {
            if (LocalDate.now().isAfter(workDate.getDate())) {
                throw new CustomException(ErrorCode.WORK_DATE_NEED_TO_FUTURE);
            }
            if (LocalDate.now().isEqual(workDate.getDate()) && LocalTime.now().isAfter(workDate.getJobPost().getStartTime())) {
                throw new CustomException(ErrorCode.WORK_DATE_NEED_TO_FUTURE);
            }
            dateList.add(workDate.getDate());
        }
    }

    // jpa 1차 캐싱을 위한 조회 & workDate 임시 캐시 map 생성
    // key: jobPostId  |  value: WorkDate
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

    /**
     * 제안 시 노동자 상세 정보 조회
     */
    public WorkerInfoResponse findWorkerInfo(Long companyId, Long resumeId, LocalDate selectMonth) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Resume resume = resumeRepository.findByIdWithMember(resumeId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        // 이미 확정된 apply 월별 조회
        List<Apply> findCantWorkDate = applyRepository.findCantWorkDate(
                resume.getMember().getId(),
                TimeTransfer.getFirstDayOfMonth(selectMonth),
                TimeTransfer.getLastDayOfMonth(selectMonth));

        return WorkerInfoResponse.from(resume, findCantWorkDate);
    }

    /**
     * 제안 시 프로젝트 선택
     * 선택 후 제안 가능한 공고 조회
     */
    public SelectOfferJobPostResponse findAvailableJobPosts(Long companyId, Long workerId, Long projectId) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));


        List<LocalDate> cantWorkDate = applyRepository.findAllCantWorkDate(worker.getId()).stream()
                .map(apply -> apply.getWorkDate().getDate())
                .collect(Collectors.toList());
        Set<LocalDate> cantWorkDateSet = new HashSet<>(cantWorkDate);

        List<JobPost> jobPostList = jobPostRepository.findByProject(project.getId());

        return SelectOfferJobPostResponse.from(jobPostList, worker, cantWorkDateSet);
    }

    /**
     * 제안 기록 조회
     */
    public Page<OfferHistoryResponse> findOfferHistory(Long companyId, OfferStatus offerStatus, Pageable pageable) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Page<Offer> offerHistoryPage = offerRepository.findOfferHistory(companyId, offerStatus, pageable);
        List<OfferHistoryResponse> offerHistoryResponseList = offerHistoryPage.getContent().stream()
                .map(OfferHistoryResponse::from)
                .collect(Collectors.toList());
        return new PageImpl<>(offerHistoryResponseList, pageable, offerHistoryPage.getTotalElements());
    }

    /**
     * 제안 취소
     */
    public void cancelOffer(Long companyId, Long offerId) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new CustomException(ErrorCode.OFFER_NOT_FOUND));

        // 제안 취소 (status 값 변경)
        offer.cancelOffer();

        int canceledOfferWorkDate = offerWorkDateRepository.cancelOffer(offer.getId(), OfferWorkDateStatus.OFFER_CANCELED);
        log.info("취소된 offerWorkDate 수: " + canceledOfferWorkDate);
    }
}
