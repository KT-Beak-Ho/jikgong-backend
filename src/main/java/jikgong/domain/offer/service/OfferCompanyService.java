package jikgong.domain.offer.service;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.offer.dto.company.*;
import jikgong.domain.offer.entity.OfferStatus;
import jikgong.domain.offer.repository.OfferRepository;
import jikgong.domain.offer.entity.Offer;
import jikgong.domain.jobpost.entity.JobPost;
import jikgong.domain.jobpost.repository.JobPostRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.notification.entity.NotificationType;
import jikgong.domain.offerworkdate.entity.OfferWorkDate;
import jikgong.domain.offerworkdate.entity.OfferWorkDateStatus;
import jikgong.domain.offerworkdate.repository.OfferWorkDateRepository;
import jikgong.domain.resume.entity.Resume;
import jikgong.domain.resume.repository.ResumeRepository;
import jikgong.domain.workdate.entity.WorkDate;
import jikgong.domain.workdate.repository.WorkDateRepository;
import jikgong.global.event.dto.NotificationEvent;
import jikgong.global.exception.JikgongException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OfferCompanyService {

    private final MemberRepository memberRepository;
    private final ApplyRepository applyRepository;
    private final OfferRepository offerRepository;
    private final ApplicationEventPublisher publisher;
    private final JobPostRepository jobPostRepository;
    private final ResumeRepository resumeRepository;
    private final WorkDateRepository workDateRepository;
    private final OfferWorkDateRepository offerWorkDateRepository;

    /**
     * 일자리 제안
     * 한 노동자에게 A공고의 여러 날짜, B공고의 여러 날짜 제안 가능
     * 제안을 하며 status가 OFFERED인 Apply 데이터 생성
     * notifiaction event 발행
     */
    public void offerJobPost(Long companyId, OfferRequest request) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        Resume resume = resumeRepository.findByIdWithMember(request.getResumeId())
            .orElseThrow(() -> new JikgongException(ErrorCode.RESUME_NOT_FOUND));
        Member worker = resume.getMember();

        List<Long> jobPostIdList = new ArrayList<>(); // 제안할 모집공고 id 리스트
        List<Long> workDateIdList = new ArrayList<>(); // 제안할 날짜 id 리스트

        for (OfferJobPostRequest offerJobPostRequest : request.getOfferJobPostRequest()) {
            jobPostIdList.add(offerJobPostRequest.getJobPostId());
            workDateIdList.addAll(offerJobPostRequest.getWorkDateIdList());
        }

        // jpa 1차 캐싱을 위한 조회 & workDate 임시 캐시 map 생성
        // key: jobPostId  |  value: WorkDate
        Map<Long, List<WorkDate>> workDateMap = getWorkDateMap(jobPostIdList, workDateIdList);

        List<Offer> offerList = new ArrayList<>();
        List<OfferWorkDate> offerWorkDateList = new ArrayList<>();
        List<Apply> applyList = new ArrayList<>();

        for (OfferJobPostRequest offerJobPostRequest : request.getOfferJobPostRequest()) {
            JobPost jobPost = jobPostRepository.findById(offerJobPostRequest.getJobPostId())
                .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));

            List<LocalDate> dateList = new ArrayList<>();

            // jobPostId 에 해당하는 workDateList 조회
            List<WorkDate> workDateEntityList = workDateMap.get(jobPost.getId());

            // 예외 체크 및 dateList 에 date 추가
            // 제안은 출역날 출역 시간 직전까지 가능
            validationAndAddDateList(offerJobPostRequest, workDateEntityList, dateList);

            // offer 엔티티 생성
            // offerWorkDate 엔티티 리스트 생성
            Offer offer = Offer.createEntity(company, worker, jobPost);
            offerList.add(offer);
            offerWorkDateList.addAll(OfferWorkDate.createEntityList(offer, workDateEntityList));

            // apply 엔티티 리스트 생성
            applyList.addAll(Apply.createEntityList(worker, workDateEntityList));

            // notification event 발행
            publisher.publishEvent(new NotificationEvent(company.getCompanyInfo().getCompanyName(), dateList,
                offerJobPostRequest.getJobPostId(), NotificationType.OFFER, worker.getId()));
        }

        offerRepository.saveAll(offerList);
        offerWorkDateRepository.saveAll(offerWorkDateList);
        applyRepository.saveAll(applyList);
    }

    // 예외 체크 및 dateList 에 date 추가
    private void validationAndAddDateList(OfferJobPostRequest offerJobPostRequest, List<WorkDate> workDateEntityList,
        List<LocalDate> dateList) {
        // 캐시 map 에서 조회한 workDateList 와 요청한 workDateIdList 와 크기가 다를 때
        if (workDateEntityList.size() != offerJobPostRequest.getWorkDateIdList().size()) {
            throw new JikgongException(ErrorCode.WORK_DATE_NOT_MATCH);
        }

        // 이미 지난 공고 인지 체크
        // 출역 시각 3시간 전까지 처리 가능
        for (WorkDate workDate : workDateEntityList) {
            if (LocalDate.now().isAfter(workDate.getDate())) {
                throw new JikgongException(ErrorCode.WORK_DATE_NEED_TO_FUTURE);
            }
            if (LocalDate.now().isEqual(workDate.getDate()) && LocalTime.now().plusHours(3L)
                .isAfter(workDate.getJobPost().getStartTime())) {
                throw new JikgongException(ErrorCode.WORK_DATE_NEED_TO_FUTURE);
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
     * 제안 기록 조회
     * 필터: 제안됨 or 제안 취소
     */
    @Transactional(readOnly = true)
    public Page<OfferHistoryResponse> findOfferHistory(Long companyId, OfferStatus offerStatus, Pageable pageable) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Page<Offer> offerHistoryPage = offerRepository.findOfferHistory(company.getId(), offerStatus, pageable);
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
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Offer offer = offerRepository.findByIdAndMember(company.getId(), offerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.OFFER_NOT_FOUND));

        List<OfferWorkDate> offerWorkDateList = offer.getOfferWorkDateList();

        // 제안 날짜 중 제안 대기중이지 않은 날짜가 있는지 체크
        for (OfferWorkDate offerWorkDate : offerWorkDateList) {
            if (offerWorkDate.getOfferWorkDateStatus() != OfferWorkDateStatus.OFFER_PENDING) {
                throw new JikgongException(ErrorCode.OFFER_CANT_CANCEL);
            }
        }

        // 제안 취소 (status 값 변경)
        offer.cancelOffer();
    }
}
